package com.cosmos.admin.entity;

import com.cosmos.login.entity.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "admin")
@Data
@NoArgsConstructor
public class Admin extends AppUser {
    @Column(name = "address")
    private String address;
}
