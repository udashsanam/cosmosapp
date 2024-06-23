package com.cosmos.questionPool.service;

import com.cosmos.admin.entity.Message;
import com.cosmos.admin.repo.MessageRepo;
import com.cosmos.astromode.enitity.AstroModeEntity;
import com.cosmos.astromode.repo.AstroModeRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.repo.CreditRepo;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.Role;
import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.moderator.service.ModeratorService;
import com.cosmos.notification.model.Notification;
import com.cosmos.notification.model.NotificationDataPayload;
import com.cosmos.notification.model.NotificationResponse;
import com.cosmos.notification.service.NotificationService;
import com.cosmos.questionPool.dto.EnglishQuestionDto;
import com.cosmos.questionPool.dto.EnglishUnclearQuestionDto;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.EnglishUnclearQuestion;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.repo.UnclearQuestionRepo;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.W3CDomHandler;
import java.util.Collection;
import java.util.List;

@Service
public class EnglishQuestionPoolService {
    @Autowired
    private EnglishQuestionPoolRepo englishQuestionPoolRepo;
    @Autowired
    private EnglishAnswerPoolRepo englishAnswerPoolRepo;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ModeratorService moderatorService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UnclearQuestionRepo unclearQuestionRepo;
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    CreditRepo creditRepo;

    @Autowired
    private AstroModeRepo astroModeRepo;


    public EnglishQuestionPool findQuestionById(Long id) {
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolRepo.getOne(id);

        if (englishQuestionPool == null) {
            throw new CustomException("No Question found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        return englishQuestionPool;
    }

    public EnglishQuestionPool addQuestionToPool(EnglishQuestionDto englishQuestionDto) {
        UserDto user = userService.findUserDetailsById(englishQuestionDto.getUserId());
        if (user == null)
            throw new CustomException("Invalid UserId! No any user found under this id: " + englishQuestionDto.getUserId(), HttpStatus.NOT_FOUND);
        List<EnglishQuestionPool> engQ = englishQuestionPoolRepo.findAll();
        if (engQ.isEmpty()) {
//            Message msg=messageRepo.selectMessageByUserGuide();
            EnglishAnswerPool answerPool = new EnglishAnswerPool();
            englishAnswerPoolRepo.save(answerPool);
        }
        EnglishQuestionPool englishQuestionPool = mapper.map(englishQuestionDto, EnglishQuestionPool.class);
        englishQuestionPool.setQuestionStatus(QuestionStatus.UnAssigned);

//         ... >>>       Send Notification to User

//        Notification notification = new Notification("");

        return englishQuestionPoolRepo.save(englishQuestionPool);
    }

    public List<EnglishQuestionPool> findAllUnAssignedQuestionFromPool() {
        return englishQuestionPoolRepo.findAllUnAssignedQuestion();
    }

    public EnglishQuestionPool findTopUnAssignedQuestionFromPool() {

        // FIFO POLICY
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolRepo.findUnAssignedQuestion();

        //1. Find List of Previously Asked Question By This User whose status is clear/unclear.
        //2. Iterate the list to find related translated question, answer from moderator and translated answer.

        if (englishQuestionPool == null) {
            throw new CustomException("Currently no task in system", HttpStatus.NOT_FOUND);
        }

        return englishQuestionPool;
    }

    // Assigns question to moderator
    public EnglishQuestionPool markQuestionDirty(EnglishQuestionPool englishQuestionPool) {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        englishQuestionPool.setQuestionStatus(QuestionStatus.Assigned);
        englishQuestionPool.setAssignedModId(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
        return englishQuestionPoolRepo.save(englishQuestionPool);
    }

    // Assigns question to moderator
    public EnglishQuestionPool assignToAstroModerator(EnglishQuestionPool englishQuestionPool) {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        englishQuestionPool.setQuestionStatus(QuestionStatus.Assigned);
        englishQuestionPool.setAssignedAstroModId(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
        return englishQuestionPoolRepo.save(englishQuestionPool);
    }

    // skip functionality for moderator
    public void pushQuestionBackToPool(Long questionId) {
        EnglishQuestionPool englishQuestionPool = findQuestionById(questionId);
        englishQuestionPool.setAssignedModId(null);
        englishQuestionPool.setQuestionStatus(QuestionStatus.UnAssigned);
        englishQuestionPoolRepo.save(englishQuestionPool);
    }


    public void markUnclearQuestion(EnglishUnclearQuestionDto englishUnclearQuestionDto) {
        String role = getRole();
        String fullname = null;
        String profile = null;
        if(Role.ROLE_MODERATOR.toString().equals(role)){
            ModeratorDto moderatorDetails = moderatorService.findModeratorById(getCurrentUserId());
            fullname = moderatorDetails.getFirstName() + " " + moderatorDetails.getLastName();
            profile = moderatorDetails.getProfileImageUrl();
        }else {
            AstroModeEntity astroModeEntity = astroModeRepo.findByUserId(getCurrentUserId());
            fullname = astroModeEntity.getFirstName() + " " + astroModeEntity.getLastName();
            profile = astroModeEntity.getProfileImageUrl();
        }

        NotificationDataPayload notificationForUnclearQuestion = new NotificationDataPayload();
        notificationForUnclearQuestion.setEngQuestionId(englishUnclearQuestionDto.getEngQuestionId().toString());
        notificationForUnclearQuestion.setStatus("UNCLEAR");
        notificationForUnclearQuestion.setMessage(englishUnclearQuestionDto.getDescription());

        notificationForUnclearQuestion.setRepliedBy(fullname);
        notificationForUnclearQuestion.setProfileImgUrl(profile);

        Notification notification = new Notification("Your recent question is vague. Please ask another question.", "Unclear Question", "FLUTTER_NOTIFICATION_CLICK");

//        EnglishUnclearQuestion englishUnclearQuestion = mapper.map(englishUnclearQuestionDto, EnglishUnclearQuestion.class);

        EnglishAnswerPool answerPool = new EnglishAnswerPool();
        answerPool.setAnswer(englishUnclearQuestionDto.getDescription());
        answerPool.setQuestionId(englishUnclearQuestionDto.getEngQuestionId());
        answerPool.setUserId(englishUnclearQuestionDto.getUserId());
        answerPool.setQuestionStatus("UNCLEAR");

        UserDto user = userService.findUserDetailsById(englishUnclearQuestionDto.getUserId());

        if (user.getDeviceToken() == null) {
            throw new CustomException("Cannot Send notification. Device Token is null", HttpStatus.SERVICE_UNAVAILABLE);
        }

        NotificationResponse notificationResponse = notificationService.sendPushNotification(user.getDeviceToken(), notification, notificationForUnclearQuestion);

        answerPool.setMessageId(notificationResponse.getMessageId());
        answerPool.setSentStatus(notificationResponse.isSuccess());
        answerPool.setFailureMsg(notificationResponse.getFailureMsg());

//              englishUnclearQuestion.setSentStatus(notificationResponse.isSuccess());
//              englishUnclearQuestion.setFailureMsg(notificationResponse.getFailureMsg());
//              englishUnclearQuestion.setMessageId(notificationResponse.getMessageId());

//        if(!notificationResponse.isSuccess()) {
//
//            throw new CustomException(notificationResponse.getFailureMsg(), HttpStatus.SERVICE_UNAVAILABLE);
//        }

        // marking this english question Unclear
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolRepo.getOne(englishUnclearQuestionDto.getEngQuestionId());
        englishQuestionPool.setQuestionStatus(QuestionStatus.Unclear);
        englishQuestionPoolRepo.save(englishQuestionPool);

        answerPool.setQuestion(englishQuestionPool.getEngQuestion());
        englishAnswerPoolRepo.save(answerPool);


//        EnglishUnclearQuestion savedEnglishUnclearQuestion = unclearQuestionRepo.save(englishUnclearQuestion);
//        return savedEnglishUnclearQuestion;
    }

    private String getRole() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<GrantedAuthority> simpleGrantedAuthorities =  currentlyLoggedInUser.getAuthorities();
        return simpleGrantedAuthorities.iterator().next().getAuthority();
    }


    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }

}
