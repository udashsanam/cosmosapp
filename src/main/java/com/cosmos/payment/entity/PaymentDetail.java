package com.cosmos.payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "tbl_payment_detail")
public class PaymentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "esewa_code")
    private String esewaCode;

    @Column(name = "khalti_code")
    private String khaltiCode;

    @Column(name = "razor_pay_code")
    private String razorPayCode;

    @Column(name = "reson_txt")
    private String resonTxt;

    @Column(name = "is_sucess", nullable = false)
    private Boolean isSuccess = false;

    @Column(name = "esewa_response", columnDefinition = "text")
    private String esewaResponse;

    @Column(name = "razorpay_response", columnDefinition = "text")
    private String razorpayResponse;

    @Column(name = "khalti_response", columnDefinition = "text")
    private String khaltiResponse;

    @Column(name = "is_esewa", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isEsewa = false;

    @Column(name = "is_khalti", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isKhalti = false;

    @Column(name = "is_razor", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRazor = false;

}
