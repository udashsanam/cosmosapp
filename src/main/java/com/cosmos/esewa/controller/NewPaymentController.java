package com.cosmos.esewa.controller;

import com.cosmos.esewa.service.EsewaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class NewPaymentController {

    private final String esewaServiceUrl = "https://rc-checkout.esewa.com.np/api/client/intent/payment/book";

    private final EsewaService esewaService;
    @GetMapping
    public String index(@RequestParam("code") String code, Model model) {
       Map<String, String>  paymentData= esewaService.initiatePayment();
//
//        String url = "https://rc-checkout.esewa.com.np/api/client/intent/payment/book";
//
//        // Build request body as per INTENT API
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("product_code", "INTENT");
//        requestBody.put("amount", Integer.parseInt(paymentData.get("amount"))); // INTENT uses amount
//        requestBody.put("transaction_uuid", paymentData.get("transaction_uuid"));
//
//        requestBody.put("signed_field_names", paymentData.get("signed_field_names"));
//        requestBody.put("signature", paymentData.get("signature"));
//
//        requestBody.put("callback_url", paymentData.get("success_url"));
//        requestBody.put("redirect_url", paymentData.get("success_url"));
//
//        Map<String, String> properties = new HashMap<>();
//        properties.put("customer_id", "CUST12345");
//        properties.put("remarks", "Internet bill payment");
//
//        requestBody.put("properties", properties);
//
//        // HTTP call
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                entity,
//                String.class
//        );

        model.addAttribute("paymentData", paymentData);
        return "payment";
    }
}
