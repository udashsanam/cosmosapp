package com.cosmos.admin.repo;

import com.cosmos.admin.entity.QuestionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionPackageRepo extends JpaRepository<QuestionPackage, Long> {

    List<QuestionPackage> findAllByTargetedCountry(String country);
}
