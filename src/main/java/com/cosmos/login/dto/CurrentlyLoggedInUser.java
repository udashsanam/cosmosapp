package com.cosmos.login.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Data
public class CurrentlyLoggedInUser extends User {
    private Long currentlyLoggedInUserId;

    public CurrentlyLoggedInUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.currentlyLoggedInUserId = id;
    }

    @Override
    public String toString() {
        return "CurrentlyLoggedInUser{" +
                "currentlyLoggedInUserId=" + currentlyLoggedInUserId +
                '}';
    }
}
