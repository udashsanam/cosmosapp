package com.cosmos.email.service;

import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.email.dto.EmailVerifiyDto;
import com.cosmos.login.entity.PasswordResetToken;
import com.cosmos.login.repo.PasswordResetTokenRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean veirifyEmail(EmailVerifiyDto emailVerifiyDto) {
        User user = userRepository.findByEmail(emailVerifiyDto.getEmail());
        if(user == null) throw new RuntimeException(" user not found");
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByToken(emailVerifiyDto.getToken());
        if(passwordResetToken == null) throw new RuntimeException("Token not match ");



        return false;
    }
}
