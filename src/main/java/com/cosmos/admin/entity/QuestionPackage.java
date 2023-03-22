package com.cosmos.admin.entity;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_package")
@Data
@NoArgsConstructor
public class QuestionPackage extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "package_id")
    private String packageId;

    @Column(name = "package_name")
    @NotNull(message = "Package name cannot be null")
    private String packageName;

    @Column(name = "package_type")
    @NotNull(message = "Package type cannot be null")
    private String packageType;

    @Column(name = "package_description", columnDefinition = "text")
    private String packageDescription;

    @Column(name = "package_price")
    @NotNull(message = "Package price cannot be null")
    private double packagePrice;

    @Column(name = "package_discount", columnDefinition = "integer default 0")
    @NotNull
    private int packageDiscount;
    /**
     *  [Question Package] is for number of question offer to the user after buying the specific package.
     **/
    @Column(name = "question_package", columnDefinition = "integer default 0")
    @NotNull
    private int questionPackage;

    @Column(name = "targeted_country")
    private String targetedCountry = "International";

    @Column(name = "country_iso", length = 10)
    private String countryISO = "ISO";
}
