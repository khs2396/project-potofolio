package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.Approval_requestDTO;
import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.repository.Approval_requestRepository;
import com.example.Intranet.repository.EmployeeRepository;
import com.example.Intranet.repository.TaskRepository;
import com.example.Intranet.repository.WorkflowRepository;




@Repository
public class Approval_requestDAO {
	@Autowired
	Approval_requestRepository repository;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	WorkflowRepository workflowRepository;
	
	//결제 테이블에 저장
	public Approval_request ApprovalWrite(Approval_requestDTO dto) {
		Task task = taskRepository.findById(dto.getTask_seq())
				.orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTask_seq()));
		Employee requestId = employeeRepository.findById(dto.getRequester_id())
				.orElseThrow(() -> new RuntimeException("Employee not found: " + dto.getRequester_id()));
		
		Employee approver = employeeRepository.findById(dto.getApprover())
				.orElseThrow(() -> new RuntimeException("Employee not found: " + dto.getApprover()));
		
		
		Workflow status = workflowRepository.findById(2)
				.orElseThrow(() -> new RuntimeException("Workflow not found: " + 2));
		
		// 이미 진행 중이어도 재상신을 허용하고 상태를 진행중으로 맞춘다.
		task.setStatus(status);
		taskRepository.save(task);
		return repository.save(dto.toEntity(task, requestId, approver));
	}
	
	
	// 단건 조회
	public  Approval_request approvalView(Integer approval_seq) {
		return repository.findById(approval_seq).orElse(null);
	}

		// 요청자 기준 목록
		public List<Approval_request> listByRequester(Integer requesterId){
			Employee emp = employeeRepository.findById(requesterId)
					.orElseThrow(() -> new RuntimeException("Employee not found: " + requesterId));
			return repository.findByRequesterIdOrderByApprovalSeqDesc(emp);
		}

		// 전체 목록
		public List<Approval_request> listAll(){
			return repository.findAllByOrderByApprovalSeqDesc();
		}
	
	
	
	
	
	
}
