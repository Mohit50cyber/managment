package com.moglix.wms.config;

import java.util.Properties;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {
	
	@Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;
	
	@Value("${spring.mail.host}")
    private String host;
	
	@Value("${spring.mail.port}")
    private Integer port;
	
	@Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String trust;
		
	@Bean("mailer")
	public Mailer configureMailer() {
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth",            auth);
        prop.put("mail.smtp.starttls.enable", starttls);
        prop.put("mail.smtp.host",            host);
        prop.put("mail.smtp.port",            port);
        prop.put("mail.smtp.ssl.trust",       trust);
        
		return MailerBuilder.withSMTPServer(host, port).withProperties(prop).withDebugLogging(true).async().buildMailer();
	}
}
