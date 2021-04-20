package com.example.config;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class FlowableConfig {

	@Bean
	@ConditionalOnBean(name = "flowable", value = DataSource.class)
	public SpringProcessEngineConfiguration springProcessEngineConfiguration(
			@Qualifier("flowable") DataSource dataSource,
			@Qualifier("flowableTx") DataSourceTransactionManager transactionManager) {
		SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
		springProcessEngineConfiguration.setDataSource(dataSource);
		springProcessEngineConfiguration.setTransactionManager(transactionManager);
		springProcessEngineConfiguration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		return springProcessEngineConfiguration;
	}

	@Bean
	@ConditionalOnClass(value = SpringProcessEngineConfiguration.class)
	public ProcessEngineFactoryBean processEngineFactoryBean(
			@Autowired SpringProcessEngineConfiguration springProcessEngineConfiguration) {
		ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
		processEngineFactoryBean.setProcessEngineConfiguration(springProcessEngineConfiguration);
		return processEngineFactoryBean;
	}
}
