package com.cosmos.cybersource.service;

import com.cosmos.cybersource.dto.PaymentUserDto;

import javax.servlet.http.HttpServletRequest;

public interface PaymentUserService {

    PaymentUserDto save(PaymentUserDto paymentUserDto, HttpServletRequest request);
}
