package com.example.KG25.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.KG25.dto.OrderListDTO;
import com.example.KG25.entity.Orderlist;
import com.example.KG25.entity.Product;
import com.example.KG25.repository.OrderListRepository;

@Repository
public class OrderListDAO {
	@Autowired
	OrderListRepository repository;

	// 장바구니 목록 리스트
	public List<Orderlist> shoppingBasketList(int startNum, int endNum) {
		return repository.findByStartNumAndEndNum(startNum, endNum);
	}
	
	public List<Orderlist> findAllById(String id) {
		return repository.findAllById(id);
	}

	// 전체 데이터 개수 구하기
	public int getTotalA() {
		return (int) repository.count();
	}
	
	// 장바구니 저장
	public Orderlist shoppingBasketSave(OrderListDTO dto) {
	    String pdname = dto.getPdname();
	    String id = dto.getId();
	    Orderlist existing = repository.findByIdAndPdname(id, pdname);

	    if (existing != null) {
	        // 기존 수량과 합계를 누적
	        int newQty = existing.getQty() + dto.getQty();
	        int newTotal = newQty * dto.getPrice(); // 가격은 동일하다고 가정

	        existing.setQty(newQty);
	        existing.setTotal(newTotal);

	        return repository.save(existing); // 수정된 데이터 저장
	    } else {
	        return repository.save(dto.Entity()); // 새로 저장
	    }
	}
	
	// 장바구니 목록 삭제
	public int shoppingBasketDelete(String id, String pdname) {
		// 기존 데이터 가져오기
		Orderlist orderList = repository.findByIdAndPdname(id, pdname);
		int result = 0;
		if(orderList != null) {
			repository.delete(orderList);
			if(!repository.existsByIdAndPdname(id, pdname)) {
				result = 1;
			}
		}
		return result;
	}
	
	// id별 목록 리스트
	public List<Orderlist> shoppingBasketList(String id, int startNum, int endNum) {
	    return repository.findByIdWithPaging(id, startNum, endNum);
	}
	
	// id별 장바구니 목록 개수
	public int getTotalAById(String id) {
	    return repository.countById(id);
	}
	
	// id별 장바구니 목록 총액
	public Integer getTotalAmountById(String id) {
	    Integer total = repository.sumTotalById(id);
	    return total != null ? total : 0;
	}
	
	// 상품 가격 변경 시 장바구니 가격 동기화
	public int updatePriceInOrderList(String pdname, int newPrice) {
	    List<Orderlist> orderLists = repository.findAllByPdname(pdname);
	    int count = 0;
	    for (Orderlist order : orderLists) {
	        order.setPrice(newPrice);
	        order.setTotal(newPrice * order.getQty());
	        repository.save(order);
	        count++;
	    }
	    return count;
	}

	public int deleteAllById(String id) {
		return repository.deleteAllById(id);
	}


}
