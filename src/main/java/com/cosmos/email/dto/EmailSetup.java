package com.cosmos.email.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class EmailSetup {
    private String bcc;
    private String emailTemplate;
    private String fromName;
    private String replyToEmail;
    private String subjectLine;
    private boolean sendEmail;
}
