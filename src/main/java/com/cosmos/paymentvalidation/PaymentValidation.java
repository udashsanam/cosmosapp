package com.cosmos.paymentvalidation;

import com.cosmos.paymentvalidation.enums.PaymentMethodEnum;
import com.cosmos.paymentvalidation.enums.PaymentStatus;
import lombok.Data;

import javax.persistence.*;
import javax.print.attribute.standard.MediaSize;

@Entity
@Table(name = "tbl_payment_validation")
@Data
public class PaymentValidation {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "fk_user_Id")
    private Long userId;


}
