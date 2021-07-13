package com.moglix.wms.service;

import com.moglix.ems.entities.ApplicationUser;
import com.moglix.ems.repository.UserRepository;
import com.moglix.wms.api.response.ApiResponse;
import com.moglix.wms.dto.UserBean;
import com.moglix.wms.security.TokenUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private TokenUtilService tokenUtilService;
    private UserRepository userRepository;

    @Autowired
    public UserService(TokenUtilService tokenUtilService, UserRepository userRepository) {
        this.tokenUtilService = tokenUtilService;
        this.userRepository = userRepository;
    }


    public ApiResponse getToken(String email) {
        Optional<ApplicationUser> optionalApplicationUser = userRepository.findOneByEmail(email);
        if (!optionalApplicationUser.isPresent()) {
            logger.warn(String.format("email : %s doesn't have access for unified portal!", email));
            return new ApiResponse(String.format("email : %s doesn't have access for unified portal!", email), false, HttpStatus.BAD_REQUEST.value(), null);
        }
        ApplicationUser loggedInUser = optionalApplicationUser.get();
        String token = tokenUtilService.createTokenForUser(loggedInUser);
        logger.info(String.format("User %s logged in successfully : %s", loggedInUser.getEmail(), new Date()));
        return new ApiResponse(null, true, HttpStatus.OK.value(), new UserBean(loggedInUser, token));
    }
}
