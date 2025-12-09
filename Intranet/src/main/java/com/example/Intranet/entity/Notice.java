package com.example.Intranet.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTICE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
	@Id
	@Column(name = "NO_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICE_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "NOTICE_SEQUENCE_GENERATOR", sequenceName = "no_seq", initialValue = 1, allocationSize = 1)
	private int seq;

	// 작성자(emp_no)
	@Column(name = "EMP_NO")
	private int depno;

	@Transient
	private String name;

	@Transient
	private String email;

	@Column(name = "SUBJECT")
	private String subject;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "HIT")
	private int hit;

	@Temporal(TemporalType.DATE)
	@Column(name = "LOGTIME")
	private Date logtime;
}
