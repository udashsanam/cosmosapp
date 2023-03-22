package com.cosmos.admin.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_question_price")
@Data
@NoArgsConstructor
public class QuestionPrice extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "question_price")
    @NotNull(message = "Question Price Cannot be Null")
    private double questionPrice;

    @Column(name = "discount")
    private double discountInPercentage;

    @Column(name = "country_iso", length = 10)
    private String countryISO;
}
