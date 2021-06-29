package com.example.inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

	@Bean
	public SingletonBean singletonBean(@Autowired IBean iBean) {
		SingletonBean singletonBean = new SingletonBean();
		singletonBean.add(iBean);
		return singletonBean;
	}
}
