package com.cosmos.questionPool.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_eng_unclear_question")
@Data
@NoArgsConstructor
public class EnglishUnclearQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "eng_ques_id")
    private Long engQuestionId;

    @Column(name = "description")
    @Lob
    private String description;

    @Column(name = "fk_mod_id")
    private Long assignedModId;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "sent_sts")
    private boolean sentStatus;

    @Column(name = "failure_msg", columnDefinition = "text")
    private String failureMsg;
}
