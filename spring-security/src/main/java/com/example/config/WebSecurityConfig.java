package com.example.config;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

	private final boolean enable;

	public WebSecurityConfig(@Value("${spring.security.enable:false}") boolean enable) {
		this.enable = enable;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(
			@Value("${spring.security.cors.allowed-origins:*}") List<String> allowedOrigins,
			@Value("${spring.security.cors.allowed-methods:get,post}") List<String> allowedMethods,
			@Value("${spring.security.cors.path-pattern:/api/**}") String pathPattern) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(allowedOrigins);
		configuration.setAllowedMethods(allowedMethods.stream().map(String::toUpperCase).collect(Collectors.toList()));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(pathPattern, configuration);
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults());
		if (this.enable) {
			super.configure(http);
		} else {
			http.authorizeRequests(request -> request.anyRequest().permitAll());
		}
	}
}
