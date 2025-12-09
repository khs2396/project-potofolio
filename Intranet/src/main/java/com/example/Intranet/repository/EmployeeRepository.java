package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Intranet.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	@Query(value = "select * from (select rownum rn, tt.* from (select * from Employee order by emp_no desc) tt) where rn>=:startNum and rn<=:endNum", nativeQuery = true)
	List<Employee> findByStartnumAndEndnum(@Param("startNum") int startNum, @Param("endNum") int endNum);

	Employee findByEmpNoAndEmail(int empNo, String email);

	Employee findByEmail(String email);

	Page<Employee> findAllByDepartment_DepId(int depId, Pageable pageable);

}
