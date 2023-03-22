package com.cosmos.login.service.impl;

import com.cosmos.common.exception.CustomException;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserLoginDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserServiceImpl appUserService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AppUser appUser = appUserService.findUserByEmail(email);

        if(appUser == null) {
            throw new CustomException("Bad user credential!", HttpStatus.UNAUTHORIZED);
        }

        return new CurrentlyLoggedInUser(
                appUser.getEmail(),
                appUser.getHashPassword(),
                appUser.isEnabled(),
                true,
                true,
                appUser.isAccountNonLocked(),
                AuthorityUtils.createAuthorityList(appUser.getRole().toString()),
                appUser.getUserId()
        );
    }
}
