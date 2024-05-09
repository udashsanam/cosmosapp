package com.cosmos.user.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tbl_user_change_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;


    @Column(name = "birth_date")
    private String dateOfBirth;

    @Column(name = "birth_time")
    private String birthTime;

    @Column(name = "is_accurate_time", columnDefinition = "tinyint(1)")
    private Boolean accurateTime;

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
