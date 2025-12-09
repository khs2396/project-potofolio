package com.example.KG25.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.KG25.dto.NoticeDTO;
import com.example.KG25.entity.Notice;
import com.example.KG25.service.NoticeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class NoticeController {

	@Autowired
	NoticeService service;
	
	@Value("${project.upload.path}")
	private String uploadpath;
	
	@GetMapping("noticeList")
	public String noticeList(HttpServletRequest request, Model model) {
		int pg = 1;
		if (request.getParameter("pg") != null) {
			pg = Integer.parseInt(request.getParameter("pg"));
		}

		// 1) 목록 작업 : 1페이지당 5개
		int endNum = pg * 5;
		int startNum = endNum - 4;
		List<Notice> list = service.noticeList(startNum, endNum);
		
		// 2) 페이징 작업 : 3블럭
		int totalA = service.getCount(); // 전체 데이터 개수
		int totalP = (totalA + 4) / 5; // 총 페이지 수
		
		int startPage = (pg - 1) / 3 * 3 + 1;
		int endPage = startPage + 2;
		if (endPage > totalP)
			endPage = totalP;
		
		model.addAttribute("pg", pg);
		model.addAttribute("list", list);
		model.addAttribute("totalP", totalP);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		return "notice/noticeList";
	}
	
	@GetMapping("noticeView")
	public String noticeView(HttpServletRequest request, Model model) {
		int seq = Integer.parseInt(request.getParameter("seq"));
		int pg = Integer.parseInt(request.getParameter("pg"));
		
		Notice notice = service.noticeView(seq);
		
		model.addAttribute("pg", pg);
		model.addAttribute("seq", seq);
		model.addAttribute("notice", notice);
		
		return "notice/noticeView";
	}
	
	@GetMapping("noticeWriteForm")
	public String noticeWriteForm() {
		return "notice/noticeWriteForm";
	}
	
	@PostMapping("noticeWrite")
	public String noticeWrite(NoticeDTO dto, Model model,
			@RequestParam(value="img1") MultipartFile uploadfile) {
		String fileName = uploadfile.getOriginalFilename();
		dto.setImg(fileName);
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
		Notice notice = service.noticeWrite(dto);
		model.addAttribute("notice", notice);
		return "notice/noticeWrite";
	}
	
	@GetMapping("noticeModifyForm")
	public String noticeModifyForm(HttpServletRequest request, Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));
		int seq = Integer.parseInt(request.getParameter("seq"));
		
		Notice notice = service.noticeView(seq);
		model.addAttribute("pg", pg);
		model.addAttribute("seq", seq);
		model.addAttribute("notice", notice);
		return "notice/noticeModifyForm";
	}
	
	@PostMapping("noticeModify")
	   public String noticeModify(HttpServletRequest request, Model model,
	         @RequestParam(value = "img1", required = false) MultipartFile imgFile) {
	      int seq = Integer.parseInt(request.getParameter("seq"));
	      int pg = Integer.parseInt(request.getParameter("pg"));
	      String subject = request.getParameter("subject");
	      String content = request.getParameter("content");
	      
	      String uploadDir = "C:/upload/";

	       // 새 이미지 파일명
	       String fileName = null;

	       try {
	           if (imgFile != null && !imgFile.isEmpty()) {
	               fileName = UUID.randomUUID().toString() + "_" + imgFile.getOriginalFilename();
	               File saveFile = new File(uploadDir, fileName);
	               imgFile.transferTo(saveFile); // 디스크에 파일 저장
	           }
	       } catch (IOException e) {
	           e.printStackTrace();
	           model.addAttribute("result", false);
	           return "notice/noticeModify"; // 파일 저장 실패 시
	       }

	       // DTO 생성
	       NoticeDTO dto = new NoticeDTO();
	       dto.setSeq(seq);
	       dto.setSubject(subject);
	       dto.setContent(content);
	       dto.setImg(fileName); // 새 이미지 파일명 저장

	       // 서비스 호출
	       boolean result = service.noticeModify(dto);

	       model.addAttribute("pg", pg);
	       model.addAttribute("seq", seq);
	       model.addAttribute("result", result);

	       return "notice/noticeModify";
	   }
	
	@GetMapping("noticeDelete")
	public String noticeDelete(HttpServletRequest request,Model model) {
		int pg = Integer.parseInt(request.getParameter("pg"));
		int seq = Integer.parseInt(request.getParameter("seq"));
		
		boolean result = service.noticeDelete(seq);
		
		model.addAttribute("pg", pg);
		model.addAttribute("seq", seq);
		model.addAttribute("result", result);
		
		return "notice/noticeDelete";
	}
}
