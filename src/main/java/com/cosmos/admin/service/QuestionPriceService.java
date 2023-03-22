package com.cosmos.admin.service;

import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.admin.repo.QuestionPriceRepo;
import com.cosmos.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionPriceService {

    @Autowired
    private QuestionPriceRepo questionPriceRepo;

    public QuestionPrice saveQuestionPrice(QuestionPrice questionPrice) {
        return questionPriceRepo.save(questionPrice);
    }

    public QuestionPrice fetchCurrentQuestionPrice() {
        return questionPriceRepo.selectLatestPrice();
    }

    public QuestionPrice fetchCurrentQuestionPriceByCountry(String country) {
        return questionPriceRepo.selectLatestPriceByCountry(country);
    }

    public QuestionPrice fetchCurrentQuesPriceForUser(User user) {
        QuestionPrice questionPrice = fetchCurrentQuestionPrice();
        if (user.getFirstLogin())
            questionPrice.setDiscountInPercentage(75);

        return questionPrice;
    }
}
