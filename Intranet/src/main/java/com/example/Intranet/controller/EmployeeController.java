package com.example.Intranet.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Intranet.dto.EmployeeDTO;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" }, allowCredentials = "true")
public class EmployeeController {

	@Autowired
	EmployeeService service;

	@PostMapping("/employeeSave")
	public Map<String, Object> employeeSave(@RequestBody EmployeeDTO dto) {
		if (dto.getJoinTime() == null)
			dto.setJoinTime(new Date());
		if (dto.getRoleId() == null)
			dto.setRoleId(1); // default role: staff

		Employee employee = service.employeeSave(dto);
		Map<String, Object> map = new HashMap<>();
		map.put("rt", employee != null ? "OK" : "FAIL");
		if (employee != null) {
			map.put("item", toPayload(employee));
		}
		return map;
	}

	@GetMapping("/employeeList")
	public Map<String, Object> employeeList(HttpServletRequest request, HttpSession session) {
		int pg = request.getParameter("pg") != null ? Integer.parseInt(request.getParameter("pg")) : 1;
		int size = request.getParameter("size") != null ? Integer.parseInt(request.getParameter("size")) : 20;
		int endNum = pg * size;
		int startNum = endNum - size + 1;

		List<Employee> base = service.getAllEmployees();
		int totalA = base.size();

		int totalP = (totalA + size - 1) / size;
		int startPage = (pg - 1) / 3 * 3 + 1;
		int endPage = Math.min(startPage + 2, totalP);
		int fromIdx = Math.max(0, startNum - 1);
		int toIdx = Math.min(base.size(), endNum);
		List<Employee> pageItems = fromIdx >= base.size() ? List.of() : base.subList(fromIdx, toIdx);

		Map<String, Object> map = new HashMap<>();
		map.put("rt", "OK");
		map.put("total", totalA);
		map.put("pg", pg);
		map.put("totalP", totalP);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("pageSize", size);
		map.put("items", pageItems.stream().map(this::toPayload).toList());
		return map;
	}

	@GetMapping("/getEmployeesByDepartmentList")
	public Map<String, Object> getEmployeesByDepartmentList(@RequestParam("dep_id") int depId,
			@RequestParam(value = "pg", defaultValue = "1") int pg) {
		int size = 10;
		Pageable pageable = PageRequest.of(pg - 1, size);
		Page<Employee> employeePage = service.getEmployeesByDepartment(depId, pageable);

		int block = 3;
		int totalP = employeePage.getTotalPages();
		int startPage = ((pg - 1) / block) * block + 1;
		int endPage = Math.min(startPage + 2, totalP);

		Map<String, Object> map = new HashMap<>();
		map.put("rt", "OK");
		map.put("totalElements", employeePage.getTotalElements());
		map.put("totalP", totalP);
		map.put("pg", pg);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("pageSize", size);
		map.put("items", employeePage.getContent().stream().map(this::toPayload).toList());
		return map;
	}

	@GetMapping("/employeeView")
	public Map<String, Object> employeeView(@RequestParam("empNo") int empNo) {
		Employee employee = service.employeeView(empNo);

		Map<String, Object> map = new HashMap<>();
		if (employee != null) {
			map.put("rt", "OK");
			map.put("total", 1);
			map.put("item", toPayload(employee));
		} else {
			map.put("rt", "FAIL");
		}
		return map;
	}

	@PostMapping("employeeModify")
	public Map<String, Object> employeeModify(@RequestBody EmployeeDTO dto, HttpSession session) {
		Employee viewer = getSessionEmployee(session);
		if (viewer == null)
			throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "loginRequired");
		// empNo가 비어 있으면 자기 자신으로 간주
		if (dto.getEmpNo() == null)
			dto.setEmpNo(viewer.getEmpNo());
		boolean sameUser = viewer.getEmpNo() != null && dto.getEmpNo() != null
				&& viewer.getEmpNo().intValue() == dto.getEmpNo().intValue();
		// HR가 아니면 자기 자신만 수정 가능, 그리고 이름/전화번호만 허용
		if (!isHr(viewer) && !sameUser)
			throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "noPermission");
		if (!isHr(viewer) && sameUser) {
			// 보호: HR이 아닌 경우 부서/직급/이메일/비밀번호 변경 차단
			dto.setDepId(null);
			dto.setRoleId(null);
			dto.setPw(null);
			dto.setEmail(null);
		}
		int result = service.employeeModify(dto);
		Map<String, Object> map = new HashMap<>();
		map.put("rt", result > 0 ? "OK" : "FAIL");
		return map;
	}

	@GetMapping("/employeeDelete")
	public Map<String, Object> employeeDelete(@RequestParam("empNo") int empNo, HttpSession session) {
		Employee viewer = getSessionEmployee(session);
		if (!isHr(viewer))
			throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "noPermission");
		int result = service.employeeDelete(empNo);
		Map<String, Object> map = new HashMap<>();
		map.put("rt", result > 0 ? "deleteOK" : "deleteFAIL");
		return map;
	}

	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody EmployeeDTO dto, HttpSession session) {
		Employee employee = service.login(dto.getEmail(), dto.getPw());
		Map<String, Object> map = new HashMap<>();
		if (employee != null) {
			map.put("rt", "loginOK");
			map.put("employee", toPayload(employee));
			map.put("item", toPayload(employee));
			session.setAttribute("emp_no", employee.getEmpNo());
			session.setAttribute("name", employee.getName());
			session.setAttribute(SessionConst.LOGIN_EMP_DEP_NO, employee.getEmpNo());
			session.setAttribute(SessionConst.LOGIN_EMP_EMAIL, employee.getEmail());
			session.setAttribute(SessionConst.LOGIN_EMP_NAME, employee.getName());
			session.setAttribute(SessionConst.LOGIN_EMP_DEPT,
					employee.getDepartment() != null ? employee.getDepartment().getDepName() : null);
			session.setAttribute(SessionConst.LOGIN_EMP_ROLE,
					employee.getRole() != null ? employee.getRole().getRoleName() : null);
		} else {
			map.put("rt", "loginFAIL");
		}
		return map;
	}

	@PostMapping("/logout")
	public Map<String, Object> logout(HttpSession session) {
		session.invalidate();
		Map<String, Object> map = new HashMap<>();
		map.put("rt", "logoutOK");
		return map;
	}

	@GetMapping("/findID")
	public Map<String, Object> findID(@RequestParam("empNo") int empNo) {
		Employee employee = service.employeeView(empNo);
		Map<String, Object> map = new HashMap<>();
		if (employee != null) {
			map.put("rt", "findOK");
			map.put("ID", employee.getEmail());
		} else {
			map.put("rt", "findFAIL");
		}
		return map;
	}

	@GetMapping("/checkPW")
	public Map<String, Object> checkPW(@RequestParam("empNo") int empNo, @RequestParam("email") String email) {
		int result = service.checkPW(empNo, email);
		Map<String, Object> map = new HashMap<>();
		map.put("rt", result > 0 ? "checkOK" : "checkFAIL");
		return map;
	}

	@PostMapping("/changePW")
	public Map<String, Object> changePW(@RequestBody EmployeeDTO dto) {
		int result = service.changePW(dto.getEmpNo(), dto.getPw());
		Map<String, Object> map = new HashMap<>();
		map.put("rt", result > 0 ? "changeOK" : "changeFAIL");
		return map;
	}

	private Map<String, Object> toPayload(Employee employee) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("empNo", employee.getEmpNo());
		payload.put("name", employee.getName());
		payload.put("email", employee.getEmail());
		payload.put("tel", employee.getTel());
		payload.put("depId", employee.getDepartment() != null ? employee.getDepartment().getDepId() : null);
		payload.put("depName",
				employee.getDepartment() != null ? mapDeptName(employee.getDepartment().getDepName()) : null);
		payload.put("roleId", employee.getRole() != null ? employee.getRole().getRoleId() : null);
		payload.put("roleName", employee.getRole() != null ? employee.getRole().getRoleName() : null);
		payload.put("joinTime", employee.getJoinTime());
		payload.put("dept",
				employee.getDepartment() != null ? mapDeptName(employee.getDepartment().getDepName()) : null);
		payload.put("title", employee.getRole() != null ? employee.getRole().getRoleName() : null);
		return payload;
	}

	private String mapDeptName(String depName) {
		if (depName == null)
			return null;
		String name = depName;
		if ("HR".equalsIgnoreCase(depName))
			name = "인사";
		else if ("GA".equalsIgnoreCase(depName))
			name = "총무";
		else if ("Dev".equalsIgnoreCase(depName))
			name = "개발";
		else if ("FID".equalsIgnoreCase(depName))
			name = "재무";
		else if ("SD".equalsIgnoreCase(depName))
			name = "영업";
		if (!name.endsWith("부"))
			name = name + "부";
		return name;
	}

	private Employee getSessionEmployee(HttpSession session) {
		if (session == null)
			return null;
		Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
		if (empNo == null)
			empNo = (Integer) session.getAttribute("emp_no");
		if (empNo == null)
			return null;
		return service.employeeView(empNo);
	}

	private boolean isHr(Employee viewer) {
		if (viewer == null || viewer.getDepartment() == null)
			return false;
		Integer depId = viewer.getDepartment().getDepId();
		return depId != null && depId == 1;
	}

	// 임시 엔드포인트: 기존 평문 비밀번호를 BCrypt로 마이그레이션
	@PostMapping("/admin/migrate-passwords")
	public Map<String, Object> migratePasswords() {
		Map<String, Object> map = new HashMap<>();
		try {
			List<Employee> allEmployees = service.getAllEmployees();
			int migrated = 0;
			int skipped = 0;

			for (Employee emp : allEmployees) {
				String currentPw = emp.getPw();

				// 이미 BCrypt 해시인지 확인 (BCrypt 해시는 $2a$, $2b$, $2y$로 시작)
				if (currentPw != null && !currentPw.startsWith("$2")) {
					// 평문 비밀번호를 BCrypt로 암호화
					EmployeeDTO dto = new EmployeeDTO();
					dto.setEmpNo(emp.getEmpNo());
					dto.setPw(currentPw); // 평문 비밀번호

					// employeeModify는 자동으로 BCrypt 암호화 처리
					service.employeeModify(dto);
					migrated++;
				} else {
					skipped++;
				}
			}

			map.put("rt", "OK");
			map.put("message", "비밀번호 마이그레이션 완료");
			map.put("migrated", migrated);
			map.put("skipped", skipped);
			map.put("total", allEmployees.size());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("rt", "FAIL");
			map.put("message", "마이그레이션 실패: " + e.getMessage());
		}
		return map;
	}
}
