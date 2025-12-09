package com.example.KG25.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.KG25.dao.ManagerDAO;
import com.example.KG25.dto.ManagerDTO;
import com.example.KG25.entity.Manager;

@Service
public class ManagerService {
	@Autowired
	ManagerDAO dao;

	// 상세보기
	public Manager managerInfo(String id) {
		return dao.managerInfo(id);
	}

	// 로그인
	public Manager login(String id, String pw) {
		return dao.login(id, pw);
	}

	// id가 존재하는 지 검사
	// 존재하면 true 리턴, 존재하지 않으면 false 리턴
	public boolean isExistId(String id) {
		return dao.isExistId(id);
	}

	// 회원가입
	public Manager write(ManagerDTO dto) {
		return dao.write(dto);
	}

	// 아이디와 전화번호 조회 : findId(아이디 찾기)
	public Manager findId(String name, String tel) {
		return dao.findId(name, tel);
	}

	// 아이디,이름,전화번호 조회 : findPW(비밀번호 찾기)
	public Manager findPW(String id, String name, String tel) {
		return dao.findPW(id, name, tel);
	}

	// 비밀번호 찾기(비밀번호 수정)
	public int changePw(String id, String pw) {
		return dao.changePw(id, pw);
	}

	// 정보 변경
	public int updateInfo(ManagerDTO dto) {
		return dao.updateInfo(dto);
	}

	// 회원 탈퇴
	public int delete(String id, String pw) {
		return dao.delete(id, pw);
	}
}
