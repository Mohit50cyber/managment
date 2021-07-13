package com.moglix.wms.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "wmsEntityManagerFactory",
        transactionManagerRef = "wmsTransactionManager",
        basePackages = "com.moglix.wms.repository"
)

@EnableTransactionManagement
public class WMSDsConfig {
	@Primary
    @Bean(name = "wmsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(final EntityManagerFactoryBuilder builder,
                                                                            final @Qualifier("wmsDatasource") DataSource dataSource) {
		 Map<String, Object> properties = new HashMap<>();
		    properties.put("hibernate.hbm2ddl.auto", "update");
		
		return builder
                .dataSource(dataSource)
                .packages("com.moglix.wms.entities")
                .persistenceUnit("wms")
                .properties(properties)
                .build();
    }
	
	@Primary
    @Bean(name = "wmsTransactionManager")
    public PlatformTransactionManager firstTransactionManager(@Qualifier("wmsEntityManagerFactory")
                                                              EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }
}
