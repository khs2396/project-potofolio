package com.example.KG25.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.KG25.dto.NoticeDTO;
import com.example.KG25.entity.Notice;
import com.example.KG25.repository.NoticeRepository;

@Repository
public class NoticeDAO {
	@Autowired
	NoticeRepository noticeRepository;
	
	public int getCount() {
		return (int)noticeRepository.count();
	}
	
	public List<Notice> noticeList(int startNum,int endNum) {
		return noticeRepository.findByStartNumAndEndNum(startNum, endNum);
	}
	
	public Notice noticeView(int seq) {
		return noticeRepository.findById(seq).orElse(null);
	}
	
	public Notice noticeWrite(NoticeDTO dto) {
		return noticeRepository.save(dto.toEntity());
	}
	
	public boolean noticeModify(NoticeDTO dto) {
		Notice notice = noticeRepository.findById(dto.getSeq()).orElse(null);
		
		boolean result = false;
		if(notice != null) {
			notice.setSubject(dto.getSubject());
			notice.setContent(dto.getContent());
			
			Notice notice_result = noticeRepository.save(notice);
			if(notice_result != null) {
				result = true;
			}
		}
		return result;
	}
	
	public boolean noticeDelete(int seq) {
		Notice notice = noticeRepository.findById(seq).orElse(null);
		
		boolean result = false;
		if(notice != null) {
			noticeRepository.delete(notice);
			if(!noticeRepository.existsById(seq)) {
				result = true;
			}
		}
		return result;
	}
}
