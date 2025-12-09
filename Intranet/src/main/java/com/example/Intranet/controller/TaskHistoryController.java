package com.example.Intranet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.Intranet.entity.Task_history;
import com.example.Intranet.service.TaskHistoryService;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class TaskHistoryController {

	@Autowired
	private TaskHistoryService service;
	
//	// 결재 처리(승인/반려) - approvalId, approverId, statusId 필수
//	@PostMapping("/approval-history")
//	public ResponseEntity<Task_history> createHistory(@RequestBody Map<String, Object> body){
//		int approvalId = ((Number) body.get("approvalId")).intValue();
//		int approverId = ((Number) body.get("approverId")).intValue();
//		int statusId = ((Number) body.get("statusId")).intValue(); // 3=승인, 4=반려
//		
//		TaskHistory saved = service.historyWriteSimple(approvalId, approverId, statusId);
//		return ResponseEntity.ok(saved);
//	}
	
	// 결재 이력 목록 (전체 또는 특정 approvalId)
//	@GetMapping("/approval-history")
//	public ResponseEntity<List<?>> listHistory(@RequestParam(value = "approvalId", required = false) Integer approvalId){
//		List<?> results;
//		if(approvalId != null) {
//			results = service.taskHistoryAllForApproval(approvalId);
//		}else {
//			results = service.taskHistoryList();
//		}
//		return ResponseEntity.ok(results);
//	}
//	
//	// 결재자 기준 이력(대기/승인/반려 필터)
//	@GetMapping("/approval-history/inbox")
//	public ResponseEntity<List<TaskHistoryViewDTO>> inboxHistory(
//			@RequestParam("approverId") int approverId,
//			@RequestParam(value = "statusId", required = false) Integer statusId){
//		List<TaskHistoryViewDTO> results = service.taskHistoryByApprover(approverId, statusId);
//		return ResponseEntity.ok(results);
//	}
//	
//	// 요청자 기준 결재 이력(내 결재 요청함)
//	@GetMapping("/approval-history/outbox")
//	public ResponseEntity<List<TaskHistoryViewDTO>> outboxHistory(
//			@RequestParam("requesterId") int requesterId,
//			@RequestParam(value = "statusId", required = false) Integer statusId){
//		List<TaskHistoryViewDTO> results = service.taskHistoryByRequester(requesterId, statusId);
//		return ResponseEntity.ok(results);
//	}
//	
//	// 반려 후 재상신: 상태를 2(결재 진행중)로 리셋
//	@PostMapping("/approval-history/resubmit")
//	public ResponseEntity<TaskHistory> resubmit(@RequestBody Map<String, Object> body){
//		int approvalId = ((Number) body.get("approvalId")).intValue();
//		int requesterId = ((Number) body.get("requesterId")).intValue();
//		TaskHistory saved = service.resubmit(approvalId, requesterId);
//		return ResponseEntity.ok(saved);
//	}
}
