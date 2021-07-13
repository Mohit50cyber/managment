package com.moglix.wms.security;


import com.moglix.ems.entities.ApplicationUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;

public class LoginUser extends org.springframework.security.core.userdetails.User {

    private ApplicationUser user;

    public LoginUser(ApplicationUser user) {
        super(user.getEmail(), StringUtils.isEmpty(user.getPassword()) ? "" : user.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        this.user = user;
    }

    public ApplicationUser getUser() {
        return user;
    }


}
