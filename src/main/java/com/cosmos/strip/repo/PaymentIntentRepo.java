package com.cosmos.strip.repo;

import com.cosmos.strip.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentIntentRepo extends JpaRepository<PaymentIntent, Long> {

    PaymentIntent findByClientSecret(String clientSecret);
}
