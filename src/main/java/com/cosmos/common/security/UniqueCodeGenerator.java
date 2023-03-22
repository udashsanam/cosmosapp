package com.cosmos.common.security;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class UniqueCodeGenerator {

    public String generateOtp(int length) {
        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[length];

        for(int i = 0; i< length ; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return otp.toString();
    }

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
