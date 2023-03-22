package com.cosmos.moderator.repo;

import com.cosmos.moderator.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeratorRepo extends JpaRepository<Moderator, Long> {
    Moderator findByUserId(Long id);
    Moderator findByEmail(String email);
}
