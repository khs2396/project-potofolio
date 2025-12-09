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



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
	 @Id
	 @GeneratedValue(generator = "TASK_SEQUENCE_GENERATOR", strategy = GenerationType.SEQUENCE)
	 @SequenceGenerator(name = "TASK_SEQUENCE_GENERATOR", sequenceName = "seq_tasks", initialValue = 1, allocationSize = 1)
	 @Column(name = "task_seq")
	 private Integer taskSeq;        //업무번호
	 
	 @ManyToOne
	 @JoinColumn(name = "requester_id")
	 private Employee requestId;       //요청자(상사)
	 
	 @ManyToOne
	 @JoinColumn(name = "assignee_id")
	 private Employee assigneeId;      //담당자(직원)
	 
	 private String subject;          //제목
	 @Lob
	 @Column(name = "task_content")
	 private String taskContent;     //내용
	 
	 @Column(name = "file_name", nullable = true)
	 private String fileName;        //파일명	 
	 
	 @Column(name = "create_at")
	 @Temporal(TemporalType.DATE)   
	 private Date createAt;          //작성일
	 
	 @ManyToOne
	 @JoinColumn(name = "status_seq")
	 private Workflow status;        //업무 상태
	 
	 
	 public Task(Employee request_id, Employee assignee_id, String subject,
             String task_content, String file_name, Date create_at, Workflow status) {

	     this.requestId = request_id;
	     this.assigneeId = assignee_id;
	     this.subject = subject;
	     this.taskContent = task_content;
	     this.fileName = file_name; 
	     this.createAt = create_at;
	     this.status = status;
	 }	 
}
