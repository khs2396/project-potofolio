package com.example.Intranet.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.example.Intranet.dto.TaskDTO;
import com.example.Intranet.dto.TaskViewDTO;
import com.example.Intranet.entity.Task;
import com.example.Intranet.service.TaskService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class TaskController {
	@Autowired
	TaskService service;

	@Autowired
	com.example.Intranet.repository.EmployeeRepository employeeRepository;

	@Value("${project.upload.path}")
	private String uploadpath;
	
	
	@GetMapping("/taskView")
	public Map<String, Object> taskWriteForm(HttpServletRequest request) {
		int taskId = Integer.parseInt(request.getParameter("taskId"));
		
		Task result = service.taskView(taskId);
		
		System.out.println("result = " + result);
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(result != null) {
			map.put("result", result);
			}
		else map.put("rt", "FAIL");
		
		return map;
	
	}
	
	
	// 업무 생성
		@PostMapping("/taskWriteForm")
        public Map<String, Object> taskWriteForm(TaskDTO dto, @RequestParam(value = "file", required = false)MultipartFile uploadFile) {
			
			System.out.println("dto = " + dto);
			
			
			Map<String, Object> map = new HashMap<String, Object>();
					
			try {
				Task result = service.createTask(dto,uploadFile);

				// 결과 응답
				if (result != null) {
					map.put("rt", "OK");
					map.put("taskSeq", result.getTaskSeq());
				} else
					map.put("rt", "FAIL");

			} catch (Exception e) {
				e.printStackTrace();
				map.put("rt", "FAIL");
				map.put("message", "파일 저장 중 오류 발생");
				return map;
			}

			return map;
		}
	
	
	
	// 업무 목록 (assignee / requester 필터)
	@GetMapping("/tasks")
	public ResponseEntity<List<TaskViewDTO>> listTasks(
			@RequestParam(value = "assignee_id", required = false) String assignee_id_str,
			@RequestParam(value = "requester_id", required = false) String requester_id_str) {

		System.out.println("=== /tasks 엔드포인트 호출 ===");
		System.out.println("assignee_id_str: " + assignee_id_str);
		System.out.println("requester_id_str: " + requester_id_str);

		// String → Integer 변환 (이메일 지원)
		Integer assignee_id = parseEmployeeId(assignee_id_str);
		Integer requester_id = parseEmployeeId(requester_id_str);

		System.out.println("assignee_id (parsed): " + assignee_id);
		System.out.println("requester_id (parsed): " + requester_id);

		List<Task> results;
		if (assignee_id != null) {
			results = service.listByAssignee(assignee_id);
			System.out.println("assignee_id_result = " + results);
		} else if (requester_id != null) {
			results = service.listByRequester(requester_id);
			System.out.println("requester_id_result = " + results);
		} else {
			results = service.tasksAll();
		}

		List<TaskViewDTO> dtos = results.stream().map(task -> new TaskViewDTO(
		        task.getTaskSeq(),
		        task.getSubject(),
		        task.getTaskContent(),
		        task.getFileName(),
		        task.getCreateAt() != null ? task.getCreateAt().toString() : "",
		        task.getRequestId() != null ? task.getRequestId().getName() : "-",
		        task.getAssigneeId() != null ? task.getAssigneeId().getName() : "-",
		        task.getStatus() != null ? task.getStatus().getStatusseq() : null
		    )).collect(Collectors.toList());

		System.out.println("dtos = " + dtos);
		return ResponseEntity.ok(dtos);
	}
	
	// 업무 상세 (REST)
	@GetMapping("/tasks/view")
	public ResponseEntity<TaskViewDTO> getTask(@RequestParam(value = "taskSeq", required = false) String taskSeqStr,
										@RequestParam(value = "taskId", required = false) String taskIdStr) {
		System.out.println("taskSeq param: " + taskSeqStr);
		System.out.println("taskId param: " + taskIdStr);

		Integer id = null;
		try {
			if (taskSeqStr != null && !taskSeqStr.equals("undefined") && !taskSeqStr.isEmpty()) {
				id = Integer.parseInt(taskSeqStr);
			} else if (taskIdStr != null && !taskIdStr.equals("undefined") && !taskIdStr.isEmpty()) {
				id = Integer.parseInt(taskIdStr);
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid number format: " + e.getMessage());
		}

		if (id == null) {
			System.out.println("No valid task ID provided");
			return ResponseEntity.badRequest().build();
		}

		Task result = service.taskView(id);

		System.out.println("result = " + result);
		if (result == null) {
			return ResponseEntity.notFound().build();
		}

		// Task를 TaskViewDTO로 변환
		TaskViewDTO dto = new TaskViewDTO(
			result.getTaskSeq(),
			result.getSubject(),
			result.getTaskContent(),
			result.getFileName(),
			result.getCreateAt() != null ? result.getCreateAt().toString() : "",
			result.getRequestId() != null ? result.getRequestId().getName() : "-",
			result.getAssigneeId() != null ? result.getAssigneeId().getName() : "-",
			result.getStatus() != null ? result.getStatus().getStatusseq() : null
		);

		return ResponseEntity.ok(dto);
	}

	// 업무 파일 다운로드
	@GetMapping("/download/task/{taskSeq}")
	public ResponseEntity<org.springframework.core.io.Resource> downloadTaskFile(@org.springframework.web.bind.annotation.PathVariable("taskSeq") Integer taskSeq) {
		try {
			// 업무 정보 조회
			Task task = service.taskView(taskSeq);
			if (task == null || task.getFileName() == null || task.getFileName().isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			// 파일 경로
			String fileName = task.getFileName();
			java.nio.file.Path filePath = java.nio.file.Paths.get(uploadpath, fileName);
			org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				System.out.println("파일을 찾을 수 없습니다: " + filePath);
				return ResponseEntity.notFound().build();
			}

			// 파일 다운로드 응답 (UTF-8 파일명 안전 처리)
			String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
			return ResponseEntity.ok()
					.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + encoded + "\"")
					.header(org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
					.body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	// Helper: String(이메일 또는 사번)을 Integer(emp_no)로 변환
	private Integer parseEmployeeId(String idStr) {
		if (idStr == null || idStr.isEmpty()) {
			return null;
		}

		// 이메일인 경우
		if (idStr.contains("@")) {
			System.out.println("이메일로 조회 시도: " + idStr);
			com.example.Intranet.entity.Employee emp = employeeRepository.findByEmail(idStr);
			if (emp == null) {
				System.out.println("Employee not found for email: " + idStr);
				return null;
			}
			System.out.println("Found employee: " + emp.getEmpNo() + " for email: " + idStr);
			return emp.getEmpNo();
		}

		// 숫자 문자열인 경우
		try {
			return Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			System.out.println("Invalid employee ID format: " + idStr);
			return null;
		}
	}
}
