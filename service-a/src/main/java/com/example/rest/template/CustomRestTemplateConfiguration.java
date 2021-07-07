package com.example.rest.template;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomRestTemplateConfiguration {

	@LoadBalanced
	@Bean("customerRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
