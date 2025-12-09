package com.example.Intranet.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task_history {
	@Id
	@GeneratedValue(generator = "TASKHISTORY_SEQUENCE_GENERATOR", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "TASKHISTORY_SEQUENCE_GENERATOR", sequenceName = "SEQ_TASKHISTORY", initialValue = 1, allocationSize = 1)
	@Column(name = "history_seq")
	private Integer historySeq;
    
	@ManyToOne
	@JoinColumn(name = "approver")
	private Employee approver;
    
    @ManyToOne
    @JoinColumn(name = "approval_seq")
    private Approval_request approval;
    
    @ManyToOne
    @JoinColumn(name = "status_seq")
    private Workflow status;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "approval_completed_at")
    private Date approvalCompletedAt;
    
    public Task_history(Approval_request approval_seq, Date approval_completed_at, Workflow status, Employee approver) {
		this.approver = approver;
    	this.approval = approval_seq;
    	this.status = status;
    	this.approvalCompletedAt = approval_completed_at;
	}
}
