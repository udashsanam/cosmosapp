package com.cosmos.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RateDto {

    @Max(value = 5)
    private Integer rate;
    @NotNull
    private Long engAnswerId;
    @NotNull
    private String deviceId;
}
