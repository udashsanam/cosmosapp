package com.cosmos.user.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_package_subscription")
@Data
@NoArgsConstructor
public class PackageSubscription extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subs_id")
    private Long subsId;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_package_id")
    private Long packageId;

    @Column(name = "allot_question")
    private int allotQuestion = 0;

    @Column(name = "remaining_question")
    private int remainingQuestion;

    @Column(name = "subs_price")
    private double subsPrice;

    @Column(name = "subs_status")
    private int subsStatus = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expire_at", nullable = false)
    private Date expireAt;

}
