package com.moglix.wms.controller;

import com.moglix.wms.api.response.ApiResponse;
import com.moglix.wms.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("User Management")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getToken")
    public ResponseEntity<ApiResponse> getToken(@RequestParam(name = "email", required = true) String email) {
        return new ResponseEntity<>(userService.getToken(email), HttpStatus.OK);
    }
}
