package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.EmployeeDTO;
import com.example.Intranet.entity.Department;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.RoleCode;
import com.example.Intranet.repository.DepartmentRepository;
import com.example.Intranet.repository.EmployeeRepository;
import com.example.Intranet.repository.RoleCodeRepository;

import jakarta.transaction.Transactional;

@Repository
public class EmployeeDAO {
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	RoleCodeRepository roleCodeRepository;

	public int getEmployeeCount() {
		return (int) employeeRepository.count();
	}

	@Transactional
    public Employee employeeSave(EmployeeDTO dto) {
        Department department = departmentRepository.findById(dto.getDepId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));

        RoleCode role = roleCodeRepository.findById(dto.getRoleId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 직책입니다."));

        Employee employee = dto.toEntity(department, role);
        return employeeRepository.save(employee);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

	public List<Employee> employeeList(int startNum, int endNum) {
		return employeeRepository.findByStartnumAndEndnum(startNum, endNum);
	}

    public Page<Employee> findEmployeesByDepartment(int depId, Pageable pageable) {
        return employeeRepository.findAllByDepartment_DepId(depId, pageable);
    }

	public Employee employeeView(int empNo) {
		return employeeRepository.findById(empNo).orElse(null);
	}

	public int employeeModify(EmployeeDTO dto) {
		Employee employee = employeeRepository.findById(dto.getEmpNo()).orElse(null);

		int result = 0;
		if (employee != null) {
			if (dto.getName() != null && !dto.getName().isEmpty()) {
				employee.setName(dto.getName());
			}
			if (dto.getPw() != null && !dto.getPw().isEmpty()) {
				employee.setPw(dto.getPw());
			}
			if (dto.getTel() != null) {
				employee.setTel(dto.getTel());
			}
			if (dto.getDepId() != null) {
				Department dep = departmentRepository.findById(dto.getDepId()).orElse(null);
				employee.setDepartment(dep);
			}
			if (dto.getRoleId() != null) {
				RoleCode role = roleCodeRepository.findById(dto.getRoleId()).orElse(null);
				employee.setRole(role);
			}

			Employee modify = employeeRepository.save(employee);
			if (modify != null) {
				result = 1;
			}
		}
		return result;
	}

	public int employeeDelete(int empNo) {
		Employee employee = employeeRepository.findById(empNo).orElse(null);
		int result = 0;

		if (employee != null) {
			employeeRepository.delete(employee);
			if (!employeeRepository.existsById(empNo)) {
				result = 1;
			}
		}
		return result;
	}

	public int checkPW(int empNo, String email) {
		int result = 0;
		Employee employee = employeeRepository.findByEmpNoAndEmail(empNo, email);
		if (employee != null) {
			result = 1;
		}
		return result;
	}

	public int changePW(int empNo, String pw) {
		int result = 0;
		Employee employee = employeeRepository.findById(empNo).orElse(null);
		if (employee != null) {
			employee.setPw(pw);
			Employee employee_result = employeeRepository.save(employee);
			if (employee_result != null) {
				result = 1;
			}
		}
		return result;
	}

	public Employee findByEmail(String email) {
		return employeeRepository.findByEmail(email);
	}

	public Employee save(Employee employee) {
		return employeeRepository.save(employee);
	}
}
