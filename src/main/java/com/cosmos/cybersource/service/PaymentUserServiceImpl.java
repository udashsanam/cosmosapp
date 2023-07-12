package com.cosmos.cybersource.service;

import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.cybersource.dto.PaymentUserDto;
import com.cosmos.cybersource.entity.PaymentUserEntity;
import com.cosmos.cybersource.repo.PaymentUserRepo;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.login.service.impl.AppUserServiceImpl;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
public class PaymentUserServiceImpl implements PaymentUserService{

    @Autowired
    private PaymentUserRepo paymentUserRepo;

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository appUserRepo;

    public PaymentUserServiceImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public PaymentUserDto save(PaymentUserDto paymentUserDto, HttpServletRequest  request) {
//        String email = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request));
//        User appUser = appUserRepo.findByEmail(email);
        AppUser auser = appUserRepo.findByDeviceId(paymentUserDto.getDeviceId());
        if(paymentUserDto.getUserId() !=null && auser ==null) auser = appUserRepo.findByUserId(paymentUserDto.getUserId());
        if(auser == null) throw new RuntimeException("user not found");
        PaymentUserEntity user = PaymentUserEntity.builder()
                .authTransRefNo(paymentUserDto.getAuthTransRefNo())
                .transactionUUID(paymentUserDto.getTransactionUUID())
                .user(auser)
                .build();

        try {
            paymentUserRepo.save(user);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("error saing");
        }
        return PaymentUserDto.builder().userId(auser.getUserId()).build();
    }
}
