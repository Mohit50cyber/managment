package com.moglix.wms.dto;


import com.moglix.ems.entities.ApplicationUser;

import java.io.Serializable;

public class UserBean implements Serializable {

    private String email;
    private String name;
    private String token;
    private Long phoneNumber;

    public UserBean(ApplicationUser applicationUser, String token) {
        this.email = applicationUser.getEmail();
        this.name = applicationUser.getName();
        this.token = token;
        this.phoneNumber = applicationUser.getPhoneNumber();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
