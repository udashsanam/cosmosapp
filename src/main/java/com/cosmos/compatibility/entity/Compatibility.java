package com.cosmos.compatibility.entity;

import com.cosmos.common.model.AuditModel;
import com.cosmos.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tbl_compatibility")
public class Compatibility extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "compatibility_id")
    private Long compatibilityId;

    private String name;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "birth_time")
    private String birthTime;

    private String gender;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "partner_name")
    private String partnerName;

    @Column(name = "partner_birth_date")
    private String partnerBirthDate;

    @Column(name = "partner_birth_time")
    private String partnerBirthTime;

    @Column(name = "partner_birth_place")
    private String partnerBirthPlace;

    @Column(name = "partner_gender")
    private String partnerGender;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_COMPATIBILITY_USER"))
    private User user;

    private String deviceId;

    @Column(columnDefinition = "text")
    private String response;

    private int status;
}
