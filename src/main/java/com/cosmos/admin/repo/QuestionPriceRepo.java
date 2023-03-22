package com.cosmos.admin.repo;

import com.cosmos.admin.entity.QuestionPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionPriceRepo extends JpaRepository<QuestionPrice, Long> {

    @Query(value = "select * from tbl_question_price order by created_at desc limit 0,1", nativeQuery = true)
    QuestionPrice selectLatestPrice();

    @Query(value = "select * from tbl_question_price where country_iso=:x order by created_at desc limit 0,1", nativeQuery = true)
    QuestionPrice selectLatestPriceByCountry(@Param("x") String country);
}
