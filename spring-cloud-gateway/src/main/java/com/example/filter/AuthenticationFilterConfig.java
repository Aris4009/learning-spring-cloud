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
import cn.hutool.json.JSONUtil;
import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.security.enable", havingValue = "true")
public class AuthenticationFilterConfig {

	@Bean
	public List<WhiteUrl> whiteUrlList(@Value("${spring.cloud.gateway.security.white-url-list-path}") String path) {
		ClassPathResource classPathResource = new ClassPathResource(path);
		String content = classPathResource.readUtf8Str();
		return JSONUtil.toList(content, WhiteUrl.class);
	}

	@Bean
	public AuthenticationUrl authenticationUrl(@Value("${spring.cloud.gateway.security.verify-url}") String verifyUrl,
			@Value("${spring.cloud.gateway.security.refresh-url}") String refreshUrl,
			@Value("${spring.cloud.gateway.security.login-url}") String loginUrl,
			@Value("${spring.cloud.gateway.security.logout-url}") String logoutUrl) {
		return new AuthenticationUrl(verifyUrl, refreshUrl, loginUrl, logoutUrl);
	}

	@Bean
	public GlobalFilter authenticationFilter(@Autowired List<WhiteUrl> whiteUrlList,
			@Autowired AuthenticationUrl authenticationUrl,
			@Value("${spring.cloud.gateway.default-filters[0]:StripPrefix=1}") String stripPrefix) {
		int part = Integer.parseInt(stripPrefix.split("=")[1]);
		AuthenticationFilter.Config config = new AuthenticationFilter.Config();
		config.setParts(part);
		return new AuthenticationFilter(config, whiteUrlList, authenticationUrl);
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

		private final Config config;

		private final List<WhiteUrl> whiteUrlList;

		private final AuthenticationUrl authenticationUrl;

		private static final int ORDERED = -1;

		private static final AntPathMatcher MATCHER = new AntPathMatcher();

		private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

		private static final String AUTHORIZATION_HEADER = "Authorization";

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
			ServerHttpRequest request = exchange.getRequest();
			String scheme = request.getURI().getScheme();
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				return chain.filter(exchange);
			}

			String path = request.getURI().getRawPath();
			boolean flag = false;
			for (WhiteUrl whiteUrl : this.whiteUrlList) {
				if (MATCHER.match(whiteUrl.getUrl(), path)) {
					flag = true;
					break;
				}
			}
			if (flag) {
				return chain.filter(exchange);
			}

			// TODO
			return chain.filter(exchange);

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

	public static void main(String[] args) {
		String pathPattern = "/service/**";
		String path = "/services/api/aa";
		System.out.println(new AntPathMatcher().match(pathPattern, path));
	}
}
