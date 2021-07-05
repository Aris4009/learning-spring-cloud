package com.example.filter;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import okhttp3.OkHttpClient;
import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.security.enable", havingValue = "true")
public class AuthenticationFilterConfig {

	private OkHttpClient okHttpClient;

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
	@RefreshScope
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

	@Bean
	public OkHttpClient httpClient(
			@Value("${spring.cloud.gateway.security.http-client.connection-timeout-second}") long connectionTimeout,
			@Value("${spring.cloud.gateway.security.http-client.read-timeout-second}") long readTimeout) {
		this.okHttpClient = new OkHttpClient().newBuilder().connectTimeout(connectionTimeout, TimeUnit.SECONDS)
				.readTimeout(readTimeout, TimeUnit.SECONDS).build();
		return okHttpClient;
	}

	@PreDestroy
	public void destroy() {
		if (this.okHttpClient != null) {
			this.okHttpClient.dispatcher().executorService().shutdown();
			this.okHttpClient.connectionPool().evictAll();
		}
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(@Autowired OkHttpClient okHttpClient) {
		return new RestTemplateBuilder().requestFactory(() -> {
			return new OkHttp3ClientHttpRequestFactory(okHttpClient);
		}).defaultHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE).build();
	}

	/**
	 * 全局认证过滤器
	 * 
	 * @param restTemplate
	 *            restTemplate
	 * @param whiteUrlList
	 *            白名单
	 * @param authenticationUrl
	 *            认证url
	 * @return 全局认证过滤器
	 */
	@Bean
	public AuthenticationFilter authenticationFilter(@Autowired RestTemplate restTemplate,
			@Autowired List<WhiteUrl> whiteUrlList, @Autowired AuthenticationUrl authenticationUrl) {
		return new AuthenticationFilter(restTemplate, whiteUrlList, authenticationUrl);
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

		private final RestTemplate restTemplate;

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

		public AuthenticationFilter(RestTemplate restTemplate, List<WhiteUrl> whiteUrlList,
				AuthenticationUrl authenticationUrl) {
			this.restTemplate = restTemplate;
			this.whiteUrlList = Objects.requireNonNullElseGet(whiteUrlList, ArrayList::new);
			this.authenticationUrl = authenticationUrl;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
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

			com.example.response.entity.Response<Void> httpResponse = authentication(exchange);
			if (httpResponse == null) {
				return serviceUnavailable(exchange);
			}
			if (httpResponse.getStatus() != HttpStatus.OK.value()) {
				return invalidToken(exchange);
			}
			return chain.filter(exchange.mutate().request(request).build());
		}

		/**
		 * 设置requestId
		 *
		 * @param exchange
		 *            ServerWebExchange
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
		 * @param exchange
		 *            ServerWebExchange
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
			com.example.response.entity.Response<Void> r = com.example.response.entity.Response.fail(
					HttpStatus.SERVICE_UNAVAILABLE.value(),
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
			com.example.response.entity.Response<Void> r = com.example.response.entity.Response
					.fail(INVALID_TOKEN_STATUS, INVALID_TOKEN);
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
		private com.example.response.entity.Response<Void> authentication(ServerWebExchange exchange) {
			com.example.response.entity.Response<Void> response = null;
			try {
				ServerHttpRequest request = exchange.getRequest();
				String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
				String xAuthTokenHeader = xAuthTokenHeader(exchange);
				String authorizationHeader = authorizationHeader(exchange);

				HttpHeaders headers = new HttpHeaders();
				headers.add(HEADER_X_AUTH_TOKEN, xAuthTokenHeader);
				headers.add(AUTHORIZATION_HEADER, authorizationHeader);
				headers.add(REQUEST_ID_HEADER, requestId);
				headers.add(TRACE_NO_HEADER, DEFAULT_TRACE_NO);

				Map<String, String> authParam = new HashMap<>();
				authParam.put("url", exchange.getRequest().getURI().getRawPath());
				HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(authParam), headers);

				ResponseEntity<String> responseEntity = this.restTemplate
						.postForEntity(this.authenticationUrl.getAuthenticateUrl(), httpEntity, String.class);
				response = JSONUtil.toBean(responseEntity.getBody(),
						new TypeReference<com.example.response.entity.Response<Void>>() {
						}, false);
				request.getHeaders().add(HEADER_X_AUTH_TOKEN, xAuthTokenHeader);
				request.getHeaders().add(AUTHORIZATION_HEADER, authorizationHeader);
				request.getHeaders().add(REQUEST_ID_HEADER, requestId);
				request.getHeaders().add(TRACE_NO_HEADER, responseEntity.getHeaders().getFirst(TRACE_NO_HEADER));
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
			return response;
		}

		@Override
		public int getOrder() {
			return ORDERED;
		}
	}
}
