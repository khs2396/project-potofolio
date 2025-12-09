package com.example.KG25.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(OrderlistId.class)
public class Orderlist {
	@Id		
	private String id;
	@Id
	private String pdname;
	
	private int price;	
	private String cat;
	private int qty;
	private int total;
}

