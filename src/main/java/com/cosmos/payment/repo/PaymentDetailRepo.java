package com.cosmos.payment.repo;

import com.cosmos.payment.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepo extends JpaRepository<PaymentDetail, Long> {

    PaymentDetail findByEsewaCode(String esewaCode);

    PaymentDetail findByKhaltiCode(String khaltiCode);
}
