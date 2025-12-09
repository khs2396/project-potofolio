package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Intranet.entity.Iboard;


public interface IboardRepository extends JpaRepository<Iboard, Integer>{
	@Query(value="select * from "
			+ "(select rownum rn, tt.* from "
			+ "(select * from Iboard order by board_seq desc) tt) "
			+ "where rn>=:startNum and rn<=:endNum" ,
			nativeQuery = true)
	List<Iboard> findByStartnumAndEndnum(@Param("startNum") int startNum,
										 @Param("endNum") int endNum);
}
