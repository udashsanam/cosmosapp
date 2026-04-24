package com.cosmos.esewa.controller;

import com.cosmos.esewa.service.EsewaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class EsewaController {

    private final EsewaService esewaService;

    public EsewaController(EsewaService esewaService) {
        this.esewaService = esewaService;
    }


    @GetMapping("/success")
    public String handleSuccess(@RequestParam Map<String, String> params) {
//        String transactionUuid = params.get("transaction_uuid");

        // IMPORTANT: verify transaction with eSewa API
//        boolean isValid = verifyTransaction(transactionUuid);
        String response =  esewaService.decodeBase64(params.get("data"));
        if (true) {
            // update DB: mark payment SUCCESS
            return "Payment Success";
        } else {
            return "Verification Failed";
        }
    }

    @GetMapping("/faliue")
    public String handleFaliue(@RequestParam Map<String, String> params) {
        String transactionUuid = params.get("transaction_uuid");

//         IMPORTANT: verify transaction with eSewa API
        boolean isValid = verifyTransaction(transactionUuid);

        if (isValid) {
            // update DB: mark payment SUCCESS
            return "Payment Success";
        } else {
            return "Verification Failed";
        }
    }

    public boolean verifyTransaction(String uuid) {
        String url = "https://rc.esewa.com.np/api/epay/transaction/status/?product_code=EPAYTEST&transaction_uuid=" + uuid;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response.contains("COMPLETE");
    }
}