package com.example.KG25.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.KG25.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, String>{
		// 아이디와 비밀번호 조회 : login
		Manager findByIdAndPw(String id, String pw);
		
		// 이름,전화번호 조회 : findId(아이디 찾기)
		Manager findByNameAndTel(String name, String tel);
		
		// 이름,전화번호,아이디 조회 : findPw(비밀번호 찾기)
		Manager findByIdAndNameAndTel(String id,String name, String tel);
		
		// 비밀번호, 전화번호 조회 : checkPw(개인정보 변경을 위해 확인용도)
		Manager findByPwAndTel(String pw, String tel);
}
