package com.cosmos.quickSupport.repo;

import com.cosmos.quickSupport.entity.ReportedIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportedIssueRepo extends JpaRepository<ReportedIssue, Long> {
    List<ReportedIssue> findAllByEndUserId(Long userId);

    ReportedIssue findByIssueId(Long issueId);
}
