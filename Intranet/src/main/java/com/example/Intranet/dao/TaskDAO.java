package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.example.Intranet.controller.ResourceConfiguration;
import com.example.Intranet.dto.TaskDTO;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.repository.EmployeeRepository;
import com.example.Intranet.repository.TaskRepository;
import com.example.Intranet.repository.WorkflowRepository;

@Repository
public class TaskDAO {

	@Autowired
	TaskRepository repository;
	
	@Autowired
	EmployeeRepository employeeRepository; 
	
	@Autowired
	WorkflowRepository workflowRepository; 
	
	public Task taskWrite(TaskDTO dto) {
		// Requester 조회 (ID 또는 이메일로)
		Employee Requester = findEmployeeByIdOrEmail(dto.getRequest_id(), dto.getRequestIdStr(), "요청자");

		// Assignee 조회 (ID 또는 이메일로)
		Employee Assignee = findEmployeeByIdOrEmail(dto.getAssignee_id(), dto.getAssigneeIdStr(), "담당자");

		Workflow status = workflowRepository.findById(1)
				.orElseThrow(() -> new RuntimeException("Workflow not found: " + 1));

		return repository.save(dto.toEntity(Requester, Assignee, status));
	}

	// Helper: ID 또는 이메일로 직원 찾기
	private Employee findEmployeeByIdOrEmail(Integer id, String emailOrId, String role) {
		System.out.println("=== findEmployeeByIdOrEmail 호출 ===");
		System.out.println("role: " + role);
		System.out.println("Integer id: " + id);
		System.out.println("String emailOrId: " + emailOrId);

		// 1. Integer ID가 있으면 우선 사용
		if (id != null) {
			System.out.println("Integer ID로 조회 시도: " + id);
			return employeeRepository.findById(id)
					.orElseThrow(() -> new RuntimeException(role + "를 찾을 수 없습니다 (ID: " + id + ")"));
		}

		// 2. 이메일 또는 문자열 ID가 있으면 사용
		if (emailOrId != null && !emailOrId.isEmpty()) {
			// 이메일인지 확인 (@ 포함)
			if (emailOrId.contains("@")) {
				System.out.println("이메일로 조회 시도: " + emailOrId);
				Employee emp = employeeRepository.findByEmail(emailOrId);
				if (emp == null) {
					throw new RuntimeException(role + "를 찾을 수 없습니다 (이메일: " + emailOrId + ")");
				}
				System.out.println("이메일로 조회 성공: emp_no=" + emp.getEmpNo());
				return emp;
			}
			// 문자열 ID인 경우 Integer로 변환 시도
			try {
				Integer parsedId = Integer.parseInt(emailOrId);
				System.out.println("문자열 ID를 Integer로 변환하여 조회 시도: " + parsedId);
				return employeeRepository.findById(parsedId)
						.orElseThrow(() -> new RuntimeException(role + "를 찾을 수 없습니다 (ID: " + parsedId + ")"));
			} catch (NumberFormatException e) {
				throw new RuntimeException(role + " ID 형식이 잘못되었습니다: " + emailOrId);
			}
		}

		throw new RuntimeException(role + " 정보가 제공되지 않았습니다.");
	}
	
	
	public Task taskView(int taskSeq) {
		return repository.findById(taskSeq).orElse(null);
	}

	public List<Task> listByAssignee(Integer assigneeId) {
		System.out.println("=== listByAssignee 호출 ===");
		System.out.println("assigneeId: " + assigneeId);
		// Employee 객체가 아닌 emp_no로 직접 조회
		List<Task> tasks = repository.findByAssigneeIdOrderByTaskSeqDesc(assigneeId);
		System.out.println("조회된 업무 수: " + tasks.size());
		return tasks;
	}

	public List<Task> listByRequester(Integer requesterId) {
		System.out.println("=== listByRequester 호출 ===");
		System.out.println("requesterId: " + requesterId);
		// Employee 객체가 아닌 emp_no로 직접 조회
		List<Task> tasks = repository.findByRequestIdOrderByTaskSeqDesc(requesterId);
		System.out.println("조회된 업무 수: " + tasks.size());
	    return tasks;
	}


	public List<Task> listAll() {
		return repository.findAllByOrderByTaskSeqDesc();
	}
	
}
