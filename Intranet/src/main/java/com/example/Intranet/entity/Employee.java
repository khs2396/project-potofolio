package com.example.Intranet.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMPLOYEE")
@Data
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYEE_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "EMPLOYEE_SEQUENCE_GENERATOR", sequenceName = "emp_no_seq", initialValue = 20250001, allocationSize = 1)
	@Column(name = "emp_no")
	private Integer empNo;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "pw", nullable = false, length = 100)
	private String pw;

	@Column(name = "tel", length = 20)
	private String tel;

	@ManyToOne
	@JoinColumn(name = "dep_id")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleCode role;

	@Column(name = "join_time")
	private Date joinTime;

	public Employee(Integer empNo, String name, String email, String pw, String tel, Department department, RoleCode role, Date joinTime) {
		this.empNo = empNo;
		this.name = name;
		this.email = email;
		this.pw = pw;
		this.tel = tel;
		this.department = department;
		this.role = role;
		this.joinTime = joinTime;
	}
}
