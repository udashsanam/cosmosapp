package com.cosmos.questionPool.repo;

import com.cosmos.astrologer.projection.AstrologerWorkReport;
import com.cosmos.moderator.dto.ModeratorWorkReport;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.projection.EnglishReplyProjection;
import com.cosmos.questionPool.projection.QuestionAnswerHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishAnswerPoolRepo extends JpaRepository<EnglishAnswerPool, Long> {
    EnglishAnswerPool getOneByQuestionId(Long engQsnId);

    @Query(value = "SELECT engR.answer as engReply, " +
            "engR.created_at AS translatedOn, CONCAT(users.first_name,' ',users.last_name) as translatedBy " +
            "FROM tbl_final_question_answer engR " +
            "INNER JOIN tbl_answers_from_astrologer nepR " +
            "ON nepR.id = engR.fk_nep_ans_id " +
            "INNER JOIN tbl_users users " +
            "ON users.user_id = nepR.fk_mod_id " +
            "WHERE engR.fk_nep_ans_id = ?1", nativeQuery = true)
    EnglishReplyProjection selectEngReplyByNepAnswerId(Long id);

    List<EnglishAnswerPool> findAllByEngToNepQsnMod(Long modId);

    List<EnglishAnswerPool> findAllByNepToEngRepMod(Long modId);

    List<EnglishAnswerPool> findAllByAstroId(Long modId);

    @Query(value = "SELECT engQues.eng_question as engQuestion, answer, concat(u.first_name, u.last_name) as repliedBy, u.img_url as profileImgUrl, engQues.ques_sts as status,\n" +
            "engQues.created_at as createdAt\n" +
            "FROM tbl_eng_ques_pool engQues\n" +
            "left JOIN tbl_final_question_answer fn\n" +
            "ON engQues.eng_ques_id = fn.fk_eng_qsn_id\n" +
            "left join tbl_users u \n" +
            "on u.user_id = fn.fk_astro_id or u.user_id = fn.fk_astro_mod_id \n" +
            "where engQues.fk_user_id=?1 order by engQues.created_at"
            , nativeQuery = true,
    countQuery = "select count(*) FROM tbl_eng_ques_pool engQues\n" +
            "left JOIN tbl_final_question_answer fn\n" +
            "ON engQues.eng_ques_id = fn.fk_eng_qsn_id\n" +
            "left join tbl_users u \n" +
            "on u.user_id = fn.fk_astro_id or u.user_id = fn.fk_astro_mod_id \n" +
            "where engQues.fk_user_id=?1 ")
    Page<QuestionAnswerHistory> findQuestionAnswerHistoryOfUser(Long userId, Pageable pageable);
    
    /*
         const searchUrl = `${this.baseUrl}/previous-history/{device_id}?id=${UserId}`
                    + `&page=${thePage}&size=${thePageSize}`;
     */

    @Query(value = "SELECT count(id) as dailyModeratorWorkCount FROM tbl_answers_from_astrologer WHERE DATE(created_at) = CURDATE()",
            nativeQuery = true)
    Integer selectDailyModeratorWorkCount();

    @Query(value = "SELECT \n" +
            "COUNT(fqa.eng_ans_id) AS workCount, fqa.fk_astro_id AS astroId, CONCAT(u.first_name, ' ', u.last_name) AS fullName, u.email as email\n" +
            "FROM tbl_final_question_answer AS fqa\n" +
            "INNER JOIN\n" +
            "tbl_users AS u\n" +
            "ON\n" +
            "u.user_id = fqa.fk_astro_id\n" +
            "WHERE DATE(created_at) BETWEEN ?1 AND ?2\n" +
            "group by fk_astro_id;",
            nativeQuery = true)
    List<AstrologerWorkReport> selectAstrologerWorkReport(String fromDate, String toDate);

    @Query(value = "SELECT \n" +
            "COUNT(fqa.eng_ans_id) AS workCount, fqa.fk_mod_nep_eng_rep AS modId, CONCAT(u.first_name, ' ', u.last_name) AS fullName, u.email as email\n" +
            "FROM tbl_final_question_answer AS fqa\n" +
            "INNER JOIN\n" +
            "tbl_users AS u\n" +
            "ON\n" +
            "u.user_id = fqa.fk_mod_nep_eng_rep\n" +
            "WHERE DATE(created_at) BETWEEN ?1 AND ?2\n" +
            "group by fk_mod_nep_eng_rep",
            nativeQuery = true)
    List<ModeratorWorkReport> selectModeratorWorkReport(String fromDate, String toDate);


    @Query(value = "SELECT engQues.eng_question as engQuestion, answer, concat(u.first_name, u.last_name) as repliedBy, u.img_url as profileImgUrl, engQues.ques_sts as status,\n" +
            "engQues.created_at as createdAt\n" +
            "FROM tbl_eng_ques_pool engQues\n" +
            "left JOIN tbl_final_question_answer fn\n" +
            "ON engQues.eng_ques_id = fn.fk_eng_qsn_id\n" +
            "left join tbl_users u \n" +
            "on u.user_id = fn.fk_astro_id\n" +
            "where engQues.fk_user_id=?1 order by engQues.created_at desc "
            , nativeQuery = true)
    List<QuestionAnswerHistory> findQuestionAnswerHistory(Long userId);


    @Query(value = "SELECT engR.answer as engReply,\n" +
            "            engR.created_at AS translatedOn, CONCAT(users.first_name,' ',users.last_name) as translatedBy, \n" +
            "'astromod' as role" +
            "            FROM tbl_final_question_answer engR\n" +
            "            inner JOIN tbl_users users\n" +
            "            ON users.user_id = engR.fk_astro_mod_id\n" +
            "            WHERE engR.fk_eng_qsn_id = ?1 ", nativeQuery = true)
    EnglishReplyProjection selectEngReplyByEnglishQuestionId(Long id);

}
