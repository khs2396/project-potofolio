package com.example.KG25.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.KG25.dto.OrderListDTO;
import com.example.KG25.dto.OrderingDTO;
import com.example.KG25.entity.Manager;
import com.example.KG25.entity.Orderlist;
import com.example.KG25.service.ManagerService;
import com.example.KG25.service.OrderListService;
import com.example.KG25.service.OrderingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderListController {

	@Autowired
	OrderListService service;
	
	@Autowired
	ManagerService service2;
	
	@Autowired
	OrderingService service3;

	// 장바구니 저장
	@PostMapping("/shoppingBasket")
	public String shoppingBasket(HttpServletRequest request, Model model, HttpSession session) {
		// 1.데이터 처리
		String pdname = request.getParameter("pdname");
		String cat = request.getParameter("cat");
		int price = Integer.parseInt(request.getParameter("price"));
		int qty = Integer.parseInt(request.getParameter("qty"));
		int pg = Integer.parseInt(request.getParameter("pg"));
		String id = (String)session.getAttribute("managerId");

		// 확인용 출력
		System.out.println("pdname = " + pdname);
		System.out.println("cat = " + cat);
		System.out.println("price = " + price);
		System.out.println("qty = " + qty);
		System.out.println("id = " + id);
		System.out.println("pg = " + pg);

		OrderListDTO dto = new OrderListDTO();
		dto.setId(id);
		dto.setPdname(pdname);
		dto.setCat(cat);
		dto.setPrice(price);
		dto.setQty(qty);
		dto.setTotal(price * qty);

		// db 저장
		Orderlist orderList = service.shoppingBasketSave(dto);
		System.out.println("orderList = " + orderList);

		int result = (orderList != null) ? 1 : 0;
		
		// 2.데이터 공유
		model.addAttribute("result", result);
		model.addAttribute("pg", pg);

		// 3.view처리 파일명 리턴
		return "/orderList/inputBasket";

	}

	// 장바구니 목록 리스트
	@GetMapping("/shoppingBasketList")
	public String shoppingBasketList(HttpServletRequest request, Model model, HttpSession session) {
		// 1.데이터 처리
		int pg = 1;
		if (request.getParameter("pg") != null) {
			pg = Integer.parseInt(request.getParameter("pg"));
		}
		String id = (String) session.getAttribute("managerId");  
		
		int endNum = pg * 5;
		int startNum = endNum - 4;

		List<Orderlist> list = service.shoppingBasketList(id, startNum, endNum);
				
		 // 로그인한 사용자의 장바구니 전체 총액 합계
	    int totalAmount = service.getTotalAmountById(id);

		int totalA = service.getTotalAById(id);
		int totalP = (totalA + 4) / 5;

		int startPage = (pg - 1) / 3 * 3 + 1;
		int endPage = startPage + 2;
		if (endPage > totalP) {
			endPage = totalP;
		}

		// 2.데이터 공유
		model.addAttribute("list", list);
		model.addAttribute("pg", pg);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalP", totalP);
		model.addAttribute("totalAmount", totalAmount);

		// 3.view처리 파일명 리턴
		return "/orderList/shoppingBasketList";
	}

	// 장바구니 목록 삭제
	@GetMapping("/shoppingBasketDelete")
	public String shoppingBasketDelete(HttpServletRequest request, Model model, HttpSession session) {
		// 1.데이터 처리
		int pg = Integer.parseInt(request.getParameter("pg"));
		String pdname = request.getParameter("pdname");
		String id = (String)session.getAttribute("managerId");
		
		// db 
		int result = service.shoppingBasketDelete(id,pdname);
		
		// 2.데이터 공유
		model.addAttribute("result", result);
		model.addAttribute("pg", pg);
		
		// 3.view처리 파일명 리턴
		return "/orderList/shoppingBasketDelete";
	}
	
	@GetMapping("/orderListPurchase")
	   public String orderListPurchase(HttpServletRequest request, HttpSession session,
	         Model model) {
	      int totalAmount = Integer.parseInt(request.getParameter("totalAmount"));
	      String id = (String) session.getAttribute("managerId");
	      if(totalAmount > 0) {
	         Manager manager = service2.managerInfo(id);
	         
	         String addr = manager.getAddr();
	         
	         List<Orderlist> list = service.findAllById(id);
	         
	         String pdname = list.get(0).getPdname() + "등등";
	         
	         OrderingDTO dto = new OrderingDTO();
	         dto.setPdname(pdname);
	         dto.setId(id);
	         dto.setAddr(addr);
	         dto.setTotal(totalAmount);
	          dto.setLogdate(new Date());
	         dto.setIng("승인중");
	         
	         service3.orderingWrite(dto);
	         
	         service.deleteAllById(id);
	         
	         model.addAttribute("totalAmount", totalAmount);
	         
	         return "orderList/orderListPurchase";
	      } 
	      return "orderList/orderListPurchaseFail";
	   }
}

// 1.데이터 처리
// 2.데이터 공유
// 3.view처리 파일명 리턴