package com.cosmos.cybersource.controller;

import com.cosmos.cybersource.dto.VerifyPaymentDto;
import com.cosmos.cybersource.service.VerifyPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify-payment")
public class VerifyPaymentController {

    private final VerifyPaymentService verifyPaymentService;

    public VerifyPaymentController(VerifyPaymentService verifyPaymentService) {
        this.verifyPaymentService = verifyPaymentService;
    }

    @PostMapping
    ResponseEntity<?> verifyPayment(@ModelAttribute VerifyPaymentDto verifyPaymentDto){
        return ResponseEntity.ok(verifyPaymentService.savePayment(verifyPaymentDto));
    }
}
