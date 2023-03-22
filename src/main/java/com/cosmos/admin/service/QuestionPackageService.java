package com.cosmos.admin.service;

import com.cosmos.admin.entity.QuestionPackage;

import java.util.List;

public interface QuestionPackageService {
    QuestionPackage registerPackage(QuestionPackage qp);

    List<QuestionPackage> getAllPackages();

    QuestionPackage updatePackage(Long id, QuestionPackage pk);

    List<QuestionPackage> getQuestionPackageByCountry(String country);

    QuestionPackage removePackage(Long id);
}
