package com.example.KG25.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.KG25.dao.OrderListDAO;
import com.example.KG25.dto.OrderListDTO;
import com.example.KG25.entity.Orderlist;

import jakarta.transaction.Transactional;

@Service
public class OrderListService {
	@Autowired
	OrderListDAO dao;

	// 장바구니 목록 리스트
	public List<Orderlist> shoppingBasketList(int startNum, int endNum) {
		return dao.shoppingBasketList(startNum, endNum);
	}
	
	public List<Orderlist> findAllById(String id) {
		return dao.findAllById(id);
	}

	// 전체 데이터수 구하기
	public int getTotalA() {
		return dao.getTotalA();
	}

	// 장바구니 저장
	public Orderlist shoppingBasketSave(OrderListDTO dto) {
		return dao.shoppingBasketSave(dto);
	}

	// 장바구니 목록 삭제
	public int shoppingBasketDelete(String id, String pdname) {
		return dao.shoppingBasketDelete(id, pdname);
	}

	public List<Orderlist> shoppingBasketList(String id, int startNum, int endNum) {
		return dao.shoppingBasketList(id, startNum, endNum);
	}
	// id별 장바구니 목록 개수
	public int getTotalAById(String id) {
		return dao.getTotalAById(id);
	}

	// id별 장바구니 목록 총액
	public Integer getTotalAmountById(String id) {
		return dao.getTotalAmountById(id);
	}
	
	@Transactional // 👉 트랜잭션 추가
	public int updatePriceInOrderList(String pdname, int newPrice) {
	    return dao.updatePriceInOrderList(pdname, newPrice);
	}
	
	@Transactional
	public int deleteAllById(String id) {
		return dao.deleteAllById(id);
	}

}
