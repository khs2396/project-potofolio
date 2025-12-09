package com.example.Intranet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Intranet.entity.EmailVerification;
import com.example.Intranet.repository.EmailVerificationRepository;

/**
 * 이메일 인증 코드 관리 서비스
 * email_verifications 테이블에 코드/만료/인증 시각을 관리합니다.
 */
@Service
public class EmailVerificationService {
	private static final Duration DEFAULT_EXPIRE = Duration.ofMinutes(10);
	private final Random random = new Random();

	@Autowired
	private EmailVerificationRepository repository;

	public String generateCode() {
		int code = 100000 + random.nextInt(900000); // 6자리
		return String.valueOf(code);
	}

	@Transactional
	public String issueCode(String emailRaw) {
		String email = normalize(emailRaw);
		String code = generateCode();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expires = now.plus(DEFAULT_EXPIRE);
		EmailVerification ev = repository.findById(email).orElse(new EmailVerification());
		ev.setEmail(email);
		ev.setCode(code);
		ev.setExpiresAt(expires);
		ev.setVerifiedAt(null);
		ev.setCreatedAt(ev.getCreatedAt() == null ? now : ev.getCreatedAt());
		repository.save(ev);
		return code;
	}

	@Transactional
	public boolean verify(String emailRaw, String code) {
		String email = normalize(emailRaw);
		return repository.findById(email)
				.map(ev -> {
					if (ev.getExpiresAt() == null || LocalDateTime.now().isAfter(ev.getExpiresAt())) {
						repository.delete(ev);
						return false;
					}
					boolean ok = ev.getCode() != null && ev.getCode().equals(code);
					if (ok) {
						ev.setVerifiedAt(LocalDateTime.now());
						repository.save(ev);
					}
					return ok;
				})
				.orElse(false);
	}

	private String normalize(String email) {
		return email == null ? "" : email.trim().toLowerCase();
	}
}
