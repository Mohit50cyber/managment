package com.moglix.wms.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "moglix.ems-engine")
@Data
@NoArgsConstructor
@Slf4j
public class EmsEngineConfig {
	
	private String baseUrl;
	private String packableQuantityUpdatePath;
}
