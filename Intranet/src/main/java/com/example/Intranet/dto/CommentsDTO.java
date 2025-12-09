package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Comments;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Iboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsDTO {
    private int commentSeq;
    private int boardSeq;
    private int empNo;
    private String content;
    private Date logtime;
    
 // 엔티티 변환
    // 실제 Iboard, Employee 객체를 Service/DAO에서 전달받아 연결
    public Comments toEntity(Iboard board, Employee employee) {
        Comments comment = new Comments();
        comment.setCommentSeq(this.commentSeq);
        comment.setBoard(board);        // 게시글 연결
        comment.setEmployee(employee);  // 작성자 연결
        comment.setContent(this.content);
        comment.setLogtime(this.logtime);

        return comment;
    }
}
