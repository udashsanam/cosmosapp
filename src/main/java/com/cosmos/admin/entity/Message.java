package com.cosmos.admin.entity;

import javax.persistence.*;

import com.cosmos.common.model.AuditModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_message")
@Data
@NoArgsConstructor
@ToString
public class Message extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "message_id")
	private Long messageId;

	@Column(name = "msg_text")
	@Lob
	private String text;

	// type includes: welcome-message, system-message
	@Column(name = "msg_type")
	private String type;

	@Column(name = "send_msg", columnDefinition = "tinyint(1)")
	private boolean sendMessage;
}
