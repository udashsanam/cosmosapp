package com.cosmos.astrologer.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.cosmos.login.entity.AppUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("astrologer")
@Data
@NoArgsConstructor
public class Astrologer extends AppUser {

}
