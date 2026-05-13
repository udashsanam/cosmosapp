package com.cosmos.esewa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EsewaPaymentResponse {

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_amount")
    private String totalAmount;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("signed_field_names")
    private String signedFieldNames;

    @JsonProperty("signature")
    private String signature;


    @Override
    public String toString() {
        return "EsewaPaymentResponse{" +
                "transactionCode='" + transactionCode + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", transactionUuid='" + transactionUuid + '\'' +
                ", productCode='" + productCode + '\'' +
                ", signedFieldNames='" + signedFieldNames + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}