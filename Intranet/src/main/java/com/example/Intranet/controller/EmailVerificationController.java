package com.example.Intranet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Intranet.service.EmailVerificationService;
import com.example.Intranet.service.EmailSenderService;

/**
 * 기본 메일 발송을 포함합니다.
 */
@RestController
public class EmailVerificationController {

	@Autowired
	private EmailVerificationService service;
	@Autowired
	private EmailSenderService mailService;

	@PostMapping("/auth/email-code")
	public Map<String, Object> sendCode(@RequestBody Map<String, String> body) {
		String email = body.getOrDefault("email", "").trim();
		Map<String, Object> res = new HashMap<>();
		if (email.isEmpty()) {
			res.put("rt", "FAIL");
			res.put("message", "이메일을 입력해주세요.");
			return res;
		}
		String code = service.issueCode(email);
		try {
			mailService.sendVerificationEmail(email, code, "signup");
			res.put("rt", "OK");
		} catch (Exception e) {
			res.put("rt", "FAIL");
			res.put("message", "이메일 발송에 실패했습니다: " + e.getMessage());
		}
		return res;
	}

	@PostMapping("/auth/email-verify")
	public Map<String, Object> verifyCode(@RequestBody Map<String, String> body) {
		String email = body.getOrDefault("email", "").trim();
		String code = body.getOrDefault("code", "").trim();
		Map<String, Object> res = new HashMap<>();
		if (email.isEmpty() || code.isEmpty()) {
			res.put("rt", "FAIL");
			res.put("message", "이메일과 인증번호를 입력해주세요.");
			return res;
		}
		boolean ok = service.verify(email, code);
		res.put("rt", ok ? "OK" : "FAIL");
		if (!ok) res.put("message", "인증번호가 올바르지 않거나 만료되었습니다.");
		return res;
	}
}
