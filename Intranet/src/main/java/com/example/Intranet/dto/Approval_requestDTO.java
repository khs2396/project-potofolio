package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task;

import lombok.Data;

@Data
public class Approval_requestDTO {
	 private Integer approval_seq;       //결제요청 번호
	 private String subject;             //제목
	 private String approval_content;    //내용
	 private String file_name;           //파일명
	 private Integer task_seq;		     //업무번호
	 private Integer requester_id;       //결제 요청자
	 private Date approval_requested_at; //결제요청일
	 private Integer approver;            //결제 승인자
	 
	 
	 public Approval_request toEntity(Task task, Employee requester_id, Employee approver) {
		return new Approval_request(subject, approval_content, file_name, requester_id, approval_requested_at, task, approver);
	}
}
