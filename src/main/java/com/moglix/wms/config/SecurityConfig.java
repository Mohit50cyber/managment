package com.moglix.wms.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.security.SimpleCORSFilter;
import com.moglix.wms.security.TokenUtilService;
import com.moglix.wms.security.VerifyTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
@Order(1)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private TokenUtilService tokenUtilService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/", "/csrf", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**",

                "/configuration/**", "/swagger-ui.html", "/webjars/**", "/api/sample/**","/api/user/**","/api/products/","/app/version/**","/actuator/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().cors().disable()
                .httpBasic().authenticationEntryPoint(authenticationEntryPoint()).and()
                .exceptionHandling().and().csrf().disable()
                .addFilterBefore(new SimpleCORSFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(new VerifyTokenFilter(tokenUtilService), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonRespString = ow.writeValueAsString(new BaseResponse("Access Denied for unified portal!", false, HttpStatus.UNAUTHORIZED.value()));
            response.setContentType("application/json");
            response.getWriter().write(jsonRespString);
            response.getWriter().flush();
            response.getWriter().close();
        };
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
