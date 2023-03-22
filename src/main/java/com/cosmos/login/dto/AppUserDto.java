package com.cosmos.login.dto;

import com.cosmos.login.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AppUserDto {
    private Long userId;
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    //    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Email
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phoneNumber;

    private String gender;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    private String country;

    private String countryIso;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;

    private String profileImageUrl;

    private boolean subscription;

}
