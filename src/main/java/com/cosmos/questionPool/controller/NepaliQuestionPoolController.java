package com.cosmos.questionPool.controller;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.credit.entity.Credit;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.questionPool.dto.EnglishUnclearQuestionDto;
import com.cosmos.questionPool.dto.NepaliQuestionDto;
import com.cosmos.questionPool.dto.NepaliUnclearQuestionDto;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.NepaliQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import com.cosmos.questionPool.service.NepaliQuestionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/nep-question")
public class NepaliQuestionPoolController {
    private final NepaliQuestionPoolService nepaliQuestionPoolService;
    private final NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    private EnglishQuestionPoolRepo engQsnRepo;
    private NepaliQuestionPoolRepo nepQsnRepo;

    @Autowired
    public NepaliQuestionPoolController(EnglishQuestionPoolRepo engQsnRepo, NepaliQuestionPoolRepo nepQsnRepo, NepaliQuestionPoolService nepaliQuestionPoolService, NepaliAnswerPoolRepo nepaliAnswerPoolRepo) {
        this.engQsnRepo = engQsnRepo;
        this.nepQsnRepo = nepQsnRepo;
        this.nepaliQuestionPoolService = nepaliQuestionPoolService;
        this.nepaliAnswerPoolRepo = nepaliAnswerPoolRepo;
    }

    @GetMapping(value = "/translatedQuestion/get", produces = "application/json")
    public NepaliQuestionPool getConvertedQuestion() {
        /*Get all translated question unassigned and having astroId 0*/
        return nepQsnRepo.findUnAssignedQuestion();
    }

    @PostMapping(value = "/translatedQuestion/post", consumes = "application/json", produces = "application/json")
    public NepaliQuestionPool convertEngQsnToNepQsn(@RequestBody NepaliQuestionDto nepQsnDto) {

        if (nepQsnDto.getConvertedQsn() == null || nepQsnDto.getConvertedQsn().equalsIgnoreCase("")) {
            throw new CustomException("Please provide a converted question!", HttpStatus.NO_CONTENT);
        } else {
            /*make eng question status clear*/
            EnglishQuestionPool engQuestion = engQsnRepo.findById(nepQsnDto.getEngQsnId())
                    .orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND));
            engQuestion.setQuestionStatus(QuestionStatus.Clear);
            engQsnRepo.save(engQuestion);
            return createNewConvertedQuestion(nepQsnDto);
        }
    }

    private NepaliQuestionPool createNewConvertedQuestion(@RequestBody NepaliQuestionDto nepQsnDto) {
        /*check if translated question already exist*/
        NepaliQuestionPool doExist = nepQsnRepo.findByEngQsnId(nepQsnDto.getEngQsnId());
        if (doExist == null) {
            /*store the converted question*/
            NepaliQuestionPool pool = new NepaliQuestionPool();
            pool.setNepQuestion(nepQsnDto.getConvertedQsn());
            pool.setUserId(nepQsnDto.getUserId());
            pool.setQuestionStatus(QuestionStatus.UnAssigned);
            pool.setEngQsnId(nepQsnDto.getEngQsnId());
            pool.setAssignedAstroId(0L);
            pool.setCreatedAt(new Date());
            pool.setUpdatedAt(new Date());
            return nepQsnRepo.save(pool);
        } else {
            doExist.setUpdatedAt(new Date());
            doExist.setNepQuestion(nepQsnDto.getConvertedQsn());
            return nepQsnRepo.save(doExist);
        }
    }

    @PostMapping(value = "/mark-unclear", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HttpStatus> processUnclearQuestion(@RequestBody NepaliUnclearQuestionDto unclearQuestion) {
        if (unclearQuestion.getDescription().equalsIgnoreCase("") || unclearQuestion.getDescription() == null) {
            throw new CustomException("Answer field is mandatory!", HttpStatus.NOT_FOUND);
        } else {
            /*Set question as assigned along with assigned astrologer Id*/
            NepaliQuestionPool nepQsn = nepQsnRepo.findById(unclearQuestion.getNepaliQuestionId())
                    .orElseThrow(() -> new CustomException("No question found.", HttpStatus.NOT_FOUND));
            nepQsn.setAssignedAstroId(getCurrentUserId());
            nepQsn.setQuestionStatus(QuestionStatus.Unclear);
            nepQsnRepo.save(nepQsn);

            /*Store the question's answer*/
            /*Check if question has already been answered*/
            NepaliAnswerPool replyFromAstrologer = nepaliAnswerPoolRepo.getOneByNepQuestionIdAndUserId(unclearQuestion.getNepaliQuestionId(), unclearQuestion.getUserId());
            if (replyFromAstrologer != null) {
                throw new CustomException("Question has already been answered.", HttpStatus.CONFLICT);
            } else {
                NepaliAnswerPool reply = new NepaliAnswerPool();
                reply.setNepQuestionId(unclearQuestion.getNepaliQuestionId());
                reply.setUserId(unclearQuestion.getUserId());
                reply.setAnswer(unclearQuestion.getDescription());
                reply.setStatus(QuestionStatus.Unclear);
                nepaliAnswerPoolRepo.save(reply);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }




}
