package com.cosmos.astrologer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionerDetailsResponse {
    private boolean success;
    private QuestionerDetails questionerDetails;
}
