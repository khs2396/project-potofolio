package com.example.KG25.dto;

import com.example.KG25.entity.Product;

import lombok.Data;

@Data
public class ProductDTO {
	private String pdname;
	private String cat;
	private int price;
	private String image1;
	
	public Product toEntity() {
		return new Product(pdname, cat, price, image1);
	}
}
