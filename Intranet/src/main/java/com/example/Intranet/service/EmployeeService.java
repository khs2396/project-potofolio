package com.example.Intranet.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Intranet.dao.EmployeeDAO;
import com.example.Intranet.dto.EmployeeDTO;
import com.example.Intranet.entity.Employee;

@Service
public class EmployeeService {

	private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d\\d\\$[./A-Za-z0-9]{53}$");

	@Autowired
	EmployeeDAO dao;

	@Autowired
	PasswordEncoder passwordEncoder;

	public int getEmployeeCount() {
		return dao.getEmployeeCount();
	}

	public Employee employeeSave(EmployeeDTO dto) {
		// 비밀번호 암호화
		if (dto.getPw() != null && !dto.getPw().isEmpty()) {
			dto.setPw(passwordEncoder.encode(dto.getPw()));
		}
		if (dto.getEmail() != null) {
			dto.setEmail(dto.getEmail().trim().toLowerCase());
		}
		return dao.employeeSave(dto);
	}

	public List<Employee> employeeList(int startNum, int endNum) {
		return dao.employeeList(startNum, endNum);
	}

	public Page<Employee> getEmployeesByDepartment(int depId, Pageable pageable) {
		return dao.findEmployeesByDepartment(depId, pageable);
	}

	public Employee employeeView(int empNo) {
		return dao.employeeView(empNo);
	}

	public int employeeModify(EmployeeDTO dto) {
		// 비밀번호가 포함된 경우 암호화
		if (dto.getPw() != null && !dto.getPw().isEmpty()) {
			dto.setPw(passwordEncoder.encode(dto.getPw()));
		}
		return dao.employeeModify(dto);
	}

	public int employeeDelete(int empNo) {
		return dao.employeeDelete(empNo);
	}

	public int checkPW(int empNo, String email) {
		return dao.checkPW(empNo, email);
	}

	public int changePW(int empNo, String pw) {
		// 비밀번호 암호화
		String encodedPw = passwordEncoder.encode(pw);
		return dao.changePW(empNo, encodedPw);
	}

	public Employee login(String email, String rawPw) {
		if (rawPw == null || rawPw.isEmpty()) return null;

		String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
		Employee employee = dao.findByEmail(normalizedEmail);
		if (employee == null) return null;

		String storedPw = employee.getPw();
		if (storedPw == null || storedPw.isEmpty()) return null;

		// 기존 평문이나 기타 포맷을 가진 레거시 데이터 자동 보정
		if (!isBcrypt(storedPw)) {
			if (storedPw.equals(rawPw)) {
				String encoded = passwordEncoder.encode(rawPw);
				dao.changePW(employee.getEmpNo(), encoded);
				employee.setPw(encoded);
				return employee;
			}
			return null;
		}

		// BCrypt 검증: 평문 입력 vs DB의 암호화된 해시
		return passwordEncoder.matches(rawPw, storedPw) ? employee : null;
	}

	public Integer findDepnoByEmail(String email) {
		Employee emp = dao.findByEmail(email);
		if (emp != null) return emp.getEmpNo();
		else return null;
	}

	public List<Employee> getAllEmployees() {
		return dao.findAllEmployees();
	}

	private boolean isBcrypt(String value) {
		return value != null && BCRYPT_PATTERN.matcher(value).matches();
	}
}
