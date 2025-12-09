package com.example.KG25.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.KG25.dto.ManagerDTO;
import com.example.KG25.entity.Manager;
import com.example.KG25.repository.ManagerRepository;

@Repository
public class ManagerDAO {
	@Autowired
	ManagerRepository managerRepository;

	// 상세정보
	public Manager managerInfo(String id) {
		return managerRepository.findById(id).orElse(null);
	}

	// 로그인
	public Manager login(String id, String pw) {
		return managerRepository.findByIdAndPw(id, pw);
	}

	// id가 존재하는 지 검사
	// 존재하면 true 리턴, 존재하지 않으면 false 리턴
	public boolean isExistId(String id) {
		boolean result = false;
		boolean isExist = managerRepository.existsById(id);
		if (isExist) {
			result = true;
		}
		return result;
	}

	// 회원가입
	public Manager write(ManagerDTO dto) {
		boolean isExist = managerRepository.existsById(dto.getId());
		if (!isExist) {
			return managerRepository.save(dto.toEntity());
		}
		return null;
	}

	// 이름,전화번호 조회 : findId(아이디 찾기)
	public Manager findId(String id, String tel) {
		return managerRepository.findByNameAndTel(id, tel);
	}

	// 아이디,이름,전화번호 조회 : findPW(비밀번호 찾기)
	public Manager findPW(String id, String name, String tel) {
		return managerRepository.findByIdAndNameAndTel(id, name, tel);
	}

	// 비밀번호 찾기(비밀번호 수정)
	public int changePw(String id, String pw) {
		Manager manager = managerRepository.findById(id).orElse(null);
		int result = 0;
		if (manager != null) {
			manager.setPw(pw);
			Manager manager_result = managerRepository.save(manager);
			if (manager_result != null) {
				result = 1;
			}
		}
		return result;
	}

	// 정보 변경
	public int updateInfo(ManagerDTO dto) {
		Manager manager = managerRepository.findById(dto.getId()).orElse(null);
		int result = 0;
		if (manager != null) {
			Manager manager_result = managerRepository.save(dto.toEntity());
			if (manager_result != null) {
				result = 1;
			}
		}
		return result;
	}

	// findPW(비밀번호 찾기)
	public Manager checkPw(String pw, String tel) {
		return managerRepository.findByPwAndTel(pw, tel);
	}

	// 회원 탈퇴
	public int delete(String id, String pw) {
		Manager manager = managerRepository.findByIdAndPw(id, pw);
		int result = 0;
		if (manager != null) {
			managerRepository.delete(manager);
			if (!managerRepository.existsById(id)) {
				result = 1;
			}
		}
		return result;
	}
}
