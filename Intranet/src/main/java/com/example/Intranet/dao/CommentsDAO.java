package com.example.Intranet.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.Intranet.dto.CommentsDTO;
import com.example.Intranet.entity.Comments;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Iboard;
import com.example.Intranet.repository.CommentsRepository;
import com.example.Intranet.repository.EmployeeRepository;
import com.example.Intranet.repository.IboardRepository;

@Repository
public class CommentsDAO {
	
	@Autowired
	CommentsRepository commentsRepository;
	
    @Autowired
    private IboardRepository iboardRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
	
	// 댓글 저장
	public Comments commentWrite(CommentsDTO dto) {
		// 게시글 조회
        Iboard board = iboardRepository.findById(dto.getBoardSeq())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 작성자 조회
        Employee emp = employeeRepository.findById(dto.getEmpNo())
                .orElseThrow(() -> new RuntimeException("작성자 없음"));

        // DTO -> Entity 변환 (실제 객체 연결)
        Comments comment = dto.toEntity(board, emp);

        return commentsRepository.save(comment);
	}
	
	// 댓글 수정
	public int commentModify(CommentsDTO dto) {
		int result = 0;
		Comments comment = commentsRepository.findById(dto.getCommentSeq()).orElse(null);
		if(comment != null) {
			comment.setContent(dto.getContent());
			comment.setLogtime(new Date()); // 수정 시간 반영
			Comments comment_result = commentsRepository.save(comment);
			if(comment_result != null) result = 1;
		}
		return result;
	}
	
    // 게시글에 달린 댓글 전체 조회 (최신 순)
    public List<Comments> findCommentsByBoard(Iboard board) {
        return commentsRepository.findByBoardOrderByCommentSeqDesc(board);
    }

    // 게시글에 달린 댓글 페이징 조회
    public Page<Comments> findCommentsByBoard(Iboard board, Pageable pageable) {
        int boardSeq = board.getBoardseq();
        return commentsRepository.findByBoardOrderByCommentSeqDesc(boardSeq, pageable);
    }
	
	// 댓글 삭제
	public int commentDelete(int commentSeq) {
		int result = 0;
		Comments comment = commentsRepository.findById(commentSeq).orElse(null);
		if(comment != null) {
			commentsRepository.delete(comment);
			if(!commentsRepository.existsById(commentSeq)) result = 1;
		}
		return result;
	}
	
	public Comments findById(int commentSeq) {
		return commentsRepository.findById(commentSeq).orElse(null);
	}	
}
