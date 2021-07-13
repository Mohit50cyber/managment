package com.moglix.wms.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "emsEntityManagerFactory",
        transactionManagerRef = "emsTransactionManager",
        basePackages = "com.moglix.ems.repository"
)
@EnableTransactionManagement
public class EMSDsConfig {

	@Bean(name = "emsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(final EntityManagerFactoryBuilder builder,
                                                                             final @Qualifier("enterpriseDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.moglix.ems.entities")
                .persistenceUnit("enterprise")
                .build();
    }
    @Bean(name = "emsTransactionManager")
    public PlatformTransactionManager secondTransactionManager(@Qualifier("emsEntityManagerFactory")
                                                               EntityManagerFactory secondEntityManagerFactory) {
        return new JpaTransactionManager(secondEntityManagerFactory);
    }
}
