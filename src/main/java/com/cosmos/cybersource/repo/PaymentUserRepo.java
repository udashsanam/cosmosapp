package com.cosmos.cybersource.repo;

import com.cosmos.cybersource.entity.PaymentUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface PaymentUserRepo extends JpaRepository<PaymentUserEntity, Long> {

    PaymentUserEntity findByAuthTransRefNoAndTransactionUUID(Long authId, String transactionUUID);
}
