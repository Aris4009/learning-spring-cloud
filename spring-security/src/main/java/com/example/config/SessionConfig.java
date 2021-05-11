package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@Import(SessionAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.redis.enable", havingValue = "true")
public class SessionConfig {
}
