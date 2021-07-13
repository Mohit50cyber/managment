package com.moglix.wms.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@Configuration
@ConfigurationProperties(prefix = "moglix.invoice-engine")
@Data
@NoArgsConstructor
@Slf4j
public class InvoiceEngineConfig {
	
	private String baseUrl;
	private String invoicePath;
	private String challanPath;


	@Bean
    public RestTemplate restTemplate(){
	    return new RestTemplate();
    }
}
