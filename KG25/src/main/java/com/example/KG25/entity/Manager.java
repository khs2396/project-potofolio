package com.example.KG25.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
	@Id
	private String id;
	private String name;
	private String pw;
	private String addr;
	private String tel;
	private int code;
}
