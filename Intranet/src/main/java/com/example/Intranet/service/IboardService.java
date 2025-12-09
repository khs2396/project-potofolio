package com.example.Intranet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Intranet.dao.IboardDAO;
import com.example.Intranet.dto.IboardDTO;
import com.example.Intranet.entity.Iboard;

@Service
public class IboardService {
	@Autowired
	IboardDAO iboardDao;

	// 전체 게시판 수 구하기
	public int getIboardCount() {
		return iboardDao.getIboardCount();
	}

	// 게시판 저장
	public Iboard iboardWrite(IboardDTO dto) {
		return iboardDao.iboardWrite(dto);
	}

	// 게시판 목록
	public List<Iboard> iboardList(int startNum, int endNum) {
		return iboardDao.iboardList(startNum, endNum);
	}

	// 게시판 상세보기
	public Iboard iboardView(int boardseq) {
		return iboardDao.iboardView(boardseq);
	}

	// 게시판 조회수 증가
	public Iboard updateHit(int boardseq) {
		return iboardDao.updateHit(boardseq);
	}

	// 게시판 수정하기
	public int iboardModify(IboardDTO dto) {
		return iboardDao.iboardModify(dto);
	}

	// 게시판 삭제하기
	public int iboardDelete(int boardseq) {
		return iboardDao.iboardDelete(boardseq);
	}
}
