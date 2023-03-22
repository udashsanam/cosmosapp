package com.cosmos.credit.service;

import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.repo.CreditRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditServiceImpl {
    @Autowired
    CreditRepo creditRepo;

    @Autowired
    UserRepository userRepo;

    public Credit grantCreditToEndUser(Credit credit) {
        credit.setCreditStatus(false);
        credit.setCreditCount(1);
        return creditRepo.save(credit);
    }

    public List<Credit> getAllCreditByUser(String deviceId) {
        User _user = userRepo.findByDeviceId(deviceId);
        return creditRepo.findAllByEndUserIdAndAndCreditStatus(_user.getUserId(), false);
    }

    public Credit useCreditByUser(String deviceId) {
        User _user = userRepo.findByDeviceId(deviceId);
        Credit _cr = creditRepo.selectOldestCredit(_user.getUserId(),false);
        _cr.setCreditStatus(true);
        return creditRepo.save(_cr);
    }
}
