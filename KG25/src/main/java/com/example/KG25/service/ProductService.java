package com.example.KG25.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.KG25.dao.ProductDAO;
import com.example.KG25.dto.ProductDTO;
import com.example.KG25.entity.Product;

@Service
public class ProductService {
	@Autowired
	ProductDAO dao;
	
	public int getCount() {
		return dao.getCount();
	}
	
	public List<Product> productList(int startNum, int endNum) {
		return dao.productList(startNum, endNum);
	}
	
	public Product productWrite(ProductDTO dto) {
		return dao.productWrite(dto);
	}
	
	public boolean productModify(ProductDTO dto) {
		return dao.productModify(dto);
	}
	
	public boolean productDelete(String pdname) {
		return dao.productDelete(pdname);
	}
	
	public Product productView(String pdname) {
		return dao.productView(pdname);
	}
	
	public List<Product> findByCategory(String cat) {
		return dao.findByCategory(cat);
	}
	
	public int getCountByCat(String cat) {
		return dao.getCountByCat(cat);
	}
	
	public List<Product> findByCatWithPaging(String cat, int startNum, int endNum) {
		return dao.findByCatWithPaging(cat, startNum, endNum);
	}
}
