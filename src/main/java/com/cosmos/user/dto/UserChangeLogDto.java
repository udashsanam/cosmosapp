package com.cosmos.user.dto;

import com.cosmos.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserChangeLogDto {



    private Long id;


    private String dateOfBirth;

    private String birthTime;

    private Boolean accurateTime;

    private boolean subscription;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String city;

    private String state;

    private String country;

    private String gender;

    private String countryIso;

    private String deviceToken;

}
