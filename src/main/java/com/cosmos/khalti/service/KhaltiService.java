package com.cosmos.khalti.service;

import com.cosmos.khalti.model.KhaltiPaymentResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KhaltiService {

    @Value("${khalti.base.url}")
    private String khaltiBaseUrl;

    @Value("${payment.base.url}")
    private String paymentBaseUrl;
    @Value("${khalti.api.key}")
    private String apiKey;
    RestTemplate restTemplate = new RestTemplate();


    public String initKhalti(Double amount) {
        String initUrl = khaltiBaseUrl + "epayment/initiate/";

        String successUrl = paymentBaseUrl + "/khalti/success";
        String failureUrl = paymentBaseUrl + "/payment/failure";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("return_url", successUrl);
        body.put("website_url", "https://system.cosmosastrology.com");
        body.put("amount", amount);
        body.put("purchase_order_id", UUID.randomUUID().toString());
        body.put("purchase_order_name", "Subscription");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        headers.set("Authorization", "Key " + apiKey);
        ResponseEntity<String> response = restTemplate.postForEntity(
                initUrl,
                request,
                String.class
        );
        if(response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // if using OffsetDateTime

            KhaltiPaymentResponse responses =
                    null;
            try {
                responses = mapper.readValue(response.getBody(), KhaltiPaymentResponse.class);
                System.out.println(response.getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return responses.getPaymentUrl();
        }
        return "invalid";
    }
}
