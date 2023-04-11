package com.cosmos.email.controller;


import com.cosmos.email.dto.EmailVerifiyDto;
import com.cosmos.email.service.EmailService;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;



    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerifiyDto emailVerifiyDto){
        boolean isverified = emailService.veirifyEmail(emailVerifiyDto);

        return ResponseEntity.ok(isverified);
    }
}
