package com.cosmos.strip.dto;

import lombok.Data;

@Data
public class VerifyStripePaymentDto {
    private String clientSecret;
    private String response;
}
