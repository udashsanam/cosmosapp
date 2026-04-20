package com.cosmos.meta.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_registration_flow")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationFlowEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "tbl_registration_flow_gen")
    @SequenceGenerator(name = "tbl_registration_flow_gen", sequenceName = "tbl_registration_flow_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "flow_step", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FlowStep flowStep;

    @Column(name = "response")
    private String response;

    @Column(name = "created_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Builder.Default
    private Date createdAt= new Date();

    @Column(name = "updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    @Column(name = "is_current_state",nullable = false)
    private Boolean isCurrentState;

    @Column(name = "latest_flow", nullable = false)
    private Boolean latestFlow;


}
