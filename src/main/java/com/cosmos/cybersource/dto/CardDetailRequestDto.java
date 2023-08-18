package com.cosmos.cybersource.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDetailRequestDto {



    private String deviceId;

    private String cardNumber;

    private String exiryDate;

    private String firstName;

    private String lastName;

    private String middleName;
}
