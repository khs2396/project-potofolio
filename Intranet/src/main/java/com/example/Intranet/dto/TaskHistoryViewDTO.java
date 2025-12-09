package com.example.Intranet.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskHistoryViewDTO {
	private Integer historyId;  // historySeq를 받음
	private Integer approver;   // approver.empNo를 받음
	private Integer approvalId; // approval.approvalSeq를 받음
	private String statusName;  // status.statusname을 받음
	private Date logtime;       // approvalCompletedAt을 받음
}
