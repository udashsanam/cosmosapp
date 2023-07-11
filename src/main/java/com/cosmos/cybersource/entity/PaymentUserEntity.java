package com.cosmos.cybersource.entity;

import com.cosmos.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_user")
public class PaymentUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_user_id")
    private User user;

    @Column(name = "auth_trans_ref_no")
    private Long authTransRefNo;

    @Column(name = "transaction_uuid")
    private String transactionUUID;

    @Column(name = "is_success")
    private Boolean isSuccess;
}
