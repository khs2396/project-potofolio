package com.example.Intranet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMAIL_VERIFICATIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {
	@Id
	@Column(name = "EMAIL")
	private String email;

	@Column(name = "CODE")
	private String code;

	@Column(name = "EXPIRES_AT")
	private LocalDateTime expiresAt;

	@Column(name = "VERIFIED_AT")
	private LocalDateTime verifiedAt;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
}
