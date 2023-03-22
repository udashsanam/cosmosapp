package com.cosmos.quickSupport.service;

import com.cosmos.common.exception.CustomException;
import com.cosmos.quickSupport.entity.ReportedIssue;
import com.cosmos.quickSupport.repo.ReportedIssueRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportedIssueServiceImpl {

    @Autowired
    ReportedIssueRepo issueRepo;

    @Autowired
    UserRepository userRepo;

    public ReportedIssue saveOrUpdateReportedIssue(ReportedIssue issue) {
        return issueRepo.save(issue);
    }

    public ReportedIssue findIssueById(Long id) {
        return issueRepo.findByIssueId(id);
    }

    public List<ReportedIssue> fetchReportedIssueByUserId(String deviceId) {
        User _user = userRepo.findByDeviceId(deviceId);
        if (_user == null) {
            throw new CustomException("User not found.", HttpStatus.NOT_FOUND);
        }
        return issueRepo.findAllByEndUserId(_user.getUserId());
    }

    public List<ReportedIssue> fetchReportedIssue() {
        return issueRepo.findAll();
    }
}
