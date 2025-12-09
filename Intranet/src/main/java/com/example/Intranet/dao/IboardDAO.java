package com.example.Intranet.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.IboardDTO;
import com.example.Intranet.entity.Iboard;
import com.example.Intranet.repository.IboardRepository;

@Repository
public class IboardDAO {

		@Autowired
		IboardRepository iboardRepository;
		
		// 전체 게시판 수 구하기
		public int getIboardCount() {
			return (int)iboardRepository.count();
		}
		
		// 게시판 저장
		public Iboard iboardWrite(IboardDTO dto) {
			return iboardRepository.save(dto.toEntity());
		}
		
		// 게시판 목록
		public List<Iboard> iboardList(int startNum, int endNum){
			return iboardRepository.findByStartnumAndEndnum(startNum, endNum);
		}
		
		// 게시판 상세보기
		public Iboard iboardView(int boardseq) {
			return iboardRepository.findById(boardseq).orElse(null);
		}
		
		// 게시판 조회수 증가
		public Iboard updateHit(int boardseq) {
			Iboard iboard = iboardRepository.findById(boardseq).orElse(null);
			if(iboard != null) {
				int hit = iboard.getHit();
				iboard.setHit(hit+1);
				return iboardRepository.save(iboard);
			} else {
				return null;
			}
		}
		
		// 게시판 수정하기
		public int iboardModify(IboardDTO dto) {
			int result = 0;
			Iboard iboard = iboardRepository.findById(dto.getBoardseq()).orElse(null);
			
			if(iboard != null) {
				iboard.setSubject(dto.getSubject());
				iboard.setContent(dto.getContent());;
				Iboard iboard_result = iboardRepository.save(iboard);
				if(iboard_result != null) {
					result = 1;
				}
			}
			return result;
		}
		
		// 게시판 삭제하기
		public int iboardDelete(int boardseq) {
			int result = 0;
			Iboard iboard = iboardRepository.findById(boardseq).orElse(null);
			
			if(iboard != null) {
				iboardRepository.delete(iboard);
				if(!iboardRepository.existsById(boardseq)) {
					result = 1;
				}
			} else {
				return result;
			}
			return result;
		}
}
