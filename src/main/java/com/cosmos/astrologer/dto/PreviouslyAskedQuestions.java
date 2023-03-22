package com.cosmos.astrologer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreviouslyAskedQuestions {
    private Long questionId;
    private String question;
    //can be null too
    private String answer;
    private Long userId;
}
