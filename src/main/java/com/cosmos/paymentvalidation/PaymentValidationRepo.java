package com.cosmos.paymentvalidation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentValidationRepo extends JpaRepository<PaymentValidation, Long> {
}
