package com.example.KG25.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.KG25.dto.ProductDTO;
import com.example.KG25.entity.Product;
import com.example.KG25.repository.ProductRepository;

@Repository
public class ProductDAO {
	@Autowired
	ProductRepository repository;
	
	public int getCount() {
		return (int) repository.count();
	}
	
	public List<Product> productList(int startNum, int endNum) {
		return repository.findByStartNumAndEndNum(startNum, endNum);
	}
	
	public Product productView(String pdname) {
		return repository.findById(pdname).orElse(null);
	}
	
	public Product productWrite(ProductDTO dto) {
		return repository.save(dto.toEntity());
	}
	
	public boolean productModify(ProductDTO dto) {
		Product product = repository.findById(dto.getPdname()).orElse(null);
		boolean result = false;
		
		if(product != null) {
			product.setPdname(dto.getPdname());
			product.setPrice(dto.getPrice());
			product.setCat(dto.getCat());
			product.setImage1(dto.getImage1());
			
			Product product_result = repository.save(product);
			if(product_result != null) {
				result = true;
			}
		}
		return result;
	}
	
	public boolean productDelete(String pdname) {
		Product product = repository.findById(pdname).orElse(null);
		boolean result = false;
		
		if(product != null) {
			repository.delete(product);
			if(!repository.existsById(pdname)) {
				result = true;
			}
		}
		return result;
	}
	
	public List<Product> findByCategory(String cat) {
		return repository.findByCat(cat);
	}
	
	public int getCountByCat(String cat) {
		return repository.getCountByCat(cat);
	}
	
	public List<Product> findByCatWithPaging(String cat, int startNum, int endNum) {
		return repository.findByCatWithPaging(cat, startNum, endNum);
	}
}
