package com.example.Intranet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Intranet.dao.CommentsDAO;
import com.example.Intranet.dto.CommentsDTO;
import com.example.Intranet.entity.Comments;
import com.example.Intranet.entity.Iboard;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommentsService {
	@Autowired
	private CommentsDAO commentsDao;

	// 댓글 작성
	public Comments commentWrite(CommentsDTO dto) {
		return commentsDao.commentWrite(dto);
	}

	// 댓글 수정
	public int commentModify(CommentsDTO dto) {
		return commentsDao.commentModify(dto);
	}

	// 게시글별 댓글 전체 조회 (최신순)
	public List<Comments> findCommentsByBoard(Iboard board) {
		return commentsDao.findCommentsByBoard(board);
	}

	// 게시글별 댓글 페이징 조회
	public Page<Comments> findCommentsByBoard(Iboard board, Pageable pageable) {
		return commentsDao.findCommentsByBoard(board, pageable);
	}

	// 댓글 삭제
	public int commentDelete(int commentseq) {
		return commentsDao.commentDelete(commentseq);
	}

	public Comments findComment(int commentseq) {
		return commentsDao.findById(commentseq);
	}
}
