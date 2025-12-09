package com.example.Intranet.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Approval_request {
	@Id
	@GeneratedValue(generator = "APPROVAL_SEQUENCE_GENERATOR",  strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "APPROVAL_SEQUENCE_GENERATOR", sequenceName = "seq_approval", initialValue = 1, allocationSize = 1)
	@Column(name = "approval_seq")
	private Integer approvalSeq;
	private String subject;
	@Lob
	@Column(name = "approval_content")
	private String approvalContent;
	@Column(name = "file_name", nullable = true)
	private String fileName;
	
	@ManyToOne
	@JoinColumn(name = "task_seq")
	private Task task;
	
	@ManyToOne
	@JoinColumn(name = "requester_id")
	private Employee requesterId;
	@Temporal(TemporalType.DATE)
	@Column(name = "approval_requested_at")
	private Date approvalRequestedAt;
	
	@ManyToOne
	@JoinColumn(name = "approver")
	private Employee approver;
	
	public Approval_request(String subject, String approval_content, String file_name, Employee requester_id, Date approval_requested_at, Task task, Employee approver){
		this.subject = subject;
		this.approvalContent = approval_content;
		this.fileName = file_name;
		this.task = task;
		this.requesterId = requester_id;
		this.approvalRequestedAt = approval_requested_at;
		this.approver = approver;
	}
}

