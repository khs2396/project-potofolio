package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.NoticeDTO;
import com.example.Intranet.entity.Notice;
import com.example.Intranet.repository.NoticeRepository;

@Repository
public class NoticeDAO {
	@Autowired
	NoticeRepository noticeRepository;
	
	// 전체 공지사항 수 구하기
	public int getNoticeCount() {
		return (int)noticeRepository.count();
	}
	
	// 공지사항 저장
	public Notice noticeWrite(NoticeDTO dto) {
		return noticeRepository.save(dto.toEntity());
	}
	
	// 공지사항 목록
	public List<Notice> noticeList(int startNum, int endNum){
		return noticeRepository.findByStartnumAndEndnum(startNum, endNum);
	}
	
	// 공지사항 상세보기
	public Notice noticeView(int seq) {
		return noticeRepository.findById(seq).orElse(null);
	}
	
	// 공지사항 조회수 증가
	public Notice updateHit(int seq) {
		Notice notice = noticeRepository.findById(seq).orElse(null);
		if(notice != null) {
			int hit = notice.getHit();
			notice.setHit(hit+1);
			return noticeRepository.save(notice);
		} else {
			return null;
		}
	}
	
	// 공지사항 수정하기
	public int noticeModify(NoticeDTO dto) {
		int result = 0;
		Notice notice = noticeRepository.findById(dto.getSeq()).orElse(null);
		
		if(notice != null) {
			notice.setSubject(dto.getSubject());
			notice.setContent(dto.getContent());;
			Notice notice_result = noticeRepository.save(notice);
			if(notice_result != null) {
				result = 1;
			}
		}
		return result;
	}
	
	// 공지사항 삭제하기
	public int noticeDelete(int seq) {
		int result = 0;
		Notice notice = noticeRepository.findById(seq).orElse(null);
		
		if(notice != null) {
			noticeRepository.delete(notice);
			if(!noticeRepository.existsById(seq)) {
				result = 1;
			}
		} else {
			return result;
		}
		return result;
	}

}
