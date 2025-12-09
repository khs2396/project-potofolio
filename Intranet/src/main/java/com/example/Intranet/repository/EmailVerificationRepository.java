package com.example.Intranet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Intranet.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
	Optional<EmailVerification> findByEmail(String email);
}
