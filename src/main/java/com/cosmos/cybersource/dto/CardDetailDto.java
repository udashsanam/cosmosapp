package com.cosmos.cybersource.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CardDetailDto {

    private String message;


    private String deviceId;

    private String cardNumber;

    private String exiryDate;

    private String firstName;

    private String lastName;

    private String middleName;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Kathmandu")
    private Timestamp createdDate;

    public CardDetailDto(String successfullySaved) {
        this.message = successfullySaved;
    }
}
