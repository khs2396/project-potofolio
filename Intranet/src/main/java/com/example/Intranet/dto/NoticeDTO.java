package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Notice;

import lombok.Data;

@Data
public class NoticeDTO {
    private int seq;
    private int depno;
    private String name;
    private String email;
    private String subject;
    private String content;
    private int hit;
    private Date logtime;
    
    public Notice toEntity() {
    	return new Notice(seq, depno, name, email, subject, content, hit, logtime);
    }
}
