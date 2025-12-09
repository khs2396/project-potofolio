package com.example.KG25.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/index") 
	public String index() {
		return "/main/index";
	}
	
	@GetMapping("/introduceForm") 
	public String indtroduce() {
		return "/about/introduceForm";
	}
	
	@GetMapping("/helpForm") 
	public String map() {
		return "/main/helpForm";
	}
}
