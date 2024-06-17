package com.cosmos.astrologer.entity;

import com.cosmos.common.model.AuditModel;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tbl_answers_from_astrologer")
public class NepaliAnswerPool extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "fk_nep_ques_id")
    private Long nepQuestionId;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @JsonIgnore
    @Column(name = "fk_mod_id")
    private Long moderatorId;

    @JsonIgnore
    @Column(name = "ques_sts")
    private QuestionStatus status;

    @JsonIgnore
    @Column(name = "fk_astro_mod_id")
    private Long astroModeId;
}
