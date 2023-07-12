package com.cosmos.cybersource.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentUserDto {

    private Long authTransRefNo;

    private String transactionUUID;

    private Long userId;

    private String deviceId;
}
