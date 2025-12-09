package com.example.KG25.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {
	
	@GetMapping("/mapSearchForm") 
	public String map() {
		return "/map/mapSearchForm";
	}
}
