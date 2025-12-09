package com.example.Intranet.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Intranet.dto.IboardDTO;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Iboard;
import com.example.Intranet.service.EmployeeService;
import com.example.Intranet.service.IboardService;
import com.example.Intranet.controller.SessionConst;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/boards")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class IboardController {

	@Autowired
	private IboardService iboardService;

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	public Map<String, Object> list(@RequestParam(name = "pg", defaultValue = "1") int pg,
									@RequestParam(name = "size", defaultValue = "10") int size) {
		int pageSize = Math.max(size, 1);
		int currentPage = Math.max(pg, 1);
		int endNum = currentPage * pageSize;
		int startNum = endNum - (pageSize - 1);

		List<Iboard> boards = iboardService.iboardList(startNum, endNum);
		int totalCount = iboardService.getIboardCount();
		int totalPages = (totalCount + pageSize - 1) / pageSize;
		int block = 3;
		int startPage = ((currentPage - 1) / block) * block + 1;
		int endPage = Math.min(startPage + block - 1, totalPages);

		Map<String, Object> map = new HashMap<>();
		map.put("rt", "OK");
		map.put("pg", currentPage);
		map.put("total", totalCount);
		map.put("totalP", totalPages);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("pageSize", pageSize);
		map.put("items", boards.stream().map(this::toBoardResponse).collect(Collectors.toList()));
		return map;
	}

	@GetMapping("/{boardseq}")
	public Map<String, Object> detail(@PathVariable("boardseq") int boardseq) {
		iboardService.updateHit(boardseq);
		Iboard board = iboardService.iboardView(boardseq);

		Map<String, Object> map = new HashMap<>();
		if (board == null) {
			map.put("rt", "FAIL");
			map.put("msg", "게시글을 찾을 수 없습니다.");
			return map;
		}

		map.put("rt", "viewOK");
		map.put("item", toBoardResponse(board));
		return map;
	}

	@PostMapping
	public Map<String, Object> write(@RequestBody IboardDTO dto, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Integer depno = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (depno == null) {
			map.put("rt", "loginRequired");
			return map;
		}

		// 새 게시글 작성 시 필수 필드 설정
		dto.setDepno(depno);
		dto.setLogtime(new Date());
		dto.setBoardseq(0); // 새 게시글은 0으로 설정 (시퀀스 자동 생성)
		if (dto.getHit() == 0) {
			dto.setHit(0); // 조회수 초기값
		}

		Iboard saved = iboardService.iboardWrite(dto);
		map.put("rt", saved != null ? "writeOK" : "writeFAIL");
		if (saved != null) {
			map.put("item", toBoardResponse(saved));
		} else {
			map.put("msg", "게시글 저장에 실패했습니다.");
		}
		return map;
	}

	@PutMapping("/{boardseq}")
	public Map<String, Object> modify(@PathVariable("boardseq") int boardseq, @RequestBody IboardDTO dto, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Integer depno = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (depno == null) {
			map.put("rt", "loginRequired");
			return map;
		}

		Iboard board = iboardService.iboardView(boardseq);
		if (board == null) {
			map.put("rt", "FAIL");
			map.put("msg", "게시글을 찾을 수 없습니다.");
			return map;
		}
		if (board.getDepno() != depno) {
			map.put("rt", "noPermission");
			return map;
		}

		dto.setBoardseq(boardseq);
		int result = iboardService.iboardModify(dto);
		map.put("rt", result > 0 ? "modifyOK" : "modifyFAIL");
		if (result > 0) {
			map.put("item", toBoardResponse(iboardService.iboardView(boardseq)));
		}
		return map;
	}

	@DeleteMapping("/{boardseq}")
	public Map<String, Object> delete(@PathVariable("boardseq") int boardseq, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Integer depno = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (depno == null) {
			map.put("rt", "loginRequired");
			return map;
		}

		Iboard board = iboardService.iboardView(boardseq);
		if (board == null) {
			map.put("rt", "FAIL");
			map.put("msg", "게시글을 찾을 수 없습니다.");
			return map;
		}
		if (board.getDepno() != depno) {
			map.put("rt", "noPermission");
			return map;
		}

		int result = iboardService.iboardDelete(boardseq);
		map.put("rt", result > 0 ? "deleteOK" : "deleteFAIL");
		return map;
	}

	private Map<String, Object> toBoardResponse(Iboard board) {
		Map<String, Object> item = new HashMap<>();
		item.put("boardseq", board.getBoardseq());
		item.put("depno", board.getDepno());
		item.put("subject", board.getSubject());
		item.put("content", board.getContent());
		item.put("hit", board.getHit());
		item.put("logtime", board.getLogtime());
		Employee writer = employeeService.employeeView(board.getDepno());
		item.put("authorName", writer != null ? writer.getName() : "익명");
		if (writer != null) {
			item.put("depName", writer.getDepartment() != null ? writer.getDepartment().getDepName() : null);
			item.put("depId", writer.getDepartment() != null ? writer.getDepartment().getDepId() : null);
			item.put("roleName", writer.getRole() != null ? writer.getRole().getRoleName() : null);
			item.put("roleId", writer.getRole() != null ? writer.getRole().getRoleId() : null);
		}
		return item;
	}
}
