package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.TaskHistoryDTO;
import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task_history;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.repository.Approval_requestRepository;
import com.example.Intranet.repository.EmployeeRepository;
import com.example.Intranet.repository.TaskHistoryRepository;
import com.example.Intranet.repository.WorkflowRepository;

@Repository
public class TaskHistoryDAO {
	@Autowired
	TaskHistoryRepository repository;
	
	@Autowired
	WorkflowRepository workflowRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	Approval_requestRepository approval_requestRepository;
	
	// TaskHistory 등록 (DTO 기반)
	public Task_history historyWrite(TaskHistoryDTO dto) {
		Employee approver = employeeRepository.findById(dto.getApprover())
				.orElseThrow(() -> new RuntimeException("Employee not found: " + dto.getApprover()));
		
		Approval_request approval_seq = approval_requestRepository.findById(dto.getApproval_seq())
				.orElseThrow(() -> new RuntimeException("Approval_request not found: " + dto.getApproval_seq()));
		
		Workflow status = workflowRepository.findById(3)
				.orElseThrow(() -> new RuntimeException("Workflow not found: " + 3));
		
		int currentStatus = approval_seq.getTask().getStatus().getStatusseq();
		if(currentStatus == 3) {
			throw new IllegalStateException("이미 승인된 문서입니다.");
		}
		
		
		return repository.save(dto.toEntity(approval_seq, status, approver));
	}

	// TaskHistory 등록 (엔티티 직접)
	public Task_history save(Task_history history) {
		return repository.save(history);
	}
	
//	// task 테이블의 statusId 반영
//	public void updateTaskStatusByApprovalId(Integer approval_id, Integer status_id) {
//		repository.updateTaskStatusByApprovalId(approval_id, status_id);
//	}
//	
//	// approvalId별 최신 이력 1건
//	public Task_history historyView(Integer approval_id) {
//		return repository.findTopByApprovalseqOrderByHistoryseqDesc(approval_id);
//	}
//	
//	// approvalId별 상태 포함 단건 뷰
//	public TaskHistoryViewDTO taskHistoryView(Integer approval_id) {
//		return repository.findHistoryWithStatusName(approval_id);
//	}
//	
//	// 전체 이력 목록
//	public List<TaskHistoryViewDTO> taskHistoryList(){
//		return repository.findAllHistoryWithStatusName();
//	}
//
//	// 결재자 기준 목록
//	public List<TaskHistoryViewDTO> historyByApprover(Integer approval_id, Integer status_id){
//		return repository.findHistoryByApprover(approval_id, status_id);
//	}
//	
//	// 요청자 기준 목록
//	public List<TaskHistoryViewDTO> historyByRequester(Integer requester_id, Integer status_id){
//		return repository.findHistoryByRequester(requester_id, status_id);
//	}
//
//	// 결재 요청별 전체 이력
//	public List<Task_history> historyListByApproval(Integer approval_id){
//		return repository.findByApprovalidOrderByHistoryidDesc(approval_id);
//	}
}
