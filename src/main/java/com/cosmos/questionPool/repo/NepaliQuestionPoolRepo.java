package com.cosmos.questionPool.repo;

import com.cosmos.questionPool.entity.NepaliQuestionPool;
import com.cosmos.questionPool.projection.NepaliQuestionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NepaliQuestionPoolRepo extends JpaRepository<NepaliQuestionPool, Long> {
    NepaliQuestionPool findByEngQsnId(Long engQsnId);

    @Query(value = "SELECT * FROM tbl_nep_ques_pool WHERE fk_astro_id=0 AND ques_sts=1 ORDER BY created_at ASC LIMIT 0,1", nativeQuery = true)
    NepaliQuestionPool findUnAssignedQuestion();

    List<NepaliQuestionPool> findAllByUserId(Long userId);

    @Query(value = "SELECT nepQ.nep_ques_id AS nepQuestionId, nepQ.nep_question as translatedQuestion, " +
            "nepQ.created_at AS translatedOn, CONCAT(users.first_name,' ',users.last_name) as translatedBy, " +
            "case when engQ.fk_astro_mod_id is not null then 'astromod' else null end as role " +
            "FROM tbl_nep_ques_pool nepQ " +
            "INNER JOIN tbl_eng_ques_pool engQ " +
            "ON nepQ.fk_eng_qsn_id = engQ.eng_ques_id " +
            "INNER JOIN tbl_users users " +
            "ON users.user_id = engQ.fk_mod_id or  users.user_id = engQ.fk_astro_mod_id " +
            "WHERE nepQ.fk_eng_qsn_id = ?1", nativeQuery = true)
    NepaliQuestionProjection selectTranslatedEngQuestionByEngQuestionId(Long id);

    @Query(value = "SELECT * FROM tbl_nep_ques_pool WHERE fk_astro_id=:id and ques_sts=0 LIMIT 0,1", nativeQuery = true)
    NepaliQuestionPool findAllUncompletedTaskByAstroId(@Param("id") Long id);
}
