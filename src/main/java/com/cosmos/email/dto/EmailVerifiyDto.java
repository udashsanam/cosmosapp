package com.cosmos.email.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifiyDto {

    private String email;

    private String oldPassword;

    private String password;

    private String confirmPassword;

    private String token;
}
