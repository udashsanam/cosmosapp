package com.cosmos.paymentvalidation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/payment-validation")
public class PaymentValidationController {

    private final PaymentValidationRepo paymentValidationRepo;

    public PaymentValidationController(PaymentValidationRepo paymentValidationRepo) {
        this.paymentValidationRepo = paymentValidationRepo;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> save(@RequestBody  PaymentValidation paymentValidation){
        return new ResponseEntity<>(paymentValidationRepo.save(paymentValidation), HttpStatus.OK);
    }
}
