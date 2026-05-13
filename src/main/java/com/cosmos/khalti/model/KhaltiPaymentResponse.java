package com.cosmos.khalti.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhaltiPaymentResponse {

    private String pidx;

    @JsonProperty("payment_url")
    private String paymentUrl;

    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;

    @JsonProperty("expires_in")
    private int expiresIn;

}