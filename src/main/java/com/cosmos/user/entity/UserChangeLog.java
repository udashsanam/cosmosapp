package com.cosmos.user.entity;


import com.cosmos.common.model.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_user_change_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeLog extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;


    @Column(name = "birth_date")
    private String dateOfBirth;

    @Column(name = "pre_birth_date")
    private String preDateOfBirth;


    @Column(name = "birth_time")
    private String birthTime;

    @Column(name = "pre_birth_time")
    private String preBirthTime;

    @Column(name = "is_accurate_time", columnDefinition = "tinyint(1)")
    private Boolean accurateTime;

    @Column(name = "pre_is_accurate_time", columnDefinition = "tinyint(1)")
    private Boolean preAccurateTime;

    @Column(name = "subscription", columnDefinition = "tinyint(1) default false")
    private boolean subscription;


    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    private String phoneNumber;

    private String city;

    private String state;

    private String country;

    private String gender;

    @Column(name = "country_iso", length = 10)
    private String countryIso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private  User user;

    private String deviceToken;


}
