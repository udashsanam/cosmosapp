package com.cosmos.compatibility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompatibilityDto {
    private Long compatibilityId;

    private String name;

    private String birthDate;

    private String birthTime;

    private String gender;

    private String birthPlace;

    private String partnerName;

    private String partnerBirthDate;

    private String partnerBirthTime;

    private String partnerBirthPlace;

    private String partnerGender;

    private String deviceId;

    private String response;

    private int status;
}
