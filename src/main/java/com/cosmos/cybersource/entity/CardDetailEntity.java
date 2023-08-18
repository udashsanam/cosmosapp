package com.cosmos.cybersource.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_detail")
public class CardDetailEntity {

    @Id
    @SequenceGenerator(name = "card_details_seq", allocationSize = 1, initialValue = 1,sequenceName = "card_details_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_details_seq")
    private Long id;

    private String deviceId;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Kathmandu")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss" )
    private Timestamp createdDate= new Timestamp(System.currentTimeMillis());


    private String cardNumber;

    private String exiryDate;

    private String firstName;

    private String lastName;

    private String middleName;





}
