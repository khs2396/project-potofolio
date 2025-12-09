package com.example.KG25.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.KG25.dto.ManagerDTO;
import com.example.KG25.entity.Manager;
import com.example.KG25.entity.Ordering;
import com.example.KG25.service.ManagerService;
import com.example.KG25.service.OrderingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ManagerController {

	@Autowired
	ManagerService service;
	@Autowired
	OrderingService service2;

	@GetMapping("loginForm")
	public String loginForm() {
		return "/manager/loginForm";
	}

	// 로그인
	@PostMapping("/login")
	public String login(ManagerDTO dto, HttpSession session) {
		// 1.데이터 처리

		// 전달 데이터 확인용
		System.out.println("dto = " + dto);

		// id와 pwd를 조회하여 데이터 가져오기
		Manager manager = service.login(dto.getId(), dto.getPw());
		// 2.데이터 공유

		// 3.view 파일명 리턴
		if (manager != null) {
			// 세션에 아이디와 이름 저장
			session.setAttribute("managerId", manager.getId());
			session.setAttribute("managerName", manager.getName());
			int code = manager.getCode();
			session.setAttribute("code", code);
			// 세션에 저장된 이름 확인용
			System.out.println("managerName = " + manager.getName());
			return "/manager/loginOk";
		}
		return "/manager/loginFail";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		// 1.데이터 처리
		// 세션 제거
		session.removeAttribute("managerId");
		session.removeAttribute("managerName");
		// 2.데이터 공유
		// 3.view 파일명 리턴
		return "/manager/logout";
	}

	// 회원가입 폼
	@GetMapping("/managerWriteForm")
	public String managerWriteForm() {
		// 1.데이터 처리
		// 2.데이터 공유
		// 3.view 파일명 리턴
		return "/manager/managerWriteForm";
	}

	// 회원가입
	@PostMapping("/managerWrite")
	public String managerWrite(ManagerDTO dto, Model model) {
		// 1.데이터 처리
		// db
		dto.setCode(0);
		Manager manager = service.write(dto);
		String result = "";
		if (manager != null) {
			result = "회원가입 완료!";
		} else {
			result = "회원가입이 완료되지 않았습니다. 다시 시도해주세요";
		}

		// 2.데이터 공유
		model.addAttribute("result", result);
		// 3.view 파일명 리턴
		return "/manager/managerWrite";
	}

	
    // 아이디 중복 체크 API
	@GetMapping("/checkId")
	@ResponseBody
	public boolean checkId(@RequestParam("id") String id) {
	    boolean exists = service.isExistId(id);
	    System.out.println("checkId called with id=" + id + ", exists=" + exists);  // 로그 출력
	    return exists;
	}


	// 아이디 찾기 폼
	@GetMapping("/findIdForm")
	public String findIdForm() {
		// 1.데이터 처리
		// 2.데이터 공유
		// 3.view 파일명 리턴
		return "manager/findIdForm";
	}

	// 아이디 찾기
	@PostMapping("/findId")
	public String findId(HttpServletRequest request, Model model) {
		// 1.데이터 처리
		String name = request.getParameter("name");
		String tel = request.getParameter("tel");

		String result = "";

		Manager manager = service.findId(name, tel);
		if (manager != null) {
			result = "아이디 : ";
			model.addAttribute("managerId", manager.getId());
		} else {
			result = "일치하는 ID가 없습니다. 다시 확인해주세요.";
			model.addAttribute("managerId", "");
		}

		// 2.데이터 공유
		model.addAttribute("result", result);

		// 3.view 파일명 리턴
		return "manager/findId";
	}

	// 비밀번호 찾기 폼
	@GetMapping("/findPwForm")
	public String findPwForm() {
		// 1.데이터 처리
		// 2.데이터 공유
		// 3.view 파일명 리턴
		return "/manager/findPwform";
	}

	// 비밀번호 찾기
	@PostMapping("/findPw")
	public String findPw(HttpServletRequest request, Model model) {
		// 1.데이터 처리
		String name = request.getParameter("name");
		String tel = request.getParameter("tel");
		String id = request.getParameter("id");

		// db , 데이터 공유, view 파일명 리턴
		Manager manager = service.findPW(id, name, tel);
		if (manager != null) {
			model.addAttribute("id", id);
			return "/manager/changePwForm";
		} else {
			return "/manager/findPwFail";
		}
	}

	@GetMapping("changePwForm")
	public String changePwForm(HttpSession session, Model model) {
		String id = (String) session.getAttribute("managerId");
		Manager manager = service.managerInfo(id);
		String pw = manager.getPw();

		model.addAttribute("id", id);
		model.addAttribute("pw", pw);

		return "manager/changePwForm";
	}

	// 비밀번호 변경
	@PostMapping("/changePw")
	public String changePw(HttpServletRequest request, Model model, HttpSession session) {
		// 1.데이터 처리
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");

		// db
		int result = service.changePw(id, pw);
		
		// 정보변경시 자동 로그아웃
		if(result > 0) {
			session.removeAttribute("managerId");
			session.removeAttribute("managerName");
		}

		// 2.데이터 공유
		model.addAttribute("result", result);
		// 3.view 파일명 리턴
		return "/manager/changePwOk";
	}

	// 정보변경을 위한 비밀번호, 전화번호 확인 폼
	@GetMapping("/checkPwAndTelForm")
	public String checkPwAndTelForm(HttpSession session, Model model) {
		// 1.데이터 처리
		String id = (String) session.getAttribute("managerId");
		// 2.데이터 공유
		model.addAttribute("id", id);
		// 3.view 파일명 리턴
		return "/manager/checkPwAndTelForm";
	}

	// 정보변경을 위한 비밀번호, 전화번호 확인
	@PostMapping("/checkPwAndTel")
	public String checkPwAndTel(HttpSession session, HttpServletRequest request, Model model) {
		String id = (String) session.getAttribute("managerId");
		String pw = request.getParameter("pw");
		String tel = request.getParameter("tel");

		// 로그인 확인 (비밀번호 확인)
		Manager manager = service.login(id, pw);

		// 전화번호도 직접 비교
		if (manager != null && manager.getTel().replaceAll("-", "").equals(tel)) {
			// 성공 시 부모창 이동 및 팝업 닫기
			model.addAttribute("manager", manager);
			return "/manager/closePopupAndRedirect";
		} else {
			// 실패한 경우 다시 폼 보여주고, 에러 메시지 전달
			model.addAttribute("id", id);
			model.addAttribute("error", "정보가 올바르지 않습니다. 다시 입력해주세요.");
			return "/manager/checkPwAndTelForm";  // ✔️ 폼을 다시 보여줌
		}
	}

	// 매니저 정보변경 폼
	@PostMapping("/managerModifyForm")
	public String managerModifyForm(HttpServletRequest request, Model model) {
		// 1.데이터 처리
		String id = request.getParameter("id");

		// db
		Manager manager = service.managerInfo(id);
		// 2.데이터 공유
		model.addAttribute("manager", manager);
		// 3.view 파일명 리턴
		return "/manager/managerModifyForm";
	}

	// 매니저 정보변경
	   @PostMapping("/managerModify")
	   public String managerModify(HttpSession session, ManagerDTO dto, Model model) {
	      // 1.데이터 처리
	      int code = (int) session.getAttribute("code");
	      dto.setCode(code);
	      // db
	      int result = service.updateInfo(dto);
	      // 정보변경시 자동 로그아웃
	      if (result > 0) {
	         session.removeAttribute("managerId");
	         session.removeAttribute("managerName");
	      }
	      // 2.데이터 공유
	      model.addAttribute("result", result);
	      // 3.view 파일명 리턴
	      return "/manager/managerModify";
	   }

	@GetMapping("/checkDelete")
	public String checkDelete(HttpSession session, Model model) {
		// 1.데이터 처리
		String id = (String) session.getAttribute("managerId");
		System.out.println("id = " + id);
		// 2.데이터 공유
		model.addAttribute("id", id);
		// 3.view 파일명 리턴
		return "/manager/checkDelete";
	}

	// 매니저 삭제
	@PostMapping("/delete")
	public String delete(HttpServletRequest request, Model model, HttpSession session) {
		// 1.데이터 처리
		String id = (String) session.getAttribute("managerId");
		String pw = request.getParameter("pw");
		System.out.println("삭제 아이디 = " + id);
		System.out.println("삭제 비밀번호 = " + pw);

		// db
		int result = service.delete(id, pw);
		if (result > 0) {
			session.removeAttribute("managerId");
			session.removeAttribute("managerName");
		}
		// 2.데이터 공유
		model.addAttribute("result", result);
		// 3.view 파일명 리턴
		return "/manager/delete";
	}

	@GetMapping("mypage")
	public String mypage(HttpSession session, Model model) {
		String id = (String) session.getAttribute("managerId");
		String name = (String) session.getAttribute("managerName");

		Manager manager = service.managerInfo(id);

		List<Ordering> list = service2.findById(id);
		
		model.addAttribute("id", id);
		model.addAttribute("name", name);
		model.addAttribute("list", list);
		model.addAttribute("manager", manager);

		return "/manager/mypage";
	}

	@GetMapping("checkPwForm")
	public String checkPwForm(@RequestParam("id") String id, 
							@RequestParam("target") String target,
							Model model) {
		// id, target을 모델에 담아 팝업에 전달
		model.addAttribute("id", id);
		model.addAttribute("target", target);

		return "/manager/checkPwForm";
	}

	@PostMapping("/checkPw")
	public String checkPw(@RequestParam("id") String id, 
			@RequestParam("pw") String pw, @RequestParam("target") String target, 
			Model model) {

		Manager manager = service.login(id, pw);

		if (manager != null) {
			// 비밀번호 일치 → 이동할 대상에 따라 분기
			model.addAttribute("manager", manager);

			if ("changePw".equals(target)) {
				return "/manager/closePopupAndRedirectToChangePw";
			} else if ("delete".equals(target)) {
				return "/manager/closePopupAndRedirectToDelete";
			}
		}

		// 비밀번호 불일치 시
		return "/manager/pwFail";
	}
}
