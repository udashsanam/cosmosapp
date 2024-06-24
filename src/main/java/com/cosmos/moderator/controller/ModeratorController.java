package com.cosmos.moderator.controller;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.service.CreditServiceImpl;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.moderator.dto.AstrologerReplyToEng;
import com.cosmos.moderator.dto.CurrentJobForModerator;
import com.cosmos.moderator.dto.QuestionAnswerPoolForModerator;
import com.cosmos.moderator.service.ModeratorService;
import com.cosmos.notification.model.NotificationResponse;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.NepaliQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import com.cosmos.questionPool.service.EnglishQuestionPoolService;
import com.cosmos.user.dto.UserQuestionAnswerHistory;
import com.cosmos.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {
    private NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    private NepaliQuestionPoolRepo nepQsnRepo;
    private EnglishQuestionPoolService englishQuestionPoolService;
    private EnglishAnswerPoolRepo finalRepo;
    private ModeratorService moderatorService;
    private UserServiceImpl userService;
    private CreditServiceImpl creditService;

    @Autowired
    public ModeratorController(NepaliAnswerPoolRepo nepaliAnswerPoolRepo, NepaliQuestionPoolRepo nepQsnRepo,
                               EnglishQuestionPoolService englishQuestionPoolService,
                               EnglishAnswerPoolRepo finalRepo, ModeratorService moderatorService,
                               UserServiceImpl userService, CreditServiceImpl creditService) {
        this.nepaliAnswerPoolRepo = nepaliAnswerPoolRepo;
        this.nepQsnRepo = nepQsnRepo;
        this.englishQuestionPoolService = englishQuestionPoolService;
        this.finalRepo = finalRepo;
        this.moderatorService = moderatorService;
        this.userService = userService;
        this.creditService = creditService;
    }

    @GetMapping(value = "/current-job", produces = "application/json")
    public QuestionAnswerPoolForModerator getUnAssignedQuestionFromPool() {
        QuestionAnswerPoolForModerator questionAnswerPoolForModerator = new QuestionAnswerPoolForModerator();

        CurrentJobForModerator currentJobForModerator = new CurrentJobForModerator();

        // First Find previously unfinished task. i.e if moderator has not submitted
        // assigned task system should first provide him his previous assigned question.
        QuestionAnswerPoolForModerator previousAssignedTask = moderatorService.findModeratorUnfinishedTask();
        if (previousAssignedTask != null) {
            return previousAssignedTask;
        }

        // find if there is unassigned astrologer reply
        NepaliAnswerPool nepaliAnswerPool = nepaliAnswerPoolRepo.getUnassignedQA();
        if (nepaliAnswerPool != null) {
            System.out.println("inside nepali answer pool != null");
            CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            nepaliAnswerPool.setStatus(QuestionStatus.Assigned);
            nepaliAnswerPool.setModeratorId(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
            nepaliAnswerPoolRepo.save(nepaliAnswerPool);

            currentJobForModerator.setCurrentJobType("nepali-answer");
            currentJobForModerator.setNepaliAnswer(nepaliAnswerPool);

            questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
            questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(nepaliAnswerPool.getUserId()));
            questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(nepaliAnswerPool.getUserId()));
            return questionAnswerPoolForModerator;
        }

        //find if this is unassigned unclear astrologer reply
        NepaliAnswerPool unclearNepaliAnswerPool = nepaliAnswerPoolRepo.getUnClearQA();
        if(unclearNepaliAnswerPool !=null){
            System.out.println("inside unclear answer pool != null");
            CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            unclearNepaliAnswerPool.setStatus(QuestionStatus.Unclear);
            unclearNepaliAnswerPool.setModeratorId(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
            nepaliAnswerPoolRepo.save(unclearNepaliAnswerPool);

            currentJobForModerator.setCurrentJobType("unclear-answer");
            currentJobForModerator.setNepaliAnswer(unclearNepaliAnswerPool);

            questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
            questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(unclearNepaliAnswerPool.getUserId()));
            questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(unclearNepaliAnswerPool.getUserId()));
            return questionAnswerPoolForModerator;
        }

        // Otherwise find unassigned question and return to moderator and mark this question dirty
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolService.findTopUnAssignedQuestionFromPool();
        englishQuestionPoolService.markQuestionDirty(englishQuestionPool);

        currentJobForModerator.setCurrentJobType("english-question");
        currentJobForModerator.setEnglishQuestion(englishQuestionPool);

        questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
        questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(englishQuestionPool.getUserId()));
        questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(englishQuestionPool.getUserId()));
        return questionAnswerPoolForModerator;
    }

    @GetMapping(value = "/user-task-history", produces = "application/json")
    public List<UserQuestionAnswerHistory> getUserTaskHistory() {
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolService.findTopUnAssignedQuestionFromPool();
        englishQuestionPoolService.markQuestionDirty(englishQuestionPool);

        return userService.findPrevQuestionHistoryOfUser(englishQuestionPool.getUserId());
    }

    @PostMapping(value = "/ua-astrologerReply/post", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EnglishAnswerPool convertNepReplyToEng(@RequestBody AstrologerReplyToEng astroRep) {
        if (astroRep.getTranslatedAns() == null || astroRep.getTranslatedAns().equals("")) {
            throw new CustomException("Please provide a translated answer.", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            NepaliAnswerPool reply = nepaliAnswerPoolRepo.getOneByNepQuestionIdAndUserId(astroRep.getNepQuestionId(), astroRep.getUserId());
            if (reply != null) {
                reply.setModeratorId(getCurrentUserId());
                reply.setStatus(QuestionStatus.Clear);
                nepaliAnswerPoolRepo.save(reply);
            }
            return storeTranslatedReply(astroRep, reply.getId());
        }
    }

    @GetMapping(value = "/grant-credit/{endUserId}", produces = "application/json")
    public Credit convertNepReplyToEng(@PathVariable Long endUserId) {
        Credit credit = new Credit();
        credit.setEndUserId(endUserId);
        credit.setUserId(getCurrentUserId());
        return creditService.grantCreditToEndUser(credit);
    }

    private EnglishAnswerPool storeTranslatedReply(AstrologerReplyToEng astroRep, Long answerId) {
        /*fetch question from nepaliQuestionPool*/
        NepaliQuestionPool nepQsn = nepQsnRepo.findById(astroRep.getNepQuestionId())
                .orElseThrow(() -> new CustomException("No question pool found.", HttpStatus.NOT_FOUND));
        EnglishQuestionPool engQsn = englishQuestionPoolService.findQuestionById(nepQsn.getEngQsnId());

        /*check if this question has already an entry*/
        EnglishAnswerPool queryWithReply = finalRepo.getOneByQuestionId(nepQsn.getEngQsnId());
        if (queryWithReply == null) {
            EnglishAnswerPool qwr = new EnglishAnswerPool();
            qwr.setQuestionId(nepQsn.getEngQsnId());
            qwr.setQuestion(engQsn.getEngQuestion());
            qwr.setAnswerId(answerId);
            qwr.setAnswer(astroRep.getTranslatedAns());

            qwr.setUserId(astroRep.getUserId());
            qwr.setAstroId(nepQsn.getAssignedAstroId());

            qwr.setEngToNepQsnMod(engQsn.getAssignedModId());
            qwr.setNepToEngRepMod(getCurrentUserId());

            qwr.setCreatedAt(new Date());
            qwr.setUpdatedAt(new Date());

            NotificationResponse notificationResponse = userService.sendAnswerToUserViaNotification(astroRep.getUserId(), astroRep.getTranslatedAns(), nepQsn.getAssignedAstroId(), nepQsn.getEngQsnId());

            qwr.setSentStatus(notificationResponse.isSuccess());
            qwr.setMessageId(notificationResponse.getMessageId());
            qwr.setFailureMsg(notificationResponse.getFailureMsg());

            return finalRepo.save(qwr);
        } else {
            queryWithReply.setAnswer(astroRep.getTranslatedAns());
            queryWithReply.setUpdatedAt(new Date());
            return finalRepo.save(queryWithReply);
        }
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }
}
