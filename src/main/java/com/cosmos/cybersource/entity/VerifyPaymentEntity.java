package com.cosmos.cybersource.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "verify_payment")
public class VerifyPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "auth_trans_ref_no")
    private Long authTransRefNo;

    @Column(name = "reference_number")
    private Long referenceNumber;

    @Column(name = "access_key")
    private String accessKey;

    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "transaction_uuid")
    private String transactionUUID;


    @Column(name = "amount")
    private Double amount;

    @Column(name = "decision")
    private String decision;

    @Column(name = "reason_code")
    private String reasonCode;



}
