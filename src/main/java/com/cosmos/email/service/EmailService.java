package com.cosmos.email.service;

import com.cosmos.email.dto.EmailVerifiyDto;

public interface EmailService {

    boolean veirifyEmail(EmailVerifiyDto emailVerifiyDto);
}
