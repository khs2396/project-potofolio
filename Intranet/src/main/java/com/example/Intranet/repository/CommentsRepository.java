package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Intranet.entity.Comments;
import com.example.Intranet.entity.Iboard;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
	@Query("select c from Comments c where c.board = :board order by c.commentSeq desc")
    List<Comments> findByBoardOrderByCommentSeqDesc(@Param("board") Iboard board);

	@Query(value = "select * from COMMENTS where BOARD_SEQ = :boardSeq order by COMMENT_SEQ desc",
			countQuery = "select count(*) from COMMENTS where BOARD_SEQ = :boardSeq",
			nativeQuery = true)
	Page<Comments> findByBoardOrderByCommentSeqDesc(@Param("boardSeq") int boardSeq, Pageable pageable);
}
