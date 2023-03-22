package com.cosmos.questionPool.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class EnglishQuestionDto {
    private Long engQuesId;
    @NotBlank(message = "Question is mandatory")
    private String engQuestion;
    @NotNull(message = "Userid is mandatory")
    private Long userId;
    @NotNull(message = "Question Price cannot be null")
    private Double questionPrice;
    private Long prevEngQuesId;
}
