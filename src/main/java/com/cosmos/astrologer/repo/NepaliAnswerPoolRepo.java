package com.cosmos.astrologer.repo;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.projection.AstrologerReplyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NepaliAnswerPoolRepo extends JpaRepository<NepaliAnswerPool, Long> {
    NepaliAnswerPool getOneByNepQuestionIdAndUserId(Long nepQuesId,Long userId);

    @Query(value = "SELECT * FROM tbl_answers_from_astrologer WHERE ques_sts=1 ORDER BY created_at ASC LIMIT 0,1", nativeQuery = true)
    NepaliAnswerPool getUnassignedQA();

    @Query(value = "SELECT * FROM tbl_answers_from_astrologer nr WHERE nr.fk_mod_id=?1 AND nr.ques_sts=0 LIMIT 0,1", nativeQuery = true)
    NepaliAnswerPool selectModeratorUnfinishedAnswer(Long modId);

    @Query(value = "SELECT nepR.id AS nepAnswerId, nepR.answer as nepReply, " +
            "nepR.created_at AS repliedOn, CONCAT(users.first_name,' ',users.last_name) as repliedBy " +
            "FROM tbl_answers_from_astrologer nepR " +
            "INNER JOIN tbl_nep_ques_pool nepQ " +
            "ON nepQ.nep_ques_id = nepR.fk_nep_ques_id " +
            "INNER JOIN tbl_users users " +
            "ON users.user_id = nepQ.fk_astro_id " +
            "WHERE nepR.fk_nep_ques_id = ?1", nativeQuery = true)
    AstrologerReplyProjection selectNepReplyByNepQuestionId(Long id);

    @Query(value = "SELECT count(eng_ans_id) as dailyAstrologerWorkCount FROM tbl_final_question_answer WHERE DATE(created_at) = CURDATE()",
            nativeQuery = true)
    Integer selectDailyAstrologerWorkCount();


    @Query(value = "SELECT * FROM tbl_answers_from_astrologer nr WHERE nr.fk_astro_mod_id=?1 AND nr.ques_sts=0 LIMIT 0,1", nativeQuery = true)
    NepaliAnswerPool selectAstoModeratorUnfinishedAnswer(Long modId);
}
