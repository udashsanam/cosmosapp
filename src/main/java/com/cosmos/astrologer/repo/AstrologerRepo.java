package com.cosmos.astrologer.repo;

import com.cosmos.astrologer.entity.Astrologer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AstrologerRepo extends JpaRepository<Astrologer, Long> {
	Astrologer findByUserId(Long id);
	Astrologer findByEmail(String email);
}
