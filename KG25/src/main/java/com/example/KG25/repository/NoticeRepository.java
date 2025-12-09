package com.example.KG25.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.KG25.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer>{
	@Query(value="select * from "
			+ "(select rownum rn, tt.* from "
			+ "(select * from notice order by seq desc) tt) "
			+ "where rn>=:startNum and rn<=:endNum", nativeQuery = true)
	List<Notice> findByStartNumAndEndNum(@Param("startNum")int startNum, 
			@Param("endNum")int endNum);
}
