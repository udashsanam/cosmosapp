package com.cosmos.user.dto;

import com.cosmos.login.dto.AppUserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UserDto extends AppUserDto {
    @NotBlank(message = "DOB is mandatory")
    private String dateOfBirth;

    @NotBlank(message = "Birth Time is mandatory")
    private String birthTime;

    private boolean accurateTime;

    @NotBlank(message = "Device Token is mandatory")
    private String deviceToken;

    @NotBlank(message = "Device Id is mandatory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String deviceId;
}
