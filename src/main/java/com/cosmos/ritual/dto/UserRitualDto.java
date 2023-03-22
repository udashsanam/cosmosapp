package com.cosmos.ritual.dto;

import com.cosmos.ritual.entity.RitualEnquiry;
import com.cosmos.user.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserRitualDto extends UserDto {

    List<RitualEnquiry> ritual;
}


