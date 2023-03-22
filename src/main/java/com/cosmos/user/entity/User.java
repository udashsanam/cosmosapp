package com.cosmos.user.entity;

import com.cosmos.login.entity.AppUser;
import com.cosmos.ritual.entity.RitualEnquiry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("user")
@Data
@NoArgsConstructor
public class User extends AppUser {

    @Column(name = "birth_date")
    private String dateOfBirth;

    @Column(name = "birth_time")
    private String birthTime;

    @Column(name = "is_accurate_time", columnDefinition = "tinyint(1)")
    private Boolean accurateTime;

    @Column(name = "subscription", columnDefinition = "tinyint(1) default false")
    private boolean subscription;

    @Column(name = "device_token")
    @JsonIgnore
    private String deviceToken;

    @Column(name = "device_id", unique = true)
    @JsonIgnore
    private String deviceId;

    @Column(name = "first_login", columnDefinition = "tinyint(1)")
    @JsonIgnore
    private Boolean firstLogin;
}
