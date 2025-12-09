package com.example.KG25.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.KG25.dao.OrderingDAO;
import com.example.KG25.dto.OrderingDTO;
import com.example.KG25.entity.Ordering;

@Service
public class OrderingService {
	@Autowired
	OrderingDAO dao;
	
	public int getCount() {
		return dao.getCount();
	}
	
	public int getCountById(String id) {
		return dao.getCountById(id);
	}
	
	public List<Ordering> findAllOrderings(int startNum, int endNum) {
		return dao.findAllOrderings(startNum, endNum);
	}
	
	public List<Ordering> findByIdAndPaging(String id, int startNum, int endNum) {
		return dao.findByIdAndPaging(id, startNum, endNum);
	}
	
	public Ordering orderingView(int seq) {
		return dao.orderingView(seq);
	}
	
	public Ordering orderingWrite(OrderingDTO dto) {
		return dao.orderingWrite(dto);
	}
	
	public boolean orderingModify(OrderingDTO dto) {
		return dao.orderingModify(dto);
	}
	
	public boolean orderingDelete(int seq) {
		return dao.orderingDelete(seq);
	}
	
	public Ordering findByPdname(String pdname) {
		return dao.findByPdname(pdname);
	}

	public List<Ordering> findById(String id) {
		return dao.findById(id);
	}
}
