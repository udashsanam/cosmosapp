package com.cosmos.questionPool.controller;

import com.cosmos.common.exception.CustomException;
import com.cosmos.questionPool.dto.NepaliQuestionDto;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.NepaliQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/nep-question")
public class NepaliQuestionPoolController {
    private EnglishQuestionPoolRepo engQsnRepo;
    private NepaliQuestionPoolRepo nepQsnRepo;

    @Autowired
    public NepaliQuestionPoolController(EnglishQuestionPoolRepo engQsnRepo, NepaliQuestionPoolRepo nepQsnRepo) {
        this.engQsnRepo = engQsnRepo;
        this.nepQsnRepo = nepQsnRepo;
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

}
