package com.cosmos.questionPool.entity;

import com.cosmos.common.model.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_eng_ques_pool")
@ToString
public class EnglishQuestionPool extends AuditModel {
    // TODO extend AuditModel
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "eng_ques_id")
    private Long engQuesId;

    @Column(name = "eng_question")
    @Lob
    private String engQuestion;

    @Column(name = "fk_mod_id")
    private Long assignedModId;

    // status can be(Unassigned, Assigned, Unclear, Cleared)
    @Column(name = "ques_sts")
    @JsonIgnore
    private QuestionStatus questionStatus;

    // who asked this
    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "question_price")
    private Double questionPrice;

    // if additional question of previously asked unclear question
    @Column(name = "prev_ques_id")
    private Long prevEngQuesId;
}
