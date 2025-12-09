package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Department;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.RoleCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Integer empNo;    // employee number
    private String name;
    private String email;

    @JsonProperty("pw")
    private String pw;        // password (plain on input)

    private String tel;

    @JsonProperty("dep_id")
    private Integer depId;

    @JsonProperty("role_id")
    private Integer roleId;

    private Date joinTime;

    // DTO -> Entity conversion (requires FK objects)
    public Employee toEntity(Department department, RoleCode role) {
        return new Employee(
            this.empNo,
            this.name,
            this.email,
            this.pw,
            this.tel,
            department,
            role,
            this.joinTime
        );
    }
}
