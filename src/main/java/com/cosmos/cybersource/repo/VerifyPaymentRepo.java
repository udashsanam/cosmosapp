package com.cosmos.cybersource.repo;


import com.cosmos.cybersource.entity.VerifyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyPaymentRepo extends JpaRepository<VerifyPaymentEntity, Long> {
}
