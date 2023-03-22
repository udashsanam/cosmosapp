package com.cosmos.email.model;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_email_sent_sts")
@Data
@NoArgsConstructor
public class EmailSentStatus extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private boolean success;

    @Column(columnDefinition = "text")
    private String failureMessage;

    public EmailSentStatus(String email, boolean success, String failureMessage) {
        this.email = email;
        this.success = success;
        this.failureMessage = failureMessage;
    }

}
