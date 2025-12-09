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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.Intranet.dto.NoticeDTO;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Notice;
import com.example.Intranet.service.EmployeeService;
import com.example.Intranet.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/notices")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	public Map<String, Object> list(@RequestParam(name = "pg", defaultValue = "1") int pg,
									@RequestParam(name = "size", defaultValue = "5") int size) {
		int currentPage = Math.max(pg, 1);
		int pageSize = Math.max(size, 1);
		int endNum = currentPage * pageSize;
		int startNum = endNum - (pageSize - 1);

		List<Notice> notices = noticeService.noticeList(startNum, endNum);
		int totalCount = noticeService.getNoticeCount();
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
		map.put("items", notices.stream().map(this::toNoticeResponse).collect(Collectors.toList()));
		return map;
	}

	@GetMapping("/{seq}")
	public Map<String, Object> detail(@PathVariable("seq") int seq) {
		Notice notice = noticeService.noticeView(seq);

		Map<String, Object> map = new HashMap<>();
		if (notice == null) {
			map.put("rt", "FAIL");
			map.put("msg", "공지 글을 찾을 수 없습니다.");
			return map;
		}

		map.put("rt", "viewOK");
		map.put("item", toNoticeResponse(notice));
		return map;
	}

	@PostMapping("/{seq}/hit")
	public Map<String, Object> increaseHit(@PathVariable("seq") int seq) {
		noticeService.updateHit(seq);
		Notice notice = noticeService.noticeView(seq);
		if (notice == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("rt", "hitOK");
		map.put("item", toNoticeResponse(notice));
		return map;
	}

	@PostMapping
	public Map<String, Object> write(@RequestBody NoticeDTO dto, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Employee writer = getSessionEmployee(session);
		if (writer == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		if (!canManageNotice(writer)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");

		dto.setDepno(writer.getEmpNo());
		dto.setLogtime(new Date());
		dto.setName(writer.getName());
		dto.setEmail(writer.getEmail());

		Notice saved = noticeService.noticeWrite(dto);
		map.put("rt", saved != null ? "writeOK" : "writeFAIL");
		if (saved != null) {
			map.put("item", toNoticeResponse(saved));
		}
		return map;
	}

	@PutMapping("/{seq}")
	public Map<String, Object> modify(@PathVariable("seq") int seq, @RequestBody NoticeDTO dto, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Employee writer = getSessionEmployee(session);
		if (writer == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		if (!canManageNotice(writer)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");

		Notice notice = noticeService.noticeView(seq);
		if (notice == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
		}
		if (notice.getDepno() != writer.getEmpNo()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
		}

		dto.setSeq(seq);
		int result = noticeService.noticeModify(dto);
		map.put("rt", result > 0 ? "modifyOK" : "modifyFAIL");
		if (result > 0) {
			map.put("item", toNoticeResponse(noticeService.noticeView(seq)));
		}
		return map;
	}

	@DeleteMapping("/{seq}")
	public Map<String, Object> delete(@PathVariable("seq") int seq, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		Employee writer = getSessionEmployee(session);
		if (writer == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
		if (!canManageNotice(writer)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");

		Notice notice = noticeService.noticeView(seq);
		if (notice == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound");
		}
		if (notice.getDepno() != writer.getEmpNo()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
		}

		int result = noticeService.noticeDelete(seq);
		map.put("rt", result > 0 ? "deleteOK" : "deleteFAIL");
		return map;
	}

	private Map<String, Object> toNoticeResponse(Notice notice) {
		Map<String, Object> item = new HashMap<>();
		item.put("seq", notice.getSeq());
		item.put("depno", notice.getDepno());
		item.put("name", notice.getName());
		item.put("email", notice.getEmail());
		item.put("subject", notice.getSubject());
		item.put("content", notice.getContent());
		item.put("hit", notice.getHit());
		item.put("logtime", notice.getLogtime());
		Employee writer = employeeService.employeeView(notice.getDepno());
		item.put("authorName", writer != null ? writer.getName() : (notice.getName() != null ? notice.getName() : "익명"));
		item.put("depName", writer != null && writer.getDepartment() != null ? writer.getDepartment().getDepName() : null);
		item.put("roleName", writer != null && writer.getRole() != null ? writer.getRole().getRoleName() : null);
		return item;
	}

	private Employee getSessionEmployee(HttpSession session) {
		if (session == null) return null;
		Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (empNo == null) empNo = (Integer) session.getAttribute("emp_no");
		if (empNo == null) return null;
		return employeeService.employeeView(empNo);
	}

	private boolean canManageNotice(Employee emp) {
		boolean isHr = emp.getDepartment() != null && emp.getDepartment().getDepId() == 1;
		boolean isManager = emp.getRole() != null && "GM".equalsIgnoreCase(emp.getRole().getRoleName());
		return isHr || isManager;
	}
}
