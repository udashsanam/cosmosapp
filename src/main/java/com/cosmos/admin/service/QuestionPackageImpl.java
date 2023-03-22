package com.cosmos.admin.service;

import com.cosmos.admin.entity.QuestionPackage;
import com.cosmos.admin.repo.QuestionPackageRepo;
import com.cosmos.admin.service.QuestionPackageService;
import com.cosmos.common.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionPackageImpl implements QuestionPackageService {
    @Autowired
    QuestionPackageRepo packageRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public QuestionPackage registerPackage(QuestionPackage qp) {
        return packageRepo.save(qp);
    }

    @Override
    public List<QuestionPackage> getAllPackages() {
        return packageRepo.findAll();
    }

    @Override
    public QuestionPackage updatePackage(Long id, QuestionPackage pk) {
        QuestionPackage qp = null;
        if (packageRepo.findById(id).isPresent()) {
            qp = packageRepo.findById(id).get();
        }
        if (qp == null) {
            throw new CustomException("No moderator found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        pk.setId(qp.getId());
        pk.setCountryISO(qp.getCountryISO());
        pk.setCreatedAt(qp.getCreatedAt());
        return modelMapper.map(packageRepo.save(pk), QuestionPackage.class);
    }

    @Override
    public List<QuestionPackage> getQuestionPackageByCountry(String country) {
        return packageRepo.findAllByTargetedCountry(country);
    }

    @Override
    public QuestionPackage removePackage(Long id) {
        QuestionPackage qp;
        if (packageRepo.findById(id).isPresent()) {
            qp = packageRepo.findById(id).get();
            packageRepo.delete(qp);
        } else {
            throw new CustomException("No package found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        return qp;
    }
}
