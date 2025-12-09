package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Task_history;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.entity.Employee;

import lombok.Data;

@Data
public class TaskHistoryDTO {
	private Integer historyId;
    private Integer approver;
    private Integer approvalId;
    private Date logtime;

    // 기존 snake_case 접근 호환
    public Integer getApproval_seq() { return approvalId; }

    public Task_history toEntity(Approval_request approval, Workflow workflow, Employee approverEmp) {
		Task_history h = new Task_history(approval, new Date(), workflow, approverEmp);
        return h;
	}
}
