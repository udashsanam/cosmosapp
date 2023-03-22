package com.cosmos.credit.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_credit")
@Data
@NoArgsConstructor
public class Credit extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "credit_id")
    private Long creditId;

    @Column(name = "credit_count",length = 10)
    private int creditCount;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_end_user_id")
    private Long endUserId;

    @Column(name = "credit_status" , columnDefinition = "tinyint(1)")
    private boolean creditStatus;
}
