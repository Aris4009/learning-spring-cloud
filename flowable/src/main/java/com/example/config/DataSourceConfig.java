package com.example.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import com.example.exception.BusinessException;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.lang.Snowflake;

@Configuration
public class DataSourceConfig {

	@Bean(name = "main", destroyMethod = "close")
	@ConditionalOnProperty(name = "spring.datasource.main.enable", havingValue = "true")
	public HikariDataSource dataSource(@Value("${spring.datasource.main.url}") final String url,
			@Value("${spring.datasource.main.username}") final String username,
			@Value("${spring.datasource.main.password}") final String password) {
		final HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setJdbcUrl(url);
		hikariDataSource.setUsername(username);
		hikariDataSource.setPassword(password);
		return hikariDataSource;
	}

	@Bean(name = "mainTx")
	@ConditionalOnBean(name = "main", value = DataSource.class)
	public TransactionManager tx(@Qualifier(value = "main") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	@ConditionalOnBean(name = "main", value = DataSource.class)
	public BeetlSqlDataSource beetlSqlDataSource(@Qualifier("main") DataSource dataSource) {
		BeetlSqlDataSource beetlSqlDataSource = new BeetlSqlDataSource();
		beetlSqlDataSource.setMasterSource(dataSource);
		return beetlSqlDataSource;
	}

	@Bean(name = "sqlManagerFactoryBean")
	@ConditionalOnBean(value = BeetlSqlDataSource.class)
	public SqlManagerFactoryBean sqlManagerFactoryBean(@Autowired BeetlSqlDataSource beetlSqlDataSource,
			@Value("${spring.datasource.main.beetlsql.base.package}") final String basePackage,
			@Value("${spring.datasource.main.beetlsql.dao.suffix}") final String daoSuffix) {
		SqlManagerFactoryBean sqlManagerFactoryBean = new SqlManagerFactoryBean();
		sqlManagerFactoryBean.setCs(beetlSqlDataSource);
		sqlManagerFactoryBean.setDbStyle(new MySqlStyle());
		sqlManagerFactoryBean.setSqlLoader(new ClasspathLoader("/sql"));
		sqlManagerFactoryBean.setNc(new UnderlinedNameConversion());
		sqlManagerFactoryBean.setInterceptors(new Interceptor[]{new DebugInterceptor()});
		Properties properties = new Properties();
		properties.setProperty("daoSuffix", daoSuffix);
		properties.setProperty("basePackage", basePackage);
		properties.setProperty("sqlManagerFactoryBeanName", "sqlManagerFactoryBean");
		sqlManagerFactoryBean.setExtProperties(properties);
		return sqlManagerFactoryBean;
	}

	@Bean
	@ConditionalOnClass(value = SqlManagerFactoryBean.class)
	public SQLManager sqlManager(@Autowired SqlManagerFactoryBean sqlManagerFactoryBean,
			@Value("${spring.datasource.main.beetlsql.worker.id}") long workerId,
			@Value("${spring.datasource.main.beetlsql.data.center.id}") long dataCenterId) throws Exception {
		SQLManager sqlManager = sqlManagerFactoryBean.getObject();
		if (sqlManager == null) {
			throw new BusinessException("sqlManager is null");
		}
		Snowflake snowflake = new Snowflake(workerId, dataCenterId);
		sqlManager.addIdAutonGen("snow", snow -> snowflake.nextId());
		return sqlManager;
	}

	@Bean(name = "flowable", destroyMethod = "close")
	@ConditionalOnProperty(name = "spring.datasource.flowable.enable", havingValue = "true")
	public HikariDataSource flowableDataSource(@Value("${spring.datasource.flowable.url}") final String url,
			@Value("${spring.datasource.flowable.username}") final String username,
			@Value("${spring.datasource.flowable.password}") final String password) {
		final HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setJdbcUrl(url);
		hikariDataSource.setUsername(username);
		hikariDataSource.setPassword(password);
		return hikariDataSource;
	}

	@Bean(name = "flowableTx")
	@ConditionalOnBean(name = "flowable", value = DataSource.class)
	public DataSourceTransactionManager flowTx(@Qualifier(value = "flowable") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
