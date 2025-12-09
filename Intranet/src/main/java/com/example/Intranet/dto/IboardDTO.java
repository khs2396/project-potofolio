package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Iboard;

import lombok.Data;

@Data
public class IboardDTO {
    private int boardseq;
    private int depno;
    private String subject;
    private String content;
    private int hit;
    private Date logtime;
    
    public Iboard toEntity() {
    	// 새 게시글 작성 시 boardseq가 0이면 시퀀스 자동 생성 (JPA @GeneratedValue)
    	// 수정 시에는 기존 boardseq 값 사용
    	if (boardseq == 0) {
    		// Builder를 사용하여 boardseq를 설정하지 않음 (시퀀스 자동 생성)
    		return Iboard.builder()
    				.depno(depno)
    				.subject(subject)
    				.content(content)
    				.hit(hit)
    				.logtime(logtime)
    				.build();
    	} else {
    		// 수정 시에는 기존 boardseq 사용
    		return new Iboard(boardseq, depno, subject, content, hit, logtime, null);
    	}
    }
}
