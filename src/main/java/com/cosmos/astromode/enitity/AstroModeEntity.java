package com.cosmos.astromode.enitity;

import com.cosmos.login.entity.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("astromode")
@Data
@NoArgsConstructor
public class AstroModeEntity extends AppUser {
}
