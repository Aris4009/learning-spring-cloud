package com.example.config;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.*;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.flowable.enable", havingValue = "true")
public class FlowableConfig {

	@Bean
	@DependsOn(value = {"flowable", "flowableTx"})
	public SpringProcessEngineConfiguration springProcessEngineConfiguration(
			@Qualifier("flowable") DataSource dataSource,
			@Qualifier("flowableTx") DataSourceTransactionManager transactionManager) {
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setDataSource(dataSource);
		configuration.setDatabaseType("mysql");
		configuration.setTransactionManager(transactionManager);
		configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		configuration.setAsyncExecutorActivate(false);
		return configuration;
	}

	@Bean
	@DependsOn(value = {"springProcessEngineConfiguration"})
	public ProcessEngine processEngine(@Autowired SpringProcessEngineConfiguration springProcessEngineConfiguration)
			throws Exception {
		ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
		processEngineFactoryBean.setProcessEngineConfiguration(springProcessEngineConfiguration);
		return processEngineFactoryBean.getObject();
	}

	@Bean
	@DependsOn(value = {"processEngine"})
	public RepositoryService repositoryService(@Autowired ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}

	@Bean
	@DependsOn(value = {"processEngine"})
	public RuntimeService runtimeService(@Autowired ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}

	@Bean
	@DependsOn(value = {"processEngine"})
	public FormService formService(@Autowired ProcessEngine processEngine) {
		return processEngine.getFormService();
	}

	@Bean
	@DependsOn(value = {"processEngine"})
	public TaskService taskService(@Autowired ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}

	@Bean
	@ConditionalOnClass(ProcessEngine.class)
	public HistoryService historyService(@Autowired ProcessEngine processEngine) {
		return processEngine.getHistoryService();
	}

	@Bean
	@ConditionalOnClass(ProcessEngine.class)
	public IdentityService identityService(@Autowired ProcessEngine processEngine) {
		return processEngine.getIdentityService();
	}

	@Bean
	@ConditionalOnClass(ProcessEngine.class)
	public ManagementService managementService(@Autowired ProcessEngine processEngine) {
		return processEngine.getManagementService();
	}

	@Bean
	@ConditionalOnClass(ProcessEngine.class)
	public DynamicBpmnService dynamicBpmnService(@Autowired ProcessEngine processEngine) {
		return processEngine.getDynamicBpmnService();
	}

	@Bean
	@ConditionalOnClass(ProcessEngine.class)
	public ProcessMigrationService processMigrationService(@Autowired ProcessEngine processEngine) {
		return processEngine.getProcessMigrationService();
	}
}
