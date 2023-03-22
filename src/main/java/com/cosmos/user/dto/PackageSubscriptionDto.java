package com.cosmos.user.dto;

import com.cosmos.admin.entity.QuestionPackage;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
public class PackageSubscriptionDto {
    private Long subsId;

    private Long userId;

    private QuestionPackage packageModel;

    private int allotQuestion;

    private int remainingQuestion;

    private double subsPrice;

    private int subsStatus;

    private Date expireAt;

    private Date createdAt;
}
