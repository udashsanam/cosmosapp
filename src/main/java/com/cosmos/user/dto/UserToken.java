package com.cosmos.user.dto;

import com.cosmos.login.dto.AppUserToken;

import lombok.Data;

import java.util.List;

@Data
public class UserToken extends AppUserToken {
    private UserDto userDetails;
}
