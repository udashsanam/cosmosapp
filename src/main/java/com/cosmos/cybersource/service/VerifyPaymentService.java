package com.cosmos.cybersource.service;

import com.cosmos.cybersource.dto.VerifyPaymentDto;

public interface VerifyPaymentService {

    boolean savePayment(VerifyPaymentDto verifyPaymentDto);
}
