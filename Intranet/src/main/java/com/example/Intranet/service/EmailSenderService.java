package com.example.Intranet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 실제 이메일 발송용 간단 서비스.
 * SMTP 설정은 application.properties의 spring.mail.*을 사용합니다.
 */
@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${mail.from.address:noreply@example.com}")
	private String fromAddress;

	@Value("${mail.from.name:NoReply}")
	private String fromName;

	public void sendVerificationEmail(String toEmail, String code, String type) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(String.format("%s <%s>", fromName, fromAddress));
		message.setTo(toEmail);

		if ("signup".equalsIgnoreCase(type)) {
			message.setSubject("[사이트] 회원가입 인증번호");
			message.setText("""
					안녕하세요.

					회원가입 인증번호는 다음과 같습니다.
					인증번호: %s

					인증번호를 입력하시면 가입을 완료할 수 있습니다.
					본인이 요청하지 않았다면 이 메일을 무시해 주세요.
					""".formatted(code));
		} else {
			message.setSubject("[사이트] 인증번호");
			message.setText("인증번호: " + code);
		}

		mailSender.send(message);
	}
}
