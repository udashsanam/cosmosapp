package com.cosmos.esewa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EsewaService {

    @Value("${payment.base.url}")
    private String baseUrl;

    public Map<String, String> initiatePayment() {
        String amount = "100";
        String taxAmount = "0";
        String totalAmount = "100";
        String transactionUuid = UUID.randomUUID().toString();
        String productCode = "EPAYTEST";
//        String productCode = "INTENT";

        String successUrl = baseUrl + "/payment/success";
        String failureUrl = baseUrl + "/payment/failure";

        String data = String.format(
                "total_amount=%s,transaction_uuid=%s,product_code=%s",
                totalAmount, transactionUuid, productCode
        );
//        String data = String.format(
//                "product_code=%s,amount=%s,transaction_uuid=%s",
//               productCode,
//                amount,
//                transactionUuid
//        );

        String signature = generateSignature(data, "8gBm/:&EnhH.1/q");
//        String signature = generateSignature(data, "LB0REg8HUSw3MTYrI1s6JTE8Kyc6JyAqJiA3MQ==");

        Map<String, String> response = new HashMap<>();
        response.put("amount", amount);
        response.put("tax_amount", taxAmount);
        response.put("total_amount", totalAmount);
        response.put("transaction_uuid", transactionUuid);
        response.put("product_code", productCode);
        response.put("success_url", successUrl);
        response.put("failure_url", failureUrl);
        response.put("signature", signature);
        response.put("product_service_charge", "0");
        response.put("product_delivery_charge", "0");
        response.put("signed_field_names", "total_amount,transaction_uuid,product_code");
//        response.put("signed_field_names", "product_code,amount,transaction_uuid");


        return response;
    }


    public String generateSignature(String data, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(),"HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
            System.out.println(hash);
            return hash;
        }
        catch (Exception e){
            System.out.println("Error");
            throw new RuntimeException(e);
        }
    }

    public String decodeBase64(String encoded) {
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

}
