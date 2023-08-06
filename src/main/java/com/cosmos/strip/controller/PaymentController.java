package com.cosmos.strip.controller;

import com.cosmos.cybersource.dto.VerifyPaymentDto;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.strip.dto.CreatePayment;
import com.cosmos.strip.dto.CreatePaymentResponse;
import com.cosmos.strip.dto.VerifyStripePaymentDto;
import com.cosmos.strip.repo.PaymentIntentRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.google.gson.internal.reflect.ReflectionAccessor;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentIntentRepo paymentIntentRepo;


    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody @Valid CreatePayment createPayment) throws StripeException {
        User user = userRepository.findByDeviceId(createPayment.getDeviceId());
        if(user ==null) throw new RuntimeException("user not found");
        Stripe.apiKey="sk_test_51NUsS3SCeBYlkmBnPu8KbCFB5UMSv1jstgvvLVwgqpGvPwYvgiyTW3wIcDu4XhMvDpTjJ9IxPmv0TRGajkkl0jna009FFFczkI";
        Double amount = createPayment.getAmount() * 100;
        PaymentIntentCreateParams createParams = new
                PaymentIntentCreateParams.Builder()
                .setCurrency(createPayment.getCurrency())
                .putMetadata("deviceId", createPayment.getDeviceId())
                .setAmount(Long.valueOf(amount.longValue()))
                .build();

        PaymentIntent intent = PaymentIntent.create(createParams);
        String clientSecret = intent.getClientSecret();

        com.cosmos.strip.entity.PaymentIntent paymentIntent = com.cosmos.strip.entity.PaymentIntent.builder()
                .clientSecret(clientSecret)
                .isSuccess(Boolean.FALSE)
                .amount(createPayment.getAmount())
                .cratedDate(new Timestamp(System.currentTimeMillis()))
                .currency(createPayment.getCurrency())
                .deviceId(createPayment.getDeviceId())
                .build();
        
        paymentIntentRepo.save(paymentIntent);
        return ResponseEntity.ok(clientSecret);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyStripePaymentDto verifyStripePaymentDto ){
        com.cosmos.strip.entity.PaymentIntent paymentIntent = paymentIntentRepo.findByClientSecret(verifyStripePaymentDto.getClientSecret());
        if(verifyStripePaymentDto.getResponse().equals("succeeded")){
            paymentIntent.setIsSuccess(Boolean.TRUE);
            paymentIntent.setResponse(verifyStripePaymentDto.getResponse());
        }
        paymentIntent.setResponse(paymentIntent.getResponse());
        paymentIntentRepo.save(paymentIntent);
        return ResponseEntity.ok(Boolean.TRUE);
    }



}
