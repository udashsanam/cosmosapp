package com.cosmos.cybersource.controller;

import com.cosmos.cybersource.dto.PaymentUserDto;
import com.cosmos.cybersource.service.PaymentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment-user")
public class PaymentUserController {

    private final PaymentUserService paymentUserService;

    public PaymentUserController(PaymentUserService paymentUserService) {
        this.paymentUserService = paymentUserService;
    }


    @PostMapping()
    ResponseEntity<?> save(@RequestBody PaymentUserDto paymentUserDto, HttpServletRequest request){
        return ResponseEntity.ok(paymentUserService.save(paymentUserDto,request));
    }
}
