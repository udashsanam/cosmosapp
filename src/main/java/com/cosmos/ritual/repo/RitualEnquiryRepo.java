package com.cosmos.ritual.repo;

import com.cosmos.ritual.entity.RitualEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface RitualEnquiryRepo extends JpaRepository<RitualEnquiry, Long> {

    List<RitualEnquiry> findAllByEndUserId(Long userId);

}
