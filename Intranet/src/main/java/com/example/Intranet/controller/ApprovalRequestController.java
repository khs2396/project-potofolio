package com.example.Intranet.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.Intranet.controller.SessionConst;
import com.example.Intranet.dto.Approval_requestDTO;
import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task_history;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.service.Approval_requestService;
import com.example.Intranet.service.EmployeeService;
import com.example.Intranet.repository.TaskHistoryRepository;
import com.example.Intranet.repository.WorkflowRepository;
import com.example.Intranet.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class ApprovalRequestController {

	@Autowired
	private Approval_requestService service;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private TaskHistoryRepository taskHistoryRepository;

	@Autowired
	private WorkflowRepository workflowRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private com.example.Intranet.repository.EmployeeRepository employeeRepository;

	@Value("${project.upload.path2}")
	private String uploadpath2;
	
	// 결재 요청 생성
	@PostMapping("/approvals")
	public Map<String, Object> createApproval(Approval_requestDTO dto, @RequestParam(value = "file2", required = false) MultipartFile uploadFile){
		Map<String, Object> map = new HashMap<String, Object>();	
		
		try {		
			Approval_request result = service.createApproval(dto, uploadFile);
		if (result != null) {
			map.put("rt", "OK");
			map.put("approvalId", result.getApprovalSeq());
		} else {
			map.put("rt", "FAIL");
		}
		}catch (IllegalStateException e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", e.getMessage());	
			return map;		
	    }catch (Exception e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", "파일 저장 중 오류 발생");	
			return map;
		}
		return map;
	}
	
	// 결재 목록
	@GetMapping("/approvals")
	public ResponseEntity<List<Approval_request>> listApprovals(
			@RequestParam(value = "requester_id", required = false) String requesterId_str) {
		// String → Integer 변환 (이메일 지원)
		Integer requesterId = parseEmployeeId(requesterId_str);

		List<Approval_request> results;
		if (requesterId != null) {
			results = service.listByRequester(requesterId);
		} else {
			results = service.listAll();
		}
		return ResponseEntity.ok(results);
	}

	// 결재 상세
	@GetMapping("/approvals/{approvalId}")
	public ResponseEntity<Approval_request> getApproval(@PathVariable("approvalId") int approvalId){
		Approval_request result = service.approvalView(approvalId);
		if(result == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(result);
	}

	// 결재 첨부 다운로드
	@GetMapping("/download/approval/{approvalId}")
	public ResponseEntity<Resource> downloadApproval(@PathVariable("approvalId") int approvalId) {
		try {
			Approval_request approval = service.approvalView(approvalId);
			if (approval == null || approval.getFileName() == null || approval.getFileName().isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			String fileName = approval.getFileName();
			Path filePath = Paths.get(uploadpath2, fileName);
			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.notFound().build();
			}

			String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
	
	// 결재 승인
	@PutMapping("/approvals/{approvalId}/approve")
	public Map<String, Object> approveApproval(@PathVariable("approvalId") int approvalId, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		
		try {
			// 1. 세션에서 사용자 정보 가져오기
			Employee approver = getSessionEmployee(session);
			if (approver == null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
			}
			
			// 2. 권한 확인 (부장만 결재 가능)
			if (!canApprove(approver)) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "결재 권한이 없습니다.");
			}
			
			// 3. 승인 처리
			service.approveApproval(approvalId, approver);
			
			map.put("rt", "OK");
			map.put("message", "결재가 승인되었습니다.");
			return map;
			
		} catch (ResponseStatusException e) {
			map.put("rt", "FAIL");
			map.put("message", e.getReason());
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", "결재 승인 중 오류가 발생했습니다.");
			return map;
		}
	}
	
	// 결재 반려
	@PutMapping("/approvals/{approvalId}/reject")
	public Map<String, Object> rejectApproval(@PathVariable("approvalId") int approvalId, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		
		try {
			// 1. 세션에서 사용자 정보 가져오기
			Employee approver = getSessionEmployee(session);
			if (approver == null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
			}
			
			// 2. 권한 확인 (부장만 결재 가능)
			if (!canApprove(approver)) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "결재 권한이 없습니다.");
			}
			
			// 3. 반려 처리
			service.rejectApproval(approvalId, approver);
			
			map.put("rt", "OK");
			map.put("message", "결재가 반려되었습니다.");
			return map;
			
		} catch (ResponseStatusException e) {
			map.put("rt", "FAIL");
			map.put("message", e.getReason());
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", "결재 반려 중 오류가 발생했습니다.");
			return map;
		}
	}
	
	// 세션에서 직원 정보 가져오기
	private Employee getSessionEmployee(HttpSession session) {
		if (session == null) return null;
		Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (empNo == null) empNo = (Integer) session.getAttribute("emp_no");
		if (empNo == null) return null;
		return employeeService.employeeView(empNo);
	}
	
	// 결재 이력 조회
	@GetMapping("/approval-history")
	public ResponseEntity<List<Task_history>> getApprovalHistory(
			@RequestParam(value = "approvalId", required = false) Integer approvalId) {
		List<Task_history> results;
		if (approvalId != null) {
			// 특정 결재의 이력 조회
			results = taskHistoryRepository.findByApprovalApprovalSeqOrderByHistorySeqDesc(approvalId);
		} else {
			// 전체 이력 조회
			results = taskHistoryRepository.findAll();
		}
		return ResponseEntity.ok(results);
	}
	
	// 결재자 기준 이력 조회 (Inbox)
	@GetMapping("/approval-history/inbox")
	public ResponseEntity<List<Task_history>> getInboxHistory(
			@RequestParam(value = "approverId", required = false) String approverId_str,
			@RequestParam(value = "statusId", required = false) Integer statusId,
			HttpSession session) {
		Employee user = getSessionEmployee(session);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		}

		// String → Integer 변환 (이메일 지원)
		Integer approverId = parseEmployeeId(approverId_str);

		// approverId가 없으면 세션 사용자 사용
		Integer targetApproverId = approverId != null ? approverId : user.getEmpNo();

		List<Task_history> results;
		if (statusId != null) {
			results = taskHistoryRepository.findByApproverEmpNoAndStatusStatusseqOrderByHistorySeqDesc(targetApproverId, statusId);
		} else {
			results = taskHistoryRepository.findByApproverEmpNoOrderByHistorySeqDesc(targetApproverId);
		}
		return ResponseEntity.ok(results);
	}
	
	// 요청자 기준 이력 조회 (Outbox)
	@GetMapping("/approval-history/outbox")
	public ResponseEntity<List<Task_history>> getOutboxHistory(
			@RequestParam(value = "requesterId", required = false) String requesterId_str,
			@RequestParam(value = "statusId", required = false) Integer statusId,
			HttpSession session) {
		Employee user = getSessionEmployee(session);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		}

		// String → Integer 변환 (이메일 지원)
		Integer requesterId = parseEmployeeId(requesterId_str);

		// requesterId가 없으면 세션 사용자 사용
		Integer targetRequesterId = requesterId != null ? requesterId : user.getEmpNo();
		
		// Approval_request에서 requesterId로 필터링된 결재를 찾고, 그 결재의 이력 조회
		List<Approval_request> approvals = service.listByRequester(targetRequesterId);
		List<Task_history> results = new java.util.ArrayList<>();
		
		for (Approval_request approval : approvals) {
			List<Task_history> historyList;
			if (statusId != null) {
				historyList = taskHistoryRepository.findByApprovalApprovalSeqOrderByHistorySeqDesc(approval.getApprovalSeq())
					.stream()
					.filter(h -> h.getStatus() != null && h.getStatus().getStatusseq() == statusId)
					.collect(java.util.stream.Collectors.toList());
			} else {
				historyList = taskHistoryRepository.findByApprovalApprovalSeqOrderByHistorySeqDesc(approval.getApprovalSeq());
			}
			results.addAll(historyList);
		}
		
		// historySeq 기준 내림차순 정렬
		results.sort((a, b) -> b.getHistorySeq().compareTo(a.getHistorySeq()));
		
		return ResponseEntity.ok(results);
	}
	
	// 반려 후 재상신
	@PostMapping("/approval-history/resubmit")
	public Map<String, Object> resubmitApproval(
			@RequestBody Map<String, Object> body,
			HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Employee user = getSessionEmployee(session);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		}
		
		try {
			Integer approvalId = ((Number) body.get("approvalId")).intValue();
			Integer requesterId = ((Number) body.get("requesterId")).intValue();
			
			// 권한 확인: 요청자 본인만 재상신 가능
			if (!requesterId.equals(user.getEmpNo())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
			}
			
			Approval_request approval = service.approvalView(approvalId);
			if (approval == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "approvalNotFound");
			}
			
			// 상태를 "결재 진행중"(2)으로 변경
			Workflow inProgressStatus = workflowRepository.findById(2)
					.orElseThrow(() -> new IllegalStateException("진행중 상태를 찾을 수 없습니다."));
			
			if (approval.getTask() != null) {
				approval.getTask().setStatus(inProgressStatus);
				taskRepository.save(approval.getTask());
			}

			// 히스토리 기록 추가 (재상신)
			Task_history history = new Task_history(approval, new Date(), inProgressStatus, user);
			taskHistoryRepository.save(history);
			
			map.put("rt", "OK");
			map.put("message", "재상신되었습니다.");
			return map;
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", "재상신 중 오류가 발생했습니다.");
			return map;
		}
	}
	
	// 결재 권한 확인 (부장만 가능)
	private boolean canApprove(Employee emp) {
		return emp.getRole() != null && "GM".equalsIgnoreCase(emp.getRole().getRoleName());
	}

	// Employee ID 파싱 (이메일 또는 숫자)
	private Integer parseEmployeeId(String idStr) {
		if (idStr == null || idStr.isEmpty()) {
			return null;
		}

		// 이메일인 경우
		if (idStr.contains("@")) {
			Employee emp = employeeRepository.findByEmail(idStr);
			if (emp == null) {
				return null;
			}
			return emp.getEmpNo();
		}

		// 숫자 문자열인 경우
		try {
			return Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
