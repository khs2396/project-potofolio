package com.example.KG25.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.KG25.dto.OrderingDTO;
import com.example.KG25.entity.Manager;
import com.example.KG25.entity.Ordering;
import com.example.KG25.service.ManagerService;
import com.example.KG25.service.OrderingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderingController {
	@Autowired
	OrderingService service;
	@Autowired
	ManagerService service2;
	
	@GetMapping("orderingList")
	public String orderingList(HttpServletRequest request, HttpSession session
			, Model model) {
		String id = (String) session.getAttribute("managerId");
		Manager manager = service2.managerInfo(id);
		
		int pg = 1;
		if(request.getParameter("pg") != null) {
			pg = Integer.parseInt(request.getParameter("pg"));
		}
		
		int endNum = pg * 5;
		int startNum = endNum - 4;
		
		List<Ordering> list = null;
		int totalA = 0;
		if(manager.getCode() > 0) {
			list = service.findAllOrderings(startNum, endNum);
			totalA = service.getCount();
		} else {
			list = service.findByIdAndPaging(id, startNum, endNum);
			totalA = service.getCountById(id);
		}
		
		int totalP = (totalA + 4)/5;
		
		int startPage = (pg - 1)/3 * 3 + 1;
		int endPage = startPage + 2;
		if(endPage > totalP) {
			endPage = totalP;
		}
		model.addAttribute("pg", pg);
		model.addAttribute("list", list);
		model.addAttribute("totalP", totalP);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		return "ordering/orderingList";
	}
	
	@GetMapping("orderingView")
	public String orderingView(HttpServletRequest request, Model model) {
		int seq = Integer.parseInt(request.getParameter("seq"));
		
		Ordering ordering = service.orderingView(seq);
		
		model.addAttribute("seq", seq);
		model.addAttribute("ordering", ordering);
		
		return "ordering/orderingView";
	}
	
	@GetMapping("orderingDelete")
	public String orderingDelete(HttpServletRequest request, Model model) {
		int seq = Integer.parseInt(request.getParameter("seq"));
		
		boolean result = service.orderingDelete(seq);
		
		model.addAttribute("seq", seq);
		model.addAttribute("result", result);
		
		return "ordering/orderingDelete";
	}
	
	@GetMapping("orderingModifyForm")
	public String orderingModifyForm(HttpServletRequest request, Model model) {
		int seq = Integer.parseInt(request.getParameter("seq"));
		
		Ordering ordering = service.orderingView(seq);
		
		model.addAttribute("seq", seq);
		model.addAttribute("ordering", ordering);
		
		return "ordering/orderingModifyForm";
	}
	
	@GetMapping("orderingModify")
	public String orderingModify(HttpServletRequest request, Model model) {
		int seq = Integer.parseInt(request.getParameter("seq"));
		String ing = request.getParameter("ing");
		
		OrderingDTO dto = new OrderingDTO();
		dto.setSeq(seq);
		dto.setIng(ing);
		
		Ordering ordering = service.orderingView(seq);
		dto.setPdname(ordering.getPdname());
		dto.setId(ordering.getId());
		dto.setAddr(ordering.getAddr());
		dto.setTotal(ordering.getTotal());
		dto.setLogdate(ordering.getLogdate());
		
		boolean result = service.orderingModify(dto);
		
		model.addAttribute("result", result);
		model.addAttribute("dto", dto);
		
		return "ordering/orderingModify";
	}
}
