package com.example.Intranet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Intranet.dao.NoticeDAO;
import com.example.Intranet.dto.NoticeDTO;
import com.example.Intranet.entity.Notice;

@Service
public class NoticeService {
	@Autowired
	NoticeDAO noticeDao;
	
	// 전체 공지 수
	public int getNoticeCount() {
		return noticeDao.getNoticeCount();
	}
	
	// 공지 등록
	public Notice noticeWrite(NoticeDTO dto) {
		return noticeDao.noticeWrite(dto);
	}
	
	// 공지 목록
	public List<Notice> noticeList(int startNum, int endNum){
		return noticeDao.noticeList(startNum, endNum);
	}
	
	// 공지 상세
	public Notice noticeView(int seq) {
		return noticeDao.noticeView(seq);
	}
	
	// 조회수 증가
	public Notice updateHit(int seq) {
		return noticeDao.updateHit(seq);
	}
	
	// 공지 수정
	public int noticeModify(NoticeDTO dto) {
		return noticeDao.noticeModify(dto);
	}
	
	// 공지 삭제
	public int noticeDelete(int seq) {
		return noticeDao.noticeDelete(seq);
	}
}
