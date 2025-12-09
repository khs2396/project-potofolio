package com.example.Intranet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLECODE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCode {
	@Id
	@Column(name = "role_id", nullable = false)
	private Integer roleId;

	@Column(name = "role_name", nullable = false)
	private String roleName;
}
