package com.cosmos.quickSupport.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_reported_issue")
@Data
@NoArgsConstructor
public class ReportedIssue extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "issue_id")
    private Long issueId;

    @Column(name = "end_user_id")
    private Long endUserId;

    @Column(name = "email")
    private String email;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "reply", columnDefinition = "text")
    private String reply;

    @Column(name = "status")
    private int status;
}
