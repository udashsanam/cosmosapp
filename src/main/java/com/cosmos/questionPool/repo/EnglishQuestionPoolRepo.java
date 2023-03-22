package com.cosmos.questionPool.repo;

import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.projection.EnglishQuestionProjection;
import com.cosmos.questionPool.projection.MonthlyRevenueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishQuestionPoolRepo extends JpaRepository<EnglishQuestionPool, Long> {

    @Query("SELECT eq FROM EnglishQuestionPool eq WHERE eq.questionStatus=1")
    List<EnglishQuestionPool> findAllUnAssignedQuestion();

    @Query(value = "SELECT * FROM tbl_eng_ques_pool eq WHERE eq.ques_sts=1 ORDER BY eq.created_at ASC LIMIT 0,1", nativeQuery = true)
    EnglishQuestionPool findUnAssignedQuestion();

    @Query(value = "SELECT * FROM tbl_eng_ques_pool eq WHERE eq.fk_mod_id=?1 AND ques_sts=0 LIMIT 0,1", nativeQuery = true)
    EnglishQuestionPool selectModeratorUnfinishedQuestion(Long moderatorId);

    @Query(value = "SELECT eng_ques_id AS engQuestionId, eng_question AS engQuestion, ques_sts AS questionStatus, created_at AS createdAt FROM " +
            "tbl_eng_ques_pool eq " +
            "WHERE eq.fk_user_id = ?1 " +
            "AND " +
            "eq.ques_sts != 1 ORDER BY eq.created_at",
            nativeQuery = true)
    List<EnglishQuestionProjection> selectPrevEngQuestionOfUser(Long userId);

    @Query(value = "SELECT count(eng_ques_id) as dailyQuestionCount FROM tbl_eng_ques_pool WHERE DATE(created_at) = CURDATE()",
    nativeQuery = true)
    Integer selectDailyQuestionCount();

    @Query(value = "SELECT count(eng_ques_id) as dailyFreeQuesCount FROM tbl_eng_ques_pool WHERE DATE(created_at) = CURDATE() AND question_price = 0",
            nativeQuery = true)
    Integer selectDailyFreeQuestionCount();

    @Query(value = "SELECT sum(question_price) as dailyRevenue FROM tbl_eng_ques_pool WHERE DATE(created_at) = CURDATE()",
        nativeQuery = true)
    Double selectDailyRevenue();

    @Query(value = "SELECT count(eng_ques_id) as dailyUnclearQuestionCount FROM tbl_eng_ques_pool WHERE DATE(created_at) = CURDATE() AND ques_sts = 3",
            nativeQuery = true)
    Integer selectDailyUnclearQuestionCount();

    @Query(value = "select year(created_at) as year,month(created_at) as month,sum(question_price) as revenue\n" +
            "     from tbl_eng_ques_pool\n" +
            "     where year(created_at) = ?1\n" +
            "     group by year(created_at),month(created_at)\n" +
            "     order by year(created_at),month(created_at);",
            nativeQuery = true)
    List<MonthlyRevenueReport> selectMonthlyRevenue(Integer year);
}
