package com.example.KG25.dto;

import com.example.KG25.entity.Orderlist;

import lombok.Data;

@Data
public class OrderListDTO {
	private String id;
	private String pdname;
	private int price;
	private String cat;
	private int qty;
	private int total;
	
	public Orderlist Entity() {
		return new Orderlist(id, pdname, price, cat, qty, total);
	}
}
