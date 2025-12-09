package com.example.KG25.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.KG25.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String>{
	@Query(value="select * from "
			+ "(select rownum rn, tt.* from "
			+ "(select * from product order by pdname desc) tt) "
			+ "where rn>=:startNum and rn<=:endNum", nativeQuery = true)
	List<Product> findByStartNumAndEndNum(@Param("startNum")int startNum,
			@Param("endNum")int endNum);
	
	List<Product> findByCat(String cat);
	
	@Query(value="select count(*) from product where cat = :cat", nativeQuery = true)
	int getCountByCat(@Param("cat") String cat);
	
	@Query(value="select * from "
		    + "(select rownum rn, tt.* from "
		    + " (select * from product where cat = :cat order by pdname desc) tt) "
		    + "where rn >= :startNum and rn <= :endNum", nativeQuery = true)
	List<Product> findByCatWithPaging(@Param("cat") String cat, 
			@Param("startNum")int startNum, @Param("endNum")int endNum
			);
}
