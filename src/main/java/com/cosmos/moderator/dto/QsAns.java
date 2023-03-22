package com.cosmos.moderator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QsAns {
    private String question;
    private String answer;
    private String mode;
    private Date response_date;
}
