package com.example.KG25.dto;

import com.example.KG25.entity.Notice;

import lombok.Data;

@Data
public class NoticeDTO {
	
	private int seq;
	private String subject;
	private String content;
	private String id;
	private String img;
	
	public Notice toEntity() {
		return new Notice(seq, subject, content, id, img);
	}
}
