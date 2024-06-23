package com.cosmos.astromode.controller;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.astromode.dto.AstroModeReplyToUser;
import com.cosmos.astromode.service.IAstroModeService;
import com.cosmos.common.exception.CustomException;
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
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.user.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/astromode")
public class AstroModeController {

    private final IAstroModeService iAstroModeService;
    private final EnglishQuestionPoolRepo englishQuestionPoolRepo;
    private final NepaliAnswerPoolRepo nepaliAnswerPoolRepo;



    public AstroModeController(IAstroModeService iAstroModeService, EnglishQuestionPoolRepo englishQuestionPoolRepo,
                               NepaliAnswerPoolRepo nepaliAnswerPoolRepo) {
        this.iAstroModeService = iAstroModeService;
        this.englishQuestionPoolRepo = englishQuestionPoolRepo;


        this.nepaliAnswerPoolRepo = nepaliAnswerPoolRepo;
    }


    @PostMapping("/answer-to-user")
    public EnglishAnswerPool answerToUser(@RequestBody AstroModeReplyToUser astroModeReplyToUser){
        if (astroModeReplyToUser.getTranslatedAns() == null || astroModeReplyToUser.getTranslatedAns().equals("")) {
            throw new CustomException("Please provide a  answer.", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            EnglishQuestionPool englishQuestionPool = englishQuestionPoolRepo.getOne(astroModeReplyToUser.getEngQuesId());
            if (englishQuestionPool == null) {
                throw new CustomException("No Question found under this id: " + astroModeReplyToUser.getEngQuesId(), HttpStatus.NOT_FOUND);
            }
            if (englishQuestionPool != null) {
                englishQuestionPool.setAssignedAstroModId(getCurrentUserId());
                englishQuestionPool.setQuestionStatus(QuestionStatus.Clear);
                englishQuestionPoolRepo.save(englishQuestionPool);
            }
            return iAstroModeService.saveFinalAnswer(astroModeReplyToUser, englishQuestionPool.getUserId());
    }}



    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }


    @GetMapping("/fetch-task")
    public QuestionAnswerPoolForModerator getUnAssignedQuestionFromPool() {
        return iAstroModeService.fetchAstroModeCurrentJob();
    }


    @PostMapping(value = "/ua-astromode/post", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EnglishAnswerPool convertNepReplyToEng(@RequestBody AstrologerReplyToEng astroRep) {
        if (astroRep.getTranslatedAns() == null || astroRep.getTranslatedAns().equals("")) {
            throw new CustomException("Please provide a translated answer.", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            NepaliAnswerPool reply = nepaliAnswerPoolRepo.getOneByNepQuestionIdAndUserId(astroRep.getNepQuestionId(), astroRep.getUserId());
            if (reply != null) {
                reply.setAstroModeId(getCurrentUserId());
                reply.setStatus(QuestionStatus.Clear);
                nepaliAnswerPoolRepo.save(reply);
            }
            return iAstroModeService.storeTranslatedReply(astroRep, reply.getId());
        }
    }


}
