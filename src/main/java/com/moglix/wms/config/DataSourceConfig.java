package com.moglix.wms.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.jdbc-url}")
	private String jdbcUrl;
	
	@Value("${spring.datasource.username}")
	private String userName;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.validationQuery}")
	private String validationQuery;
	
	@Bean(name = "wmsDatasource")
	@ConfigurationProperties("spring.datasource")
	@Primary
	public DataSource dataSource() {
		HikariDataSource datasource  = DataSourceBuilder.create().type(HikariDataSource.class).build();
		
		datasource.setJdbcUrl(jdbcUrl);
		datasource.setUsername(userName);
		datasource.setPassword(password);
		datasource.setConnectionTestQuery(validationQuery);
		datasource.setMaximumPoolSize(12);
		datasource.setIdleTimeout(20000);
		datasource.setLeakDetectionThreshold(60000);
		datasource.setPoolName("wms");
		
		return datasource;
	}

	@Bean
	@Qualifier("enterpriseDataSource")
	@ConfigurationProperties(prefix = "enterprise.datasource")
	DataSource enterpriseDataSource() {
		return DataSourceBuilder.create().build();
	}

}
