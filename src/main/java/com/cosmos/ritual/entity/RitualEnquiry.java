package com.cosmos.ritual.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_ritual_enquiry")
@Data
@NoArgsConstructor
public class RitualEnquiry extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ritual_id")
    private Long ritualId;

    @Column(name = "fk_end_user_id")
    private Long endUserId;

    @Column(name = "enquiry_message", columnDefinition = "text")
    private String enquiryMessage;

    @Column(name = "fk_user_id")
    private Long userId;
}
