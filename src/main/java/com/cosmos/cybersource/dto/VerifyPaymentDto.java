package com.cosmos.cybersource.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class VerifyPaymentDto {

    private Long auth_trans_ref_no;

    private Long reference_number;

    private String access_key;

    private String profile_id;

    private String transaction_uuid;


    private Double amount;

    private String decision;

    private String reason_code;
}
