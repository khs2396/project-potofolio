package com.example.KG25.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.KG25.dao.NoticeDAO;
import com.example.KG25.dto.NoticeDTO;
import com.example.KG25.entity.Notice;

@Service
public class NoticeService {
	@Autowired
	NoticeDAO dao;
	
	public int getCount() {
		return dao.getCount();
	}
	
	public List<Notice> noticeList(int startNum, int endNum) {
		return dao.noticeList(startNum, endNum);
	}
	
	public Notice noticeView(int seq) {
		return dao.noticeView(seq);
	}
	
	public Notice noticeWrite(NoticeDTO dto) {
		return dao.noticeWrite(dto);
	}
	
	public boolean noticeModify(NoticeDTO dto) {
		return dao.noticeModify(dto);
	}
	
	public boolean noticeDelete(int seq) {
		return dao.noticeDelete(seq);
	}
}
