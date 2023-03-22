package com.cosmos.questionPool.entity;

import com.cosmos.common.model.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
@Table(name = "tbl_nep_ques_pool")
public class NepaliQuestionPool extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "nep_ques_id")
    @Setter(AccessLevel.NONE)
    private Long nepQuesId;

    @Column(name = "nep_question", columnDefinition = "TEXT")
    @Lob
    private String nepQuestion;

    @Column(name = "fk_astro_id")
    @JsonIgnore
    private Long assignedAstroId;

    @Column(name = "fk_eng_qsn_id")
    private Long engQsnId;

    // status can be(Unassigned, Assigned, Unclear, Cleared)
    @Column(name = "ques_sts")
    private QuestionStatus questionStatus;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "mark_unclear", columnDefinition = "tinyint(1)")
    @JsonIgnore
    private boolean markQuestionUnclear;
}
