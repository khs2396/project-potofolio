package com.example.Intranet.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Intranet.dto.CommentsDTO;
import com.example.Intranet.entity.Comments;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Iboard;
import com.example.Intranet.controller.SessionConst;
import com.example.Intranet.service.CommentsService;
import com.example.Intranet.service.EmployeeService;
import com.example.Intranet.service.IboardService;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" }, allowCredentials = "true")
public class CommentsController {

	private static final int PAGE_SIZE = 5;

	@Autowired
	private CommentsService commentsService;

	@Autowired
	private IboardService iboardService;

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/boards/{boardseq}/comments")
	public Map<String, Object> list(@PathVariable("boardseq") int boardseq,
			@RequestParam(name = "pg", defaultValue = "1") int pg) {
		int currentPage = Math.max(pg, 1);
		Iboard board = iboardService.iboardView(boardseq);

		Map<String, Object> map = new HashMap<>();
		if (board == null) {
			map.put("rt", "FAIL");
			map.put("msg", "게시글을 찾을 수 없습니다.");
			return map;
		}

		Page<Comments> pageData = commentsService.findCommentsByBoard(board,
				PageRequest.of(currentPage - 1, PAGE_SIZE));

		int totalPages = pageData.getTotalPages();
		int block = 3;
		int startPage = ((currentPage - 1) / block) * block + 1;
		int endPage = Math.min(startPage + block - 1, totalPages);

		map.put("rt", "OK");
		map.put("pg", currentPage);
		map.put("total", pageData.getTotalElements());
		map.put("totalP", totalPages);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("items", pageData.getContent().stream().map(this::toCommentResponse).collect(Collectors.toList()));
		return map;
	}

	@PostMapping("/boards/{boardseq}/comments")
	public Map<String, Object> write(@PathVariable("boardseq") int boardseq, @RequestBody CommentsDTO dto,
			HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (empNo == null) {
			empNo = (Integer) session.getAttribute("emp_no"); // 이전 세션 키 fallback
		}
		if (empNo == null && dto.getEmpNo() != 0) {
			empNo = dto.getEmpNo(); // 프런트에서 넘어오는 empNo 사용 (세션 없을 때)
		}
		if (empNo == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		}

		Iboard board = iboardService.iboardView(boardseq);
		if (board == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "boardNotFound");
		}
		if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contentRequired");
		}

		dto.setBoardSeq(boardseq);
		dto.setEmpNo(empNo);
		dto.setLogtime(new Date());

		Comments saved = commentsService.commentWrite(dto);
		map.put("rt", saved != null ? "OK" : "FAIL");
		if (saved != null) {
			map.put("item", toCommentResponse(saved));
		}
		return map;
	}

	@PutMapping("/boards/{boardseq}/comments/{commentseq}")
	public Map<String, Object> modify(@PathVariable("boardseq") int boardseq,
			@PathVariable("commentseq") int commentseq, @RequestBody CommentsDTO dto, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (empNo == null) {
			empNo = (Integer) session.getAttribute("emp_no"); // 이전 세션 키 fallback
		}
		if (empNo == null && dto.getEmpNo() != 0) {
			empNo = dto.getEmpNo(); // 프런트에서 전달한 사번 사용 (세션 없을 때)
		}
		if (empNo == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		}

		Comments existing = commentsService.findComment(commentseq);
		if (existing == null || existing.getBoard() == null || existing.getBoard().getBoardseq() != boardseq) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "commentNotFound");
		}
		if (!canManage(existing, empNo)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");

		dto.setCommentSeq(commentseq);
		dto.setBoardSeq(boardseq);
		dto.setEmpNo(empNo);

		int result = commentsService.commentModify(dto);
		map.put("rt", result > 0 ? "modifyOK" : "modifyFAIL");
		if (result > 0) {
			map.put("item", toCommentResponse(commentsService.findComment(commentseq)));
		}
		return map;
	}

	@DeleteMapping("/boards/{boardseq}/comments/{commentseq}")
	public Map<String, Object> delete(@PathVariable("boardseq") int boardseq,
	                                  @PathVariable("commentseq") int commentseq,
	                                  HttpSession session) {
	    Map<String, Object> map = new HashMap<>();
	    Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
	    if (empNo == null) {
	        empNo = (Integer) session.getAttribute("emp_no"); // 이전 세션 키 fallback
	    }
	    if (empNo == null) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
	    }

	    Comments existing = commentsService.findComment(commentseq);
	    if (existing == null || existing.getBoard() == null || existing.getBoard().getBoardseq() != boardseq) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "commentNotFound");
	    }
	    if (!canManage(existing, empNo)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");

	    int result = commentsService.commentDelete(commentseq);
	    map.put("rt", result > 0 ? "deleteOK" : "deleteFAIL");
	    return map;
	}


	private Map<String, Object> toCommentResponse(Comments comment) {
		Map<String, Object> item = new HashMap<>();
		item.put("commentseq", comment.getCommentSeq());
		item.put("boardseq", comment.getBoard() != null ? comment.getBoard().getBoardseq() : null);
		Integer empNo = comment.getEmployee() != null ? comment.getEmployee().getEmpNo() : null;
		item.put("empNo", empNo);
		// 프런트 depno 필드에 사번을 내려 기존 권한 체크 로직과 호환
		item.put("depno", empNo);
		item.put("content", comment.getContent());
		item.put("logtime", comment.getLogtime());
		if (comment.getEmployee() != null) {
			Employee author = comment.getEmployee();
			item.put("authorName", author.getName());
			item.put("email", author.getEmail());
		} else {
			item.put("authorName", "익명");
		}
		return item;
	}

	private boolean canManage(Comments comment, Integer empNo) {
		if (empNo == null) return false;
		// 관리자(부장/GM roleId 5) 허용
		Employee actor = employeeService.employeeView(empNo);
		boolean isAdmin = actor != null && actor.getRole() != null && actor.getRole().getRoleId() == 5;
		if (isAdmin) return true;
		// 본인 댓글 여부
		return comment.getEmployee() != null && comment.getEmployee().getEmpNo() == empNo;
	}
}
