package com.example.KG25.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfiguration implements WebMvcConfigurer{
	@Value("${project.upload.path}")
	private String uploadpath;	
	
	// 파일 저장 폴더 연결 함수
	// http://localhost:8080/storage/cupra.jpg
	// => 브라우저의 요청에 응답할 폴더 등록 설정
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/storage/**")
		.addResourceLocations("file:///" + uploadpath + "/");
	}
}
