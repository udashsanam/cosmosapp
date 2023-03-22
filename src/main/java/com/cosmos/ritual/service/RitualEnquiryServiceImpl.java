package com.cosmos.ritual.service;

import com.cosmos.common.exception.CustomException;
import com.cosmos.ritual.entity.RitualEnquiry;
import com.cosmos.ritual.repo.RitualEnquiryRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RitualEnquiryServiceImpl {

    @Autowired
    RitualEnquiryRepo ritualRepo;

    @Autowired
    UserRepository userRepo;

    public RitualEnquiry receiveMessageFromUser(RitualEnquiry enquiry) {
        return ritualRepo.save(enquiry);
    }

    public List<RitualEnquiry> fetchAllRitualEnquires() {
        return ritualRepo.findAll();
    }

    public List<RitualEnquiry> fetchAllRitualEnquiresByUserId(String deviceId) {
        User _user = userRepo.findByDeviceId(deviceId);
        if (_user == null) {
            throw new CustomException("User not found.", HttpStatus.NOT_FOUND);
        }
        return ritualRepo.findAllByEndUserId(_user.getUserId());
    }


    public RitualEnquiry replyRitualEnquiry(RitualEnquiry ritualEnquiry) {
        return ritualRepo.save(ritualEnquiry);
    }

}
