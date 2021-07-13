package com.moglix.wms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.moglix.ems.entities.ApplicationUser;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.exception.InvalidUserTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


public class VerifyTokenFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(VerifyTokenFilter.class);
    private final TokenUtilService tokenUtilService;

    public VerifyTokenFilter(TokenUtilService tokenUtilService) {
        this.tokenUtilService = tokenUtilService;
    }

    private static ApplicationUser getUserFromAuthentication(Authentication authentication) {
        LoginUser loginUser = (LoginUser) authentication.getDetails();
        return loginUser.getUser();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            Optional<Authentication> authentication = tokenUtilService.verifyToken(request);
            if (authentication.isPresent()) {
                ApplicationUser loginUser = getUserFromAuthentication(authentication.get());
                logger.info(String.format("User : %s || action url : %s", loginUser.getEmail(), ((HttpServletRequest) req).getRequestURI()));
                SecurityContextHolder.getContext().setAuthentication(authentication.get());
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
            
            response.setHeader("Access-Control-Allow-Origin", "*");
    		response.setHeader("Access-Control-Allow-Credentials", "true");
    		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    		response.setHeader("Access-Control-Max-Age", "3600");
    		response.setHeader("Access-Control-Allow-Headers", "*");

            filterChain.doFilter(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ExpiredJwtException)
                setResponse(false, response, "Session Expired", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
            if (e instanceof SignatureException)
                setResponse(false, response, "Invalid Token", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
            if (e instanceof UsernameNotFoundException)
                setResponse(false, response, "User Not Found", HttpStatus.UNAUTHORIZED.value(), HttpStatus.NOT_FOUND);
            if (e instanceof InvalidUserTokenException)
                setResponse(false, response, "Session Expired", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
            else {
                setResponse(false, response, e.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED);
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    private void setResponse(boolean status, HttpServletResponse response, String message, int httpStatusValue, HttpStatus httpStatus)
            throws IOException {
        response.setStatus(httpStatusValue);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonRespString = ow.writeValueAsString(new BaseResponse(message, status, httpStatus.value()));
        response.setContentType("application/json");
        response.getWriter().write(jsonRespString);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
