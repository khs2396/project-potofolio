package com.example.KG25.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.KG25.dto.OrderingDTO;
import com.example.KG25.entity.Ordering;
import com.example.KG25.repository.OrderingRepository;

@Repository
public class OrderingDAO {
	@Autowired
	OrderingRepository repository;
	
	public int getCount() {
		return (int)repository.count();
	}
	
	public int getCountById(String id) {
		return (int)repository.getCountById(id);
	}
	
	public List<Ordering> findAllOrderings(int startNum, int endNum) {
		return repository.findAllOrderings(startNum, endNum);
	}
	
	public List<Ordering> findByIdAndPaging(String id, int startNum, int endNum) {
		return repository.findByIdAndPaging(id, startNum, endNum);
	}
	
	public Ordering orderingView(int seq) {
		return repository.findById(seq).orElse(null);
	}
	
	public Ordering orderingWrite(OrderingDTO dto) {
		return repository.save(dto.toEntity());
	}
	
	public boolean orderingModify(OrderingDTO dto) {
		Ordering ordering = repository.findById(dto.getSeq()).orElse(null);
		boolean result = false;
		if(ordering != null) {
			ordering.setIng(dto.getIng());
			
			Ordering ordering_result = repository.save(ordering);
			if(ordering_result != null) {
				result = true;
			}
		}
		return result;
	}
	
	public boolean orderingDelete(int seq) {
		Ordering ordering = repository.findById(seq).orElse(null);
		boolean result = false;
		if(ordering != null) {
			repository.delete(ordering);
			if(!repository.existsById(seq)) {
				result = true;
			}
		}
		return result;
	}
	
	public Ordering findByPdname(String pdname) {
		Ordering ordering = repository.findByPdname(pdname);
		return ordering;
	}
	
	public List<Ordering> findById(String id) {
		return repository.findById(id);
	}
}
