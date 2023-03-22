package com.cosmos.user.service;

import com.cosmos.admin.entity.QuestionPackage;
import com.cosmos.admin.repo.QuestionPackageRepo;
import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.common.exception.CustomException;
import com.cosmos.user.dto.PackageSubscriptionDto;
import com.cosmos.user.entity.PackageSubscription;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.PackageSubscriptionRepo;
import com.cosmos.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageSubscriptionServiceImpl {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PackageSubscriptionRepo subscriptionRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    QuestionPackageRepo questionPackageRepo;

    public PackageSubscriptionDto subscribePackage(Long packageId, String deviceId) {
        PackageSubscription pkg = new PackageSubscription();
        User us = userRepo.findByDeviceId(deviceId);
        if (us == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        QuestionPackage qp;
        if (questionPackageRepo.findById(packageId).isPresent()) {
            qp = questionPackageRepo.findById(packageId).get();
        } else {
            throw new CustomException("Package not found", HttpStatus.NOT_FOUND);
        }
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(qp.getPackageType().equalsIgnoreCase("Weekly") ? 7 : 30);
        pkg.setExpireAt(java.sql.Timestamp.valueOf(localDateTime));
        pkg.setAllotQuestion(qp.getQuestionPackage());
        pkg.setRemainingQuestion(qp.getQuestionPackage());
        pkg.setSubsPrice(qp.getPackagePrice());
        pkg.setSubsStatus(1);
        pkg.setPackageId(qp.getId());
        pkg.setUserId(us.getUserId());
        PackageSubscriptionDto subscriptionDto = modelMapper.map(subscriptionRepo.save(pkg), PackageSubscriptionDto.class);
        subscriptionDto.setPackageModel(qp);
        return subscriptionDto;
    }

    public List<PackageSubscriptionDto> fetchAllSubscribePackageByDeviceId(String deviceId) {
        User us = userRepo.findByDeviceId(deviceId);
        if (us == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        List<PackageSubscription> _list = subscriptionRepo.findUnExpirePackageByUserId(us.getUserId());

        return _list.stream().map(packageSubscription -> {
            PackageSubscriptionDto _pkgSubsDto = modelMapper.map(packageSubscription, PackageSubscriptionDto.class);
            _pkgSubsDto.setPackageModel(questionPackageRepo.findById(packageSubscription.getPackageId()).get());
            return _pkgSubsDto;
        }).collect(Collectors.toList());
    }

    public PackageSubscriptionDto useSubscribePackage(String deviceId) {
        User us = userRepo.findByDeviceId(deviceId);
        if (us == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        PackageSubscription subsPkg = subscriptionRepo.findOldestPackageByUserId(us.getUserId());
        if (subsPkg.getRemainingQuestion() > 0) {
            if (subsPkg.getRemainingQuestion() == 1) {
                subsPkg.setSubsStatus(0);
            }
            subsPkg.setRemainingQuestion(subsPkg.getRemainingQuestion() - 1);
        }
        PackageSubscriptionDto subsPkgDto = modelMapper.map(subscriptionRepo.save(subsPkg), PackageSubscriptionDto.class);
        subsPkgDto.setPackageModel(questionPackageRepo.findById(subsPkg.getPackageId()).get());
        return subsPkgDto;
    }
}
