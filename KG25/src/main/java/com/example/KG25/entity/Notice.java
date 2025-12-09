package com.example.KG25.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, 
	                generator = "NOTICE_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "NOTICE_SEQUENCE_GENERATOR", 
	                sequenceName = "seq_notice", initialValue = 1, allocationSize = 1)
	private int seq;
	private String subject;
	private String content;
	private String id;
	private String img;
}
