package com.cosmos.questionPool.repo;

import com.cosmos.questionPool.entity.EnglishUnclearQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnclearQuestionRepo extends JpaRepository<EnglishUnclearQuestion, Long> {
}
