package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.example.Intranet.dto.TaskHistoryViewDTO;
import com.example.Intranet.entity.Task_history;

public interface TaskHistoryRepository extends JpaRepository<Task_history, Integer>{
	
	// Task 상태 업데이트 (Native Query)
	@Transactional
	@Modifying
	@Query(value = "UPDATE task SET status_seq = :statusSeq WHERE task_seq = (SELECT task_seq FROM approval_request WHERE approval_seq = :approvalSeq)", nativeQuery = true)
	int updateTaskStatusByApprovalId(@Param("approvalSeq") Integer approvalSeq, @Param("statusSeq") Integer statusSeq);
	
	// approvalId값인 Task_history 테이블 중에서 historySeq가 가장 큰값(최신) 1줄 가져오기
	Task_history findTopByApprovalApprovalSeqOrderByHistorySeqDesc(Integer approvalSeq);
	
	// approvalId값에 해당하는 결제내역 1줄 읽어오기 (DTO 사용)
	@Query("SELECT new com.example.Intranet.dto.TaskHistoryViewDTO(h.historySeq, h.approver.empNo, h.approval.approvalSeq, h.status.statusname, h.approvalCompletedAt) " +
		       "FROM Task_history h " +
		       "JOIN h.status s " +
		       "WHERE h.approval.approvalSeq = :approvalId " +
		       "ORDER BY h.historySeq DESC")
	TaskHistoryViewDTO findHistoryWithStatusName(@Param("approvalId") Integer approvalId);

	// 결제내역 테이블 전체 목록 (DTO 사용)
	@Query("SELECT new com.example.Intranet.dto.TaskHistoryViewDTO(h.historySeq, h.approver.empNo, h.approval.approvalSeq, h.status.statusname, h.approvalCompletedAt) " +
		    "FROM Task_history h " +
		    "JOIN h.status s " +
		    "ORDER BY h.historySeq DESC")
	List<TaskHistoryViewDTO> findAllHistoryWithStatusName();
	
	// 결재자 기준 목록(상태 필터 가능) - DTO 사용
	@Query("SELECT new com.example.Intranet.dto.TaskHistoryViewDTO(h.historySeq, h.approver.empNo, h.approval.approvalSeq, h.status.statusname, h.approvalCompletedAt) " +
			"FROM Task_history h " +
			"JOIN h.status s " +
			"WHERE h.approver.empNo = :approverId AND (:statusSeq IS NULL OR s.statusseq = :statusSeq) " +
			"ORDER BY h.historySeq DESC")
	List<TaskHistoryViewDTO> findHistoryByApprover(@Param("approverId") Integer approverId, @Param("statusSeq") Integer statusSeq);
	
	// 요청자 기준 목록(Approval JOIN) - DTO 사용
	@Query("SELECT new com.example.Intranet.dto.TaskHistoryViewDTO(h.historySeq, h.approver.empNo, h.approval.approvalSeq, h.status.statusname, h.approvalCompletedAt) " +
			"FROM Task_history h " +
			"JOIN h.status s " +
			"JOIN h.approval a " +
			"WHERE a.requesterId.empNo = :requesterId AND (:statusSeq IS NULL OR s.statusseq = :statusSeq) " +
			"ORDER BY h.historySeq DESC")
	List<TaskHistoryViewDTO> findHistoryByRequester(@Param("requesterId") Integer requesterId, @Param("statusSeq") Integer statusSeq);
	
	// 결재 요청별 전체 이력 (최신순) - 엔티티 반환
	List<Task_history> findByApprovalApprovalSeqOrderByHistorySeqDesc(Integer approvalSeq);
	
	// 결재자 기준 목록 조회 (Employee 엔티티의 empNo로) - 엔티티 반환
	List<Task_history> findByApproverEmpNoOrderByHistorySeqDesc(Integer empNo);
	
	// 결재자 기준 목록 조회 (상태 필터 포함) - 엔티티 반환
	List<Task_history> findByApproverEmpNoAndStatusStatusseqOrderByHistorySeqDesc(Integer empNo, Integer statusSeq);
	
	// 전체 이력 조회 (최신순) - 엔티티 반환
	List<Task_history> findAllByOrderByHistorySeqDesc();
}
