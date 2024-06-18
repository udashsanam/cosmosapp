package com.cosmos.questionPool.entity;

import com.cosmos.common.model.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tbl_final_question_answer")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnglishAnswerPool extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "eng_ans_id")
    private Long englishAnswerId;

    @Column(name = "fk_nep_ans_id")
    private Long answerId;

    @Column(name = "answer")
    @Lob
    private String answer;

    @Column(name = "fk_eng_qsn_id")
    private Long questionId;

    @Column(name = "eng_question")
    @Lob
    private String question;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_astro_id")
    private Long astroId;



    @Column(name = "fk_mod_eng_nep_qsn")
    private Long engToNepQsnMod;

    @Column(name = "fk_astro_mod_eng_nep_qsn")
    private Long engToNepQsnAstroMod;

    @Column(name = "fk_mod_nep_eng_rep")
    private Long nepToEngRepMod;

    @Column(name = "fk_astro_mod_nep_eng_rep")
    private Long nepToEngRepAstroMod;

    @Column(name = "sent_status", columnDefinition = "tinyint(1)")
    private boolean sentStatus;

    @Column(name = "failure_msg", length = 300)
    private String failureMsg;

    @Column(name = "message_id")
    private String messageId;

    @ColumnDefault(value = "'CLEAR'")
    @Column(name = "question_status", length = 10)
    private String questionStatus;

    @Column(name = "fk_astro_mod_id")
    private Long astroModId;



}
