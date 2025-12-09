package com.example.KG25.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.KG25.dto.OrderingDTO;
import com.example.KG25.dto.ProductDTO;
import com.example.KG25.entity.Manager;
import com.example.KG25.entity.Ordering;
import com.example.KG25.entity.Orderlist;
import com.example.KG25.entity.Product;
import com.example.KG25.service.ManagerService;
import com.example.KG25.service.OrderListService;
import com.example.KG25.service.OrderingService;
import com.example.KG25.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

	@Autowired
	ProductService service;
	@Autowired
	ManagerService service2;
	@Autowired
	OrderListService orderListService; // 🔥 이 부분 추가
	
	@Autowired
	OrderingService orderingService;
	
	@Value("${project.upload.path}")
	private String uploadpath;

   
	
	@GetMapping("productList")
	public String productList(HttpServletRequest request, Model model,
			@RequestParam(name="cat", required = false) String cat) {
		int pg = 1;
		if (request.getParameter("pg") != null) {
			pg = Integer.parseInt(request.getParameter("pg"));
		}
		
		int endNum = pg * 5;
		int startNum = endNum - 4;
		List<Product> list = null;
		int totalA = 0;
		if(cat == null || cat.isEmpty()) {
			list = service.productList(startNum, endNum);
			totalA = service.getCount();
		} else {
			list = service.findByCatWithPaging(cat, startNum, endNum);
			totalA = service.getCountByCat(cat);
		}
		
		int totalP = (totalA + 4) / 5; // 총 페이지 수
		
		int startPage = (pg - 1) / 3 * 3 + 1;
		int endPage = startPage + 2;
		if (endPage > totalP)
			endPage = totalP;
		
		model.addAttribute("cat", cat);
		model.addAttribute("pg", pg);
		model.addAttribute("list", list);
		model.addAttribute("totalP", totalP);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		return "product/productList";
	}
	
	@GetMapping("productView")
	public String productView(HttpServletRequest request, Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));
		String pdname = request.getParameter("pdname");
		
		Product product = service.productView(pdname);
		
		model.addAttribute("pg", pg);
		model.addAttribute("pdname", pdname);
		model.addAttribute("product", product);
		
		return "product/productView";
	}
	
	@GetMapping("productWriteForm")
	public String productWriteForm() {
		return "product/productWriteForm";
	}
	
	@PostMapping("productWrite")
	public String productWrite(ProductDTO dto, Model model,
			@RequestParam(value="image")MultipartFile uploadfile) {
		String fileName = uploadfile.getOriginalFilename();
		dto.setImage1(fileName);
		File folder = new File(uploadpath);
		if(!folder.exists()) {
			folder.mkdirs(); // 폴더 만들기
		}
		if(!fileName.equals("")) {
			File file = new File(uploadpath, fileName);
			try {
				uploadfile.transferTo(file);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Product product = service.productWrite(dto);
		
		model.addAttribute("product", product);
		return "product/productWrite";
	}
	
	@GetMapping("productModifyForm")
	public String productModifyForm(HttpServletRequest request, Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));
		String pdname = request.getParameter("pdname");
		
		Product product = service.productView(pdname);
		
		model.addAttribute("pg", pg);
		model.addAttribute("pdname", pdname);
		model.addAttribute("product", product);
		
		return "product/productModifyForm";
	}
	
	@PostMapping("productModify")
	public String productModify(HttpServletRequest request, ProductDTO dto, Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));

		Product product = service.productView(dto.getPdname());
		String image1 = product.getImage1();
		dto.setImage1(image1);

		boolean result = service.productModify(dto);

		   // 👉 가격이 변경되지 않았더라도 무조건 동기화
	    if (result) {
	        orderListService.updatePriceInOrderList(dto.getPdname(), dto.getPrice());
	    }

		model.addAttribute("pg", pg);
		model.addAttribute("result", result);

		return "product/productModify";
	}
	
	@GetMapping("productDelete")
	public String productDelete(HttpServletRequest request, Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));
		String pdname = request.getParameter("pdname");
		
		boolean result = service.productDelete(pdname);
		
		model.addAttribute("pg", pg);
		model.addAttribute("result", result);
		
		return "product/productDelete";
	}
	
	@PostMapping("productPurchase")
	public String productPurchase(HttpSession session, HttpServletRequest request, 
			Model model) {
		String pdname = request.getParameter("pdname");
		String id = (String) session.getAttribute("managerId");
		int price = Integer.parseInt(request.getParameter("price"));
		int qty = Integer.parseInt(request.getParameter("qty"));
		
		int total = price * qty;
		
		Manager manager = service2.managerInfo(id);
		String addr = manager.getAddr();
		
		OrderingDTO dto = new OrderingDTO();
		dto.setPdname(pdname);
		dto.setId(id);
		dto.setAddr(addr);
		dto.setTotal(total);
	    dto.setLogdate(new Date());
		dto.setIng("승인중");
		
		orderingService.orderingWrite(dto);
		
		
		
		model.addAttribute("total", total);
		
		return "product/productPurchase";
	}
}
