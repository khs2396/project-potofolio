package com.example.Intranet.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Intranet.dao.Approval_requestDAO;
import com.example.Intranet.dto.Approval_requestDTO;
import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task;
import com.example.Intranet.entity.Task_history;
import com.example.Intranet.entity.Workflow;
import com.example.Intranet.repository.TaskHistoryRepository;
import com.example.Intranet.repository.TaskRepository;
import com.example.Intranet.repository.WorkflowRepository;


@Service
public class Approval_requestService {
	@Autowired
	Approval_requestDAO dao;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TaskHistoryRepository taskHistoryRepository;

	@Autowired
	WorkflowRepository workflowRepository;

	@Value("${project.upload.path2}")
	private String uploadpath2;

	@Autowired
	TaskService taskService;

		@Transactional
	    public Approval_request createApproval(Approval_requestDTO dto, MultipartFile uploadFile) throws Exception {
	        // 1. 파일 저장

			if (uploadFile != null && !uploadFile.isEmpty()) {
	            String fileName = uploadFile.getOriginalFilename();
	            dto.setFile_name(fileName);
	          //파일 저장 폴더 만들기
	            File folder = new File(uploadpath2);
	            if (!folder.exists()) { //폴더가 없으면 폴더 만들기
	                folder.mkdirs();
	            }

	            File file = new File(uploadpath2, fileName);
	            uploadFile.transferTo(file);
	        } else {
	            // 파일이 없을 때 빈 문자열로 설정
	            dto.setFile_name("");
	        }

	        // 2. 로그 시간 기록
	        dto.setApproval_requested_at(new Date());

	        // 3. DB 저장
	        Approval_request approval = dao.ApprovalWrite(dto);

	        return approval;
	    }


		public  Approval_request approvalView(Integer approval_seq) {
			return dao.approvalView(approval_seq);
		}

		// 결재 목록 조회
		public List<Approval_request> listAll() {
			return dao.listAll();
		}

		// 요청자별 결재 목록 조회
		public List<Approval_request> listByRequester(Integer requesterId) {
			return dao.listByRequester(requesterId);
		}

		// 결재 승인
		@Transactional
		public void approveApproval(int approvalId, Employee approver) {
			// 1. 결재 정보 조회
			Approval_request approval = dao.approvalView(approvalId);
			if (approval == null) {
				throw new IllegalArgumentException("결재 정보를 찾을 수 없습니다.");
			}

			// 2. 이미 최종 처리되었는지 확인 (최신 이력 조회)
			Task_history latestHistory = taskHistoryRepository.findTopByApprovalApprovalSeqOrderByHistorySeqDesc(approvalId);
			if (latestHistory != null && latestHistory.getStatus() != null) {
				int latestStatusSeq = latestHistory.getStatus().getStatusseq();
				// 이미 승인(3) 또는 반려(4)된 경우 재처리 불가
				if (latestStatusSeq == 3 || latestStatusSeq == 4) {
					throw new IllegalStateException("이미 처리된 결재입니다.");
				}
			}

			// 3. Workflow 상태 조회 (3 = APPROVED)
			Workflow approvedStatus = workflowRepository.findById(3)
					.orElseThrow(() -> new IllegalStateException("승인 상태를 찾을 수 없습니다."));

			// 4. Task_history에 승인 기록 저장
			Task_history history = new Task_history(approval, new Date(), approvedStatus, approver);
			taskHistoryRepository.save(history);

			// 5. 연결된 Task 상태 업데이트
			if (approval.getTask() != null) {
				Task task = approval.getTask();
				task.setStatus(approvedStatus);
				taskRepository.save(task);
			}
		}

		// 결재 반려
		@Transactional
		public void rejectApproval(int approvalId, Employee approver) {
			// 1. 결재 정보 조회
			Approval_request approval = dao.approvalView(approvalId);
			if (approval == null) {
				throw new IllegalArgumentException("결재 정보를 찾을 수 없습니다.");
			}

			// 2. 이미 최종 처리되었는지 확인 (최신 이력 조회)
			Task_history latestHistory = taskHistoryRepository.findTopByApprovalApprovalSeqOrderByHistorySeqDesc(approvalId);
			if (latestHistory != null && latestHistory.getStatus() != null) {
				int latestStatusSeq = latestHistory.getStatus().getStatusseq();
				// 이미 승인(3) 또는 반려(4)된 경우 재처리 불가
				if (latestStatusSeq == 3 || latestStatusSeq == 4) {
					throw new IllegalStateException("이미 처리된 결재입니다.");
				}
			}

			// 3. Workflow 상태 조회 (4 = REJECTED)
			Workflow rejectedStatus = workflowRepository.findById(4)
					.orElseThrow(() -> new IllegalStateException("반려 상태를 찾을 수 없습니다."));

			// 4. Task_history에 반려 기록 저장
			Task_history history = new Task_history(approval, new Date(), rejectedStatus, approver);
			taskHistoryRepository.save(history);

			// 5. 연결된 Task 상태 업데이트
			if (approval.getTask() != null) {
				Task task = approval.getTask();
				task.setStatus(rejectedStatus);
				taskRepository.save(task);
			}
		}

}
