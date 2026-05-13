package com.cosmos.esewa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    @Value("${esewa.status.check}")
    private String statusCheckUrl;

    public Map<String, String> initiatePayment(Double packagePrice) {
        String amount = String.valueOf(packagePrice);
        String taxAmount = "0";
        String totalAmount = String.valueOf(packagePrice);
        String transactionUuid = UUID.randomUUID().toString();
        String productCode = "EPAYTEST";

        String successUrl = baseUrl + "/esewa/success";
        String failureUrl = baseUrl + "/esewa/failure";

        String data = String.format(
                "total_amount=%s,transaction_uuid=%s,product_code=%s",
                totalAmount, transactionUuid, productCode
        );


        String signature = generateSignature(data, "8gBm/:&EnhH.1/q");
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

    public String checkEsewaStatus(String productCode,
                                   String transactionUuid,
                                   String totalAmount) {
        RestTemplate restTemplate = new RestTemplate();

        String url = statusCheckUrl
                + "?product_code=" + productCode
                + "&transaction_uuid=" + transactionUuid
                + "&total_amount=" + totalAmount;
        return restTemplate.getForObject(url, String.class);
    }

}
