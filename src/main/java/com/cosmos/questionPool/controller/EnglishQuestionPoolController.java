package com.cosmos.questionPool.controller;

import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.service.CreditServiceImpl;
import com.cosmos.questionPool.dto.EnglishUnclearQuestionDto;
import com.cosmos.questionPool.entity.EnglishUnclearQuestion;
import com.cosmos.questionPool.service.EnglishQuestionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eng-question")
public class EnglishQuestionPoolController {
    @Autowired
    private EnglishQuestionPoolService englishQuestionPoolService;

    @Autowired
    private CreditServiceImpl creditService;

    @PutMapping(value = "/skip/{id}", produces = "application/json")
    public ResponseEntity<?> updateQuestionStatus(@PathVariable Long id) {
        englishQuestionPoolService.pushQuestionBackToPool(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/mark-unclear", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HttpStatus> processUnclearQuestion(@RequestBody EnglishUnclearQuestionDto unclearQuestion) {
        Credit credit = new Credit();
        credit.setUserId(unclearQuestion.getAssignedModId());
        credit.setEndUserId(unclearQuestion.getUserId());
        creditService.grantCreditToEndUser(credit);
        englishQuestionPoolService.markUnclearQuestion(unclearQuestion);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
