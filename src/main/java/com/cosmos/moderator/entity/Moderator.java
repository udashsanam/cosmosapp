package com.cosmos.moderator.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.cosmos.login.entity.AppUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("moderator")
@Data
@NoArgsConstructor
public class Moderator extends AppUser {

}
