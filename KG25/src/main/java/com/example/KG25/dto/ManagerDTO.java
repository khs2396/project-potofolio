package com.example.KG25.dto;

import com.example.KG25.entity.Manager;

import lombok.Data;

@Data
public class ManagerDTO {
	  private String id;
	  private String name;
	  private String pw;
	  private String addr;
	  private String tel;
	  private int code;
	  
	  public Manager toEntity() {
		  return new Manager(id, name, pw, addr, tel, code);
	  }			  
}
