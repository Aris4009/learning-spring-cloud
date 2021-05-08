package com.example.config;

import javax.sql.DataSource;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring.BeetlSqlScannerConfigurer;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.beetl.sql.ext.spring.SqlManagerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.lang.Snowflake;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.enable", havingValue = "true")
public class DataSourceConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String url;

	@Bean
	public DataSource dataSource(@Value("${spring.datasource.url}") final String url,
			@Value("${spring.datasource.username}") final String username,
			@Value("${spring.datasource.password}") final String password) {
		final HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setJdbcUrl(url);
		hikariDataSource.setUsername(username);
		hikariDataSource.setPassword(password);
		this.url = url;
		return hikariDataSource;
	}

	@Bean("tx")
	public TransactionManager tx(@Autowired DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlManagerFactoryBean sqlManagerFactoryBean(@Autowired DataSource dataSource) {
		SqlManagerFactoryBean sqlManagerFactoryBean = new SqlManagerFactoryBean();
		sqlManagerFactoryBean.setDbStyle(new MySqlStyle());
		sqlManagerFactoryBean.setSqlLoader(new MarkdownClasspathLoader("sql"));
		sqlManagerFactoryBean.setNc(new UnderlinedNameConversion());
		sqlManagerFactoryBean.setInterceptors(new Interceptor[]{new DebugInterceptor()});
		SpringConnectionSource springConnectionSource = new SpringConnectionSource();
		springConnectionSource.setMasterSource(dataSource);
		sqlManagerFactoryBean.setCs(springConnectionSource);
		return sqlManagerFactoryBean;
	}

	@Bean
	public SQLManager sqlManager(@Autowired SqlManagerFactoryBean sqlManagerFactoryBean,
			@Value("${spring.datasource.beetlsql.worker.id}") long workerId,
			@Value("${spring.datasource.beetlsql.data.center.id}") long dataCenterId) throws Exception {
		SQLManager sqlManager = sqlManagerFactoryBean.getObject();
		Snowflake snowflake = new Snowflake(workerId, dataCenterId);
		sqlManager.addIdAutoGen("snow", params -> snowflake.nextId());
		log.info("connection mysql url {} success", this.url);
		return sqlManager;
	}

	@Bean
	public BeetlSqlScannerConfigurer beetlSqlScannerConfigurer() {
		BeetlSqlScannerConfigurer beetlSqlScannerConfigurer = new BeetlSqlScannerConfigurer();
		// 该类实现了BeanDefinitionRegistryPostProcessor，无法使用占位符来获取配置
		beetlSqlScannerConfigurer.setDaoSuffix("Dao");
		beetlSqlScannerConfigurer.setBasePackage("com.example.dao");
		beetlSqlScannerConfigurer.setSqlManagerFactoryBeanName("sqlManagerFactoryBean");
		return beetlSqlScannerConfigurer;
	}
}
