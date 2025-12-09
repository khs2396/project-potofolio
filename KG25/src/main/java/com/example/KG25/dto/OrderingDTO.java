package com.example.KG25.dto;

import java.util.Date;

import com.example.KG25.entity.Ordering;

import lombok.Data;

@Data
public class OrderingDTO {
	
	 private int seq;
	 private String pdname;
	 private String id;
	 private String addr;
	 private int total;
	 private Date logdate;
	 private String ing;
	 
	 public Ordering toEntity() {
		 return new Ordering(seq, pdname, id, addr, total, logdate, ing);
	 }
}
