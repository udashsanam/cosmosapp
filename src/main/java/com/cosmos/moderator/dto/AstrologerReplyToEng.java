package com.cosmos.moderator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AstrologerReplyToEng {

    private Long nepQuestionId;
//    private String question;
    private Long userId;
//    private String kl;
    private String translatedAns;
    private Long moderatorId;
}
