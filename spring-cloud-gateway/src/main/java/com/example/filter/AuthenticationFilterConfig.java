package com.example.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
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
	public AuthenticationUrl authenticationUrl(@Value("${spring.cloud.gateway.security.verify-url}") String verifyUrl,
			@Value("${spring.cloud.gateway.security.refresh-url}") String refreshUrl,
			@Value("${spring.cloud.gateway.security.login-url}") String loginUrl,
			@Value("${spring.cloud.gateway.security.logout-url}") String logoutUrl) {
		return new AuthenticationUrl(verifyUrl, refreshUrl, loginUrl, logoutUrl);
	}

	/**
	 * 全局认证过滤器
	 * 
	 * @param httpClient
	 * @param whiteUrlList
	 * @param authenticationUrl
	 * @param stripPrefix
	 * @return
	 */
	@Bean
	public GlobalFilter authenticationFilter(@Autowired HttpClient httpClient, @Autowired List<WhiteUrl> whiteUrlList,
			@Autowired AuthenticationUrl authenticationUrl,
			@Value("${spring.cloud.gateway.default-filters[0]:StripPrefix=1}") String stripPrefix) {
		int part = Integer.parseInt(stripPrefix.split("=")[1]);
		AuthenticationFilter.Config config = new AuthenticationFilter.Config();
		config.setParts(part);
		return new AuthenticationFilter(httpClient, config, whiteUrlList, authenticationUrl);
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

		private final String verifyUrl;

		private final String refreshUrl;

		private final String loginUrl;

		private final String logoutUrl;

		public AuthenticationUrl(String verifyUrl, String refreshUrl, String loginUrl, String logoutUrl) {
			this.verifyUrl = verifyUrl;
			this.refreshUrl = refreshUrl;
			this.loginUrl = loginUrl;
			this.logoutUrl = logoutUrl;
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

		private final HttpClient httpClient;

		private final Config config;

		private final List<WhiteUrl> whiteUrlList;

		private final AuthenticationUrl authenticationUrl;

		private static final int ORDERED = -1;

		private static final AntPathMatcher MATCHER = new AntPathMatcher();

		private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

		private static final String AUTHORIZATION_HEADER = "Authorization";

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		public AuthenticationFilter(HttpClient httpClient, Config config, List<WhiteUrl> whiteUrlList,
				AuthenticationUrl authenticationUrl) {
			this.httpClient = httpClient;
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
			ServerHttpRequest request = exchange.getRequest();
			if (checkScheme(exchange) || checkWhiteUrl(exchange)) {
				return chain.filter(exchange);
			}

			if (checkHeaders(exchange)) {

			}

			return chain.filter(exchange);

		}

		private boolean checkScheme(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			String scheme = request.getURI().getScheme();
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				flag = true;
			}
			return flag;
		}

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

		private boolean checkHeaders(ServerWebExchange exchange) {
			boolean flag = false;
			ServerHttpRequest request = exchange.getRequest();
			if (StrUtil.isBlankIfStr(request.getHeaders().getFirst(HEADER_X_AUTH_TOKEN))
					|| StrUtil.isBlankIfStr(request.getHeaders().getFirst(AUTHORIZATION_HEADER))) {
				flag = true;
			}
			return flag;
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
	}
}
