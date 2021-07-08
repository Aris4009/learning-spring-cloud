package com.example.filter;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.response.entity.Response;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * 全局认证过滤器
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.security.enable", havingValue = "true")
@RefreshScope
public class AuthenticationFilterConfig {

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String CONTENT_TYPE_VALUE = "application/json";

	/**
	 * 白名单
	 * 
	 * @param path
	 *            白名单配置地址
	 * @return 白名单列表
	 */
	@Bean
	public List<WhiteUrl> whiteUrlList(@Value("${spring.cloud.gateway.security.white-url-list-path}") String path) {
		ClassPathResource classPathResource = new ClassPathResource(path);
		String content = classPathResource.readUtf8Str();
		return JSONUtil.toList(content, WhiteUrl.class);
	}

	/**
	 * 认证地址
	 * 
	 * @param authenticateUrl
	 *            鉴权地址
	 * @param verifyUrl
	 *            校验令牌地址
	 * @param refreshUrl
	 *            刷新令牌地址
	 * @param loginUrl
	 *            登录地址
	 * @param logoutUrl
	 *            注销地址
	 * @return 认证地址
	 */
	@Bean
	public AuthenticationUrl authenticationUrl(
			@Value("${spring.cloud.gateway.security.authenticate-url}") String authenticateUrl,
			@Value("${spring.cloud.gateway.security.verify-url}") String verifyUrl,
			@Value("${spring.cloud.gateway.security.refresh-url}") String refreshUrl,
			@Value("${spring.cloud.gateway.security.login-url}") String loginUrl,
			@Value("${spring.cloud.gateway.security.logout-url}") String logoutUrl) {
		return new AuthenticationUrl(authenticateUrl, verifyUrl, refreshUrl, loginUrl, logoutUrl);
	}

	@Bean("client")
	public WebClient webClient(
			@Value("${spring.cloud.gateway.security.http-client.connection-timeout-second}") int connectionTimeout,
			@Value("${spring.cloud.gateway.security.http-client.read-timeout-second}") int readTimeout,
			@Autowired ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {
		HttpClient client = HttpClient.create().headers(headers -> headers.add(CONTENT_TYPE, CONTENT_TYPE_VALUE))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout * 1000)
				.doOnConnected(con -> con.addHandler(new ReadTimeoutHandler(readTimeout, TimeUnit.SECONDS)));
		return WebClient.builder().filter(reactorLoadBalancerExchangeFilterFunction)
				.clientConnector(new ReactorClientHttpConnector(client)).build();
	}

	/**
	 * 全局认证过滤器
	 * 
	 * @param webClient
	 *            webClient
	 * @param whiteUrlList
	 *            白名单
	 * @param authenticationUrl
	 *            认证url
	 * @return 全局认证过滤器
	 */
	@Bean
	public AuthenticationFilter authenticationFilter(@Qualifier("client") WebClient webClient,
			@Autowired List<WhiteUrl> whiteUrlList, @Autowired AuthenticationUrl authenticationUrl) {
		return new AuthenticationFilter(webClient, whiteUrlList, authenticationUrl);
	}

	@Data
	static class WhiteUrl {

		private String name;

		private String url;
	}

	@Getter
	static class AuthenticationUrl {

		private final String authenticateUrl;

		private final String verifyUrl;

		private final String refreshUrl;

		private final String loginUrl;

		private final String logoutUrl;

		public AuthenticationUrl(String authenticateUrl, String verifyUrl, String refreshUrl, String loginUrl,
				String logoutUrl) {
			this.authenticateUrl = authenticateUrl;
			this.verifyUrl = verifyUrl;
			this.refreshUrl = refreshUrl;
			this.loginUrl = loginUrl;
			this.logoutUrl = logoutUrl;
		}
	}

	static class AuthenticationFilter implements GlobalFilter, Ordered {

		private final WebClient webClient;

		private final List<WhiteUrl> whiteUrlList;

		private final AuthenticationUrl authenticationUrl;

		private static final int ORDERED = -1;

		private static final AntPathMatcher MATCHER = new AntPathMatcher();

		private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

		private static final String AUTHORIZATION_HEADER = "Authorization";

		private static final String REQUEST_ID_HEADER = "requestId";

		private static final String TRACE_NO_HEADER = "traceNo";

		private static final String DEFAULT_TRACE_NO = "0";

		private static final String INVALID_TOKEN = "invalid token";

		public static final int INVALID_TOKEN_STATUS = 4001;

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		public AuthenticationFilter(WebClient webClient, List<WhiteUrl> whiteUrlList,
				AuthenticationUrl authenticationUrl) {
			this.webClient = webClient;
			this.whiteUrlList = Objects.requireNonNullElseGet(whiteUrlList, ArrayList::new);
			this.authenticationUrl = authenticationUrl;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Mono<Void> filter(ServerWebExchange ex, GatewayFilterChain chain) {
			ServerWebExchange exchange = setRequestId(ex);
			ServerHttpRequest request = exchange.getRequest();
			// 非http或https请求，不执行鉴权
			if (!checkScheme(exchange) || checkWhiteUrl(exchange)) {
				log.debug("url {} skip authenticate", request.getURI().getRawPath());
				return chain.filter(exchange);
			}

			// 不包含鉴权请求头，直接返回407，表示无效的token
			if (checkHeaders(exchange)) {
				log.debug("url {} invalid token or session", request.getURI().getRawPath());
				return invalidToken(exchange);
			}

			Mono<Response> mono = authentication(exchange);
			return mono.flatMap((httpResponse -> {
				if (httpResponse == null) {
					return serviceUnavailable(exchange);
				}
				if (httpResponse.getStatus() != HttpStatus.OK.value()) {
					return invalidToken(exchange);
				}
				ServerHttpRequest passedRequest = request.mutate().headers((headers) -> {
					headers.add(HEADER_X_AUTH_TOKEN, httpResponse.getHeaders().getFirst(HEADER_X_AUTH_TOKEN));
					headers.add(AUTHORIZATION_HEADER, httpResponse.getHeaders().getFirst(AUTHORIZATION_HEADER));
					headers.add(REQUEST_ID_HEADER, httpResponse.getHeaders().getFirst(REQUEST_ID_HEADER));
					headers.add(TRACE_NO_HEADER, httpResponse.getHeaders().getFirst(TRACE_NO_HEADER));
				}).build();
				return chain.filter(exchange.mutate().request(passedRequest).build());
			}));
		}

		/**
		 * 设置requestId
		 * 
		 * @param exchange
		 *            exchange
		 * @return ServerWebExchange
		 */
		private ServerWebExchange setRequestId(ServerWebExchange exchange) {
			String requestId = UUID.randomUUID().toString().replace("-", "");
			ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
				httpHeaders.add(REQUEST_ID_HEADER, requestId);
			}).build();
			return exchange.mutate().request(request).build();
		}

		/**
		 * 检查http/https请求
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return true:http/https false:非http/https请求
		 */
		private boolean checkScheme(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			String scheme = request.getURI().getScheme();
			if ("http".equals(scheme) || "https".equals(scheme)) {
				flag = true;
			}
			return flag;
		}

		/**
		 * 检查原始请求是否在白名单中
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return true:在白名单 false:不在白名单
		 */
		private boolean checkWhiteUrl(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			String path = request.getURI().getRawPath();
			for (WhiteUrl whiteUrl : this.whiteUrlList) {
				if (MATCHER.match(whiteUrl.getUrl(), path)) {
					flag = true;
					break;
				}
			}
			return flag;
		}

		/**
		 * 检查请求头中是否包含X-Auth-Token、Authorization
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return true:两个请求头至少缺少一个 false:两个请求头都包含
		 */
		private boolean checkHeaders(ServerWebExchange exchange) {
			return StrUtil.isBlankIfStr(xAuthTokenHeader(exchange))
					|| StrUtil.isBlankIfStr(authorizationHeader(exchange));
		}

		/**
		 * 鉴权网关无响应
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return 鉴权网关无响应
		 */
		private Mono<Void> serviceUnavailable(ServerWebExchange exchange) {
			ServerHttpResponse response = exchange.getResponse();
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			Response<Void> r = Response.fail(HttpStatus.SERVICE_UNAVAILABLE.value(),
					HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase().toLowerCase());
			return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(r).getBytes())));
		}

		/**
		 * 无效token响应，http响应码:407
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return 无效token响应
		 */
		private Mono<Void> invalidToken(ServerWebExchange exchange) {
			ServerHttpResponse response = exchange.getResponse();
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			Response<Void> r = Response.fail(INVALID_TOKEN_STATUS, INVALID_TOKEN);
			return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(r).getBytes())));
		}

		/**
		 * 获取sessionId
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return 获取sessionId
		 */
		private String xAuthTokenHeader(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			return request.getHeaders().getFirst(HEADER_X_AUTH_TOKEN);
		}

		/**
		 * 获取token
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return 获取token
		 */
		private String authorizationHeader(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			return request.getHeaders().getFirst(AUTHORIZATION_HEADER);
		}

		/**
		 * 鉴权
		 * 
		 * @param exchange
		 *            ServerWebExchange
		 * @return 请求结果
		 */
		@SuppressWarnings("unchecked")
		private Mono<Response> authentication(ServerWebExchange exchange) {
			Mono<Response> res;
			try {
				ServerHttpRequest request = exchange.getRequest();
				String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
				String xAuthTokenHeader = xAuthTokenHeader(exchange);
				String authorizationHeader = authorizationHeader(exchange);

				Map<String, String> authParam = new HashMap<>();
				authParam.put("url", exchange.getRequest().getURI().getRawPath());

				res = this.webClient.post().uri(this.authenticationUrl.getAuthenticateUrl()).headers((headers -> {
					headers.add(HEADER_X_AUTH_TOKEN, xAuthTokenHeader);
					headers.add(AUTHORIZATION_HEADER, authorizationHeader);
					headers.add(REQUEST_ID_HEADER, requestId);
					headers.add(TRACE_NO_HEADER, DEFAULT_TRACE_NO);
				})).bodyValue(authParam).exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(Response.class).map(r -> {
							HttpHeaders headers = new HttpHeaders();
							headers.set(HEADER_X_AUTH_TOKEN, xAuthTokenHeader);
							headers.set(AUTHORIZATION_HEADER, authorizationHeader);
							headers.set(REQUEST_ID_HEADER, requestId);
							headers.set(TRACE_NO_HEADER, response.headers().asHttpHeaders().getFirst(TRACE_NO_HEADER));
							r.setHeaders(headers);
							return r;
						});
					} else {
						return response.createException().map(map -> Response.fail(map.getCause().getMessage()));
					}
				});
			} catch (Exception e) {
				log.info("error msg:{},ex:{}", e.getMessage(), e);
				res = Mono.just(Response.fail("can't access resource " + this.authenticationUrl.getAuthenticateUrl()));
			}
			return res;
		}

		@Override
		public int getOrder() {
			return ORDERED;
		}
	}
}
