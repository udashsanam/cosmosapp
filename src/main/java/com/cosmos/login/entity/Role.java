package com.cosmos.login.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
    ROLE_ADMIN, ROLE_MODERATOR, ROLE_ASTROLOGER, ROLE_USER,ROlE_ASTRO_MODE;
    
    public String getAuthority() {
        return name();
      }
}
