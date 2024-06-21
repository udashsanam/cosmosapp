package com.cosmos.user.controller;

import com.cosmos.admin.entity.QuestionPackage;
import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.admin.repo.QuestionPackageRepo;
import com.cosmos.admin.service.QuestionPackageService;
import com.cosmos.admin.service.QuestionPriceService;
import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.service.impl.AstrologerServiceImpl;
import com.cosmos.common.exception.CustomException;
import com.cosmos.common.storage.model.UploadFileResponse;
import com.cosmos.common.storage.service.FileStorageService;
import com.cosmos.compatibility.dto.CompatibilityDto;
import com.cosmos.compatibility.service.CompatibilityServiceImpl;
import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.service.CreditServiceImpl;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.questionPool.dto.EnglishQuestionDto;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.service.EnglishQuestionPoolService;
import com.cosmos.quickSupport.entity.ReportedIssue;
import com.cosmos.quickSupport.service.ReportedIssueServiceImpl;
import com.cosmos.ritual.entity.RitualEnquiry;
import com.cosmos.ritual.service.RitualEnquiryServiceImpl;
import com.cosmos.user.dto.*;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.user.service.PackageSubscriptionServiceImpl;
import com.cosmos.user.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private EnglishQuestionPoolService englishQuestionPoolService;
    @Autowired
    private AstrologerServiceImpl astrologerService;
    @Autowired
    private QuestionPriceService questionPriceService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private EnglishAnswerPoolRepo englishAnswerPoolRepo;
    @Autowired
    private CreditServiceImpl creditService;
    @Autowired
    private PackageSubscriptionServiceImpl packageSubscriptionService;
    @Autowired
    private QuestionPackageService questionPackageService;
    @Autowired
    private RitualEnquiryServiceImpl ritualEnquiryService;

    @Autowired
    private ReportedIssueServiceImpl reportedIssueService;

    @Autowired
    private CompatibilityServiceImpl compatibilityService;

    @Autowired
    private QuestionPackageRepo questionPackageRepo;

    @GetMapping(value = "/welcome-messages", produces = "application/json")
    public Map<String, List<String>> processWelcomeMessages() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("messages", userService.getWelcomeMessages("welcome"));
        return map;
    }

    @GetMapping(value = "/user-info/{deviceId}", produces = "application/json")
    public UserDto fetchUserDataByDeviceId(@PathVariable String deviceId) {
        return userService.findUserDetailsByDeviceId(deviceId);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public UserDto signUp(@Valid @RequestBody UserDto userDto) {
        return userService.processUserRegistration(userDto);
    }

    @GetMapping(value = "/previous-history", produces = "application/json")
    public UserHistoryDto getUserPreviousHistory(@RequestParam("device_id") String device_id,
    											@RequestParam("page") int page,
    											@RequestParam("pageSize") int pageSize) {

        if (device_id == null || device_id.isEmpty())
            throw new CustomException("Please provide valid device id to query.", HttpStatus.NOT_ACCEPTABLE);
        UserHistoryDto userHistory = new UserHistoryDto();
        User user = userRepository.getUserbyDeviceId(device_id);

        if (user != null) {
            UserDetailsWithQA details = mapper.map(user, UserDetailsWithQA.class);
            Pageable reqPage = PageRequest.of(page,pageSize);
            details.setQuestionAnswerHistoryList(englishAnswerPoolRepo.findQuestionAnswerHistoryOfUser(user.getUserId(),reqPage).getContent());
            userHistory.setUserDetailsWithQA(details);
        }

        userHistory.setWelcomeMessages(userService.getWelcomeMessages("welcome"));

        userHistory.setMessages(userService.getInitialMessages("welcome"));

        return userHistory;
    }

    @PostMapping(value = "/ask-question", consumes = "application/json", produces = "application/json")
    public EnglishQuestionPool askQuestion(@RequestBody @Valid EnglishQuestionDto englishQuestionDto) {
        return englishQuestionPoolService.addQuestionToPool(englishQuestionDto);
    }

    @PostMapping("/upload-profile-picture")
    public UploadFileResponse uploadFile(@RequestParam("image") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = "http://system.cosmosastrology.com/api/public/download-file/" + fileName;

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping(value = "/fetch-astrologers", produces = "application/json")
    public List<AstrologerDto> getAllAstrologers() {
        return astrologerService.findAllAstrologer();
    }

    @GetMapping(value = "/question-price", produces = "application/json")
    public QuestionPrice getQuestionPrice() {
        return questionPriceService.fetchCurrentQuestionPrice();
    }

    @GetMapping(value = "/question-price/{country}", produces = "application/json")
    public QuestionPrice getQuestionPriceByCountry(@PathVariable String country) {
        return questionPriceService.fetchCurrentQuestionPriceByCountry(country);
    }

    @GetMapping(value = "/fetch-credit/{deviceId}", produces = "application/json")
    public List<Credit> getAllUserSpecificCredit(@PathVariable String deviceId) {
        return creditService.getAllCreditByUser(deviceId);
    }

    @GetMapping(value = "/use-credit/{deviceId}", produces = "application/json")
    public Credit useCreditByUser(@PathVariable String deviceId) {
        return creditService.useCreditByUser(deviceId);
    }

    @GetMapping(value = "/fetch-package/{country}", produces = "application/json")
    public List<QuestionPackage> fetchPackageByCountry(@PathVariable String country) {
//        if (!country.equalsIgnoreCase("Nepal")) {
//            country = "International";
//        }
        return questionPackageService.getQuestionPackageByCountry(country);
    }

    @PostMapping(value = "/fetch-package", produces = "application/json")
    public QuestionPackage savePackage(@RequestBody QuestionPackage  country) {
//        if (!country.equalsIgnoreCase("Nepal")) {
//            country = "International";
//        }
        return questionPackageRepo.save(country);
    }

    @PostMapping(value = "/compatibility/add", consumes = "application/json")
    public ResponseEntity<?> RegisterForCompatibilityTest(@RequestBody CompatibilityDto compatibilityDto) {
        return new ResponseEntity<>(compatibilityService.addCompatibilityTestRequest(compatibilityDto), HttpStatus.OK);
    }

    @GetMapping(value = "/compatibility/view/{deviceId}", produces = "application/json")
    public ResponseEntity<?> ViewAllCompatibilityTest(@PathVariable String deviceId) {
        return new ResponseEntity<>(compatibilityService.getALlCompatibilityTest(deviceId), HttpStatus.OK);
    }


    @GetMapping(value = "/express-package/{deviceId}/{status}")
    public Map<String, String> expressPackage(@PathVariable String deviceId, @PathVariable String status) {
        Map<String, String> map = new HashMap<>();

        if (status.equalsIgnoreCase("subscribe")) {
            map.put("message", "Express package has been subscription.");
            userService.subscribeUnsubscribeExpressPackage(deviceId, true);
            return map;
        } else if (status.equalsIgnoreCase("unsubscribe")) {
            map.put("message", "Express Package has been unsubscribed. Normal rate of QA has been applied.");
            userService.subscribeUnsubscribeExpressPackage(deviceId, false);
            return map;
        } else {
            throw new CustomException("Please provide param to query.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/fetch-subscribe-package/{deviceId}", produces = "application/json")
    public List<PackageSubscriptionDto> fetchSubscribePackage(@PathVariable String deviceId) {
        return packageSubscriptionService.fetchAllSubscribePackageByDeviceId(deviceId);
    }

    @GetMapping(value = "/subscribe-package/{deviceId}/{packageId}", consumes = "application/json", produces = "application/json")
    public PackageSubscriptionDto subscribePackage(@PathVariable String deviceId, @PathVariable Long packageId) {
        return packageSubscriptionService.subscribePackage(packageId, deviceId);
    }

    @GetMapping(value = "/use-subscribe-package/{deviceId}", consumes = "application/json", produces = "application/json")
    public PackageSubscriptionDto subscribePackage(@PathVariable String deviceId) {
        return packageSubscriptionService.useSubscribePackage(deviceId);
    }

    @GetMapping(value = "fetch-ritual-enquiry/{deviceId}")
    public Map<String, Object> fetchRitualEnquiry(@PathVariable String deviceId) {
        Map<String, Object> _ritual = new HashMap<>();
        _ritual.put("message", userService.getWelcomeMessages("ritual"));
        _ritual.put("ritualEnquiry", ritualEnquiryService.fetchAllRitualEnquiresByUserId(deviceId));
        return _ritual;
    }

    @PostMapping(value = "append-ritual-enquiry", consumes = "application/json", produces = "application/json")
    public RitualEnquiry receiveRitualEnquiry(@RequestBody RitualEnquiry ritual) {
        return ritualEnquiryService.receiveMessageFromUser(ritual);
    }

    @PostMapping(value = "report-issue", consumes = "application/json", produces = "application/json")
    public ReportedIssue receiveRitualEnquiry(@RequestBody ReportedIssue issue) {
        issue.setStatus(1);
        return reportedIssueService.saveOrUpdateReportedIssue(issue);
    }

    @GetMapping(value = "report-issue/{deviceId}", consumes = "application/json", produces = "application/json")
    public List<ReportedIssue> receiveRitualEnquiry(@PathVariable String deviceId) {
        return reportedIssueService.fetchReportedIssueByUserId(deviceId);
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }

    @GetMapping(value = "/previous-history/{device_id}", produces = "application/json")
    public UserHistoryDto getUserPreviousHistory(@PathVariable("device_id") String device_id) {

        if (device_id == null || device_id.isEmpty())
            throw new CustomException("Please provide valid device id to query.", HttpStatus.NOT_ACCEPTABLE);
        UserHistoryDto userHistory = new UserHistoryDto();
        User user = userRepository.getUserbyDeviceId(device_id);

        if (user != null) {
            UserDetailsWithQA details = mapper.map(user, UserDetailsWithQA.class);
            details.setQuestionAnswerHistoryList(englishAnswerPoolRepo.findQuestionAnswerHistory(user.getUserId()));
            userHistory.setUserDetailsWithQA(details);
        }

        userHistory.setWelcomeMessages(userService.getWelcomeMessages("welcome"));

        userHistory.setMessages(userService.getInitialMessages("welcome"));

        return userHistory;
    }

    @GetMapping(value = "/get-detail-change-history")
    public List<UserChangeLogDto> getChangeHistory(@RequestParam("userId") Long userId){
        return  userService.getAllChangeHistory(userId);
    }

    @PostMapping(value = "/rate-answer")
    public EnglishAnswerPool rateAnswer(@Valid @RequestBody RateDto rateDto) {
        return userService.rateAnswer(rateDto);
    }
}
