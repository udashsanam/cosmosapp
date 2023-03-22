package com.cosmos.admin.controller;

import com.cosmos.admin.dto.DashboardDailyReport;
import com.cosmos.admin.entity.QuestionPackage;
import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.admin.service.AdminService;
import com.cosmos.admin.service.QuestionPackageService;
import com.cosmos.admin.service.QuestionPriceService;
import com.cosmos.astrologer.projection.AstrologerWorkReport;
import com.cosmos.common.exception.CustomException;
import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.service.CreditServiceImpl;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.moderator.dto.ModeratorOrAstrologerResponse;
import com.cosmos.moderator.dto.ModeratorWorkReport;
import com.cosmos.questionPool.projection.MonthlyRevenueReport;
import com.cosmos.quickSupport.entity.ReportedIssue;
import com.cosmos.quickSupport.service.ReportedIssueServiceImpl;
import com.cosmos.ritual.dto.UserRitualDto;
import com.cosmos.ritual.entity.RitualEnquiry;
import com.cosmos.ritual.service.RitualEnquiryServiceImpl;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;
import com.cosmos.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private QuestionPriceService questionPriceService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private QuestionPackageService packageService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ReportedIssueServiceImpl reportedIssueService;

    @Autowired
    private RitualEnquiryServiceImpl ritualEnquiryService;

    @Autowired
    private CreditServiceImpl creditService;


    @GetMapping(value = "/dashboard")
    public DashboardDailyReport getAdminDashboard() {
        return adminService.getDailyReport();
    }

    @GetMapping(value = "/question-price", produces = "application/json")
    public QuestionPrice getQuestionPrice() {
        return questionPriceService.fetchCurrentQuestionPrice();
    }

    @PostMapping(value = "/question-price", produces = "application/json", consumes = "application/json")
    public QuestionPrice processSaveQuestionPrice(@Valid @RequestBody QuestionPrice questionPrice) {
        return questionPriceService.saveQuestionPrice(questionPrice);
    }

    @GetMapping(value = "/package", produces = "application/json")
    public List<QuestionPackage> getPackageDetails() {
        return packageService.getAllPackages();
    }

    @PostMapping(value = "/package", produces = "application/json", consumes = "application/json")
    public QuestionPackage processSavePackage(@Valid @RequestBody QuestionPackage questionPackage) {
        return packageService.registerPackage(questionPackage);
    }

    @PutMapping(value = "/package/{id}", produces = "application/json", consumes = "application/json")
    public QuestionPackage processSavePackage(@PathVariable Long id, @Valid @RequestBody QuestionPackage questionPackage) {
        return packageService.updatePackage(id, questionPackage);
    }

    @GetMapping(value = "removePackage/{packageId}", produces = "application/json")
    public ResponseEntity<QuestionPackage> removePackageById(@PathVariable Long packageId) {
        return new ResponseEntity<>(packageService.removePackage(packageId), HttpStatus.OK);
    }

    @GetMapping(value = "/astrologers-work-report")
    public List<AstrologerWorkReport> getAstrologersWorkReport(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate) {
        return adminService.getAstrologerWorkReport(fromDate, toDate);
    }

    @GetMapping(value = "/moderators-work-report")
    public List<ModeratorWorkReport> getModeratorsWorkReport(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate) {
        return adminService.getModeratorWorkReport(fromDate, toDate);
    }

    @GetMapping(value = "/monthly-revenue-report")
    public List<MonthlyRevenueReport> getMonthlyRevenueReports(@RequestParam(name = "year") Integer year) {
        return adminService.getMonthlyRevenueReport(year);
    }

    @GetMapping("/modAnswers/all")
    public ModeratorOrAstrologerResponse moderatorResponses(@RequestParam Long modId, @RequestParam(required = false) String startDate,
                                                            @RequestParam(required = false) String endDate) throws ParseException {
        return adminService.allResponsesByModerator(modId, startDate, endDate);
    }

    @GetMapping(value = "/ritual-inquiry", produces = "application/json")
    public ResponseEntity<?> getAllRitualRelatedInquiry() {
//        return adminService.getAllUsersWithRitual();
        return new ResponseEntity<>(adminService.getAllUsersWithRitual(), HttpStatus.OK);
    }

    @PostMapping(value = "/ritual-inquiry", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RitualEnquiry> saveReplyFromAdmin(@RequestBody RitualEnquiry ritualEnquiry) {
//        return adminService.getAllUsersWithRitual();
        ritualEnquiry.setUserId(getCurrentUserId());
        return new ResponseEntity<>(adminService.saveRitualInquiryMessage(ritualEnquiry), HttpStatus.OK);
    }

    @GetMapping(value = "reported-issue", produces = "application/json")
    public List<ReportedIssue> fetchReportedIssue() {
        return reportedIssueService.fetchReportedIssue();
    }

    @PostMapping(value = "reported-issue", produces = "application/json", consumes = "application/json")
    public ReportedIssue responseReportedIssue(@RequestBody ReportedIssue issue) {
        if (issue.getIssueId() == null) {
            throw new CustomException("Issue id not specified", HttpStatus.NOT_FOUND);
        }
        ReportedIssue _iss = reportedIssueService.findIssueById(issue.getIssueId());
        _iss.setStatus(0);
        _iss.setIssueId(getCurrentUserId());
        _iss.setReply(issue.getReply());
        return reportedIssueService.saveOrUpdateReportedIssue(issue);
    }

    @GetMapping(value = "users/all", produces = "application/json")
    public List<UserDto> fetchAllUser() {
        return userService.fetchAllEndUser();
    }

    @GetMapping(value = "users/credit/{userId}", produces = "application/json")
    public Credit fetchAllUser(@PathVariable Long userId) {
        UserDto us=userService.findUserDetailsById(userId);
        Credit credit = new Credit();
        credit.setEndUserId(us.getUserId());
        credit.setUserId(getCurrentUserId());
        return creditService.grantCreditToEndUser(credit);
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }

}
