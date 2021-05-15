package com.example.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * 全局认证过滤器
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.security.enable", havingValue = "true")
public class AuthenticationFilterConfig {

	/**
	 * 白名单
	 * 
	 * @param path 白名单配置地址
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
	 * @param authenticateUrl 鉴权地址
	 * @param verifyUrl       校验令牌地址
	 * @param refreshUrl      刷新令牌地址
	 * @param loginUrl        登录地址
	 * @param logoutUrl       注销地址
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

	/**
	 * 全局认证过滤器
	 * 
	 * @param whiteUrlList      白名单
	 * @param authenticationUrl 认证url
	 * @param stripPrefix       剥离前缀
	 * @return 全局认证过滤器
	 */
	@Bean
	public GlobalFilter authenticationFilter(@Autowired List<WhiteUrl> whiteUrlList,
			@Autowired AuthenticationUrl authenticationUrl,
			@Value("${spring.cloud.gateway.default-filters[0]:StripPrefix=1}") String stripPrefix) {
		int part = Integer.parseInt(stripPrefix.split("=")[1]);
		AuthenticationFilter.Config config = new AuthenticationFilter.Config();
		config.setParts(part);
		return new AuthenticationFilter(config, whiteUrlList, authenticationUrl);
	}

	@Bean
	public WebClient webClient(@Autowired HttpClient httpClient) {
		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}

	static class WhiteUrl {

		private String name;

		private String url;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

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

		public String getAuthenticateUrl() {
			return authenticateUrl;
		}

		public String getVerifyUrl() {
			return verifyUrl;
		}

		public String getRefreshUrl() {
			return refreshUrl;
		}

		public String getLoginUrl() {
			return loginUrl;
		}

		public String getLogoutUrl() {
			return logoutUrl;
		}
	}

	static class AuthenticationFilter implements GlobalFilter, Ordered {

		private final Config config;

		private final List<WhiteUrl> whiteUrlList;

		private final AuthenticationUrl authenticationUrl;

		private static final int ORDERED = -1;

		private static final AntPathMatcher MATCHER = new AntPathMatcher();

		private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

		private static final String AUTHORIZATION_HEADER = "Authorization";

		private static final String SERVICE_ID_HEADER = "serviceId";

		private static final String UNKNOWN_SERVICE_ID_HEADER = "unknown";

		private static final String REQUEST_ID_HEADER = "requestId";

		private static final String INVALID_TOKEN = "invalid token";

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		public AuthenticationFilter(Config config, List<WhiteUrl> whiteUrlList, AuthenticationUrl authenticationUrl) {
			this.config = config;
			if (whiteUrlList == null) {
				this.whiteUrlList = new ArrayList<>();
			} else {
				this.whiteUrlList = whiteUrlList;
			}
			this.authenticationUrl = authenticationUrl;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			setServiceId(exchange);
			setRequestId(exchange);
			ServerHttpRequest request = exchange.getRequest();
			// 非http或https请求，不执行鉴权
			if (checkScheme(exchange) || checkWhiteUrl(exchange)) {
				log.debug("url {} skip authenticate", request.getURI().getRawPath());
				return chain.filter(exchange);
			}

			// 不包含鉴权请求头，直接返回407，表示无效的token
			if (checkHeaders(exchange)) {
				log.debug("url {} invalid token or session", request.getURI().getRawPath());
				return invalidToken(exchange);
			}

			int code = authentication(exchange);
			if (code == 200) {
				String xAuthTokenHeader = xAuthTokenHeader(exchange);
				String authorizationHeader = authorizationHeader(exchange);
				request = exchange.getRequest().mutate().headers(httpHeaders -> {
					httpHeaders.add(HEADER_X_AUTH_TOKEN, xAuthTokenHeader);
					httpHeaders.add(AUTHORIZATION_HEADER, authorizationHeader);
				}).build();
				return chain.filter(exchange.mutate().request(request).build());
			} else {
				return invalidToken(exchange);
			}
		}

		/**
		 * 设置serviceId
		 * 
		 * @param exchange ServerWebExchange
		 */
		private void setServiceId(ServerWebExchange exchange) {
			Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
			String serviceId;
			if (route == null) {
				serviceId = UNKNOWN_SERVICE_ID_HEADER;
			} else {
				serviceId = route.getId();
			}
			ServerHttpRequest request = exchange.getRequest();
			request = exchange.getRequest().mutate().headers(httpHeaders -> {
				httpHeaders.add(SERVICE_ID_HEADER, serviceId);
			}).build();
			exchange.mutate().request(request).build();
		}

		/**
		 * 设置requestId
		 *
		 * @param exchange ServerWebExchange
		 */
		private void setRequestId(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			String requestId = UUID.randomUUID().toString().replace("-", "");
			request = exchange.getRequest().mutate().headers(httpHeaders -> {
				httpHeaders.add(REQUEST_ID_HEADER, requestId);
			}).build();
			exchange.mutate().request(request).build();
		}

		/**
		 * 检查非http/https请求
		 * 
		 * @param exchange ServerWebExchange
		 * @return true:非http/https false:http/https请求
		 */
		private boolean checkScheme(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			String scheme = request.getURI().getScheme();
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				flag = true;
			}
			return flag;
		}

		/**
		 * 检查原始请求是否在白名单中
		 * 
		 * @param exchange ServerWebExchange
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
		 * @param exchange ServerWebExchange
		 * @return true:两个请求头至少缺少一个 false:两个请求头都包含
		 */
		private boolean checkHeaders(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			if (StrUtil.isBlankIfStr(xAuthTokenHeader(exchange))
					|| StrUtil.isBlankIfStr(authorizationHeader(exchange))) {
				flag = true;
			}
			return flag;
		}

		/**
		 * 无效token响应，http响应码:407
		 * 
		 * @param exchange ServerWebExchange
		 * @return 无效token响应
		 */
		private Mono<Void> invalidToken(ServerWebExchange exchange) {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			Response<Void> r = Response.fail(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value(), INVALID_TOKEN);
			return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(r).getBytes())));
		}

		/**
		 * 获取sessionId
		 * 
		 * @param exchange ServerWebExchange
		 * @return 获取sessionId
		 */
		private String xAuthTokenHeader(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			return request.getHeaders().getFirst(HEADER_X_AUTH_TOKEN);
		}

		/**
		 * 获取token
		 * 
		 * @param exchange ServerWebExchange
		 * @return 获取token
		 */
		private String authorizationHeader(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			return request.getHeaders().getFirst(AUTHORIZATION_HEADER);
		}

		/**
		 * 鉴权
		 *
		 * @return 鉴权结果
		 */
		private int authentication(ServerWebExchange exchange) {
			int code;
			try {
				ServerHttpRequest request = exchange.getRequest();
				String serviceId = request.getHeaders().getFirst(SERVICE_ID_HEADER);
				String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
				String xAuthTokenHeader = xAuthTokenHeader(exchange);
				String authorizationHeader = authorizationHeader(exchange);
				Map<String, String> authParam = new HashMap<>();
				authParam.put("url", exchange.getRequest().getURI().getRawPath());
				code = HttpRequest.post(this.authenticationUrl.getAuthenticateUrl())
						.header(HEADER_X_AUTH_TOKEN, xAuthTokenHeader).header(AUTHORIZATION_HEADER, authorizationHeader)
						.header(SERVICE_ID_HEADER, serviceId).header(REQUEST_ID_HEADER, requestId)
						.body(JSONUtil.toJsonStr(authParam)).execute().getStatus();
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				code = HttpStatus.SERVICE_UNAVAILABLE.value();
			}
			return code;
		}

		@Override
		public int getOrder() {
			return ORDERED;
		}

		public static class Config {

			private int parts;

			public int getParts() {
				return parts;
			}

			public void setParts(int parts) {
				this.parts = parts;
			}

		}

		public static class Response<T> {

			private T data;

			private String path;

			private String message;

			private int status;

			private String timestamp;

			public T getData() {
				return data;
			}

			public void setData(T data) {
				this.data = data;
			}

			public String getPath() {
				return path;
			}

			public void setPath(String path) {
				this.path = path;
			}

			public String getMessage() {
				return message;
			}

			public void setMessage(String message) {
				this.message = message;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			public String getTimestamp() {
				return timestamp;
			}

			public void setTimestamp(String timestamp) {
				this.timestamp = timestamp;
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public static <T> Response<T> fail(T data, int status, String... msg) {
				String s;
				if (!StrUtil.isEmptyIfStr(msg)) {
					StringBuilder builder = new StringBuilder();
					for (String m : msg) {
						builder.append(m);
					}
					s = builder.toString();
				} else {
					s = "fail";
				}
				Response response = new Response();
				response.setData(data);
				response.setMessage(s);
				response.setStatus(status);
				response.setTimestamp(LocalDateTime.now().toString());
				return (Response<T>) response;
			}

			public static Response<Void> fail(int status, String... msg) {
				return fail(null, status, msg);
			}
		}
	}
}
