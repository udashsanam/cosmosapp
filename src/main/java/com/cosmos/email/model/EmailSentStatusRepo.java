package com.cosmos.email.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailSentStatusRepo extends JpaRepository<EmailSentStatus, Long> {
}
