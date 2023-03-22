package com.cosmos.credit.repo;

import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.credit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditRepo extends JpaRepository<Credit, Long> {
    List<Credit> findAllByEndUserIdAndAndCreditStatus(Long userId,boolean status);

    @Query(value = "select * from tbl_credit where fk_end_user_id=:x and credit_status=:y order by created_at asc limit 0,1 ", nativeQuery = true)
    Credit selectOldestCredit(@Param("x") long endUserId, @Param("y") boolean status);
}
