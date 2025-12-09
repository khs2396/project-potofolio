package com.example.KG25.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.KG25.entity.Ordering;

public interface OrderingRepository extends JpaRepository<Ordering, Integer>{
	@Query(value = "select * from "
	        + "(select rownum rn, tt.* from "
	        + "(select * from ordering order by seq desc) tt) "
	        + "where rn >= :startNum and rn <= :endNum", nativeQuery = true)
	List<Ordering> findAllOrderings(@Param("startNum") int startNum, @Param("endNum") int endNum);

	// 일반 사용자용 - id 조건 포함
	@Query(value = "select * from "
	        + "(select rownum rn, tt.* from "
	        + "(select * from ordering where id = :id order by seq desc) tt) "
	        + "where rn >= :startNum and rn <= :endNum", nativeQuery = true)
	List<Ordering> findByIdAndPaging(@Param("id") String id, @Param("startNum") int startNum, @Param("endNum") int endNum);
	
	Ordering findByPdname(String pdname);
	
	@Query(value = "select count(*) from ordering where id =:id", nativeQuery = true)
	Integer getCountById(@Param("id") String id);
	
	@Query(value = "select * from "
			+ "(select rownum rn, tt.* from "
			+ "(select * from ordering where id = :id order by seq desc) tt) "
			, nativeQuery = true)
	List<Ordering> findById(@Param("id") String id);
}
