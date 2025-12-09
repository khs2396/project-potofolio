package com.example.KG25.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.KG25.entity.Orderlist;
import com.example.KG25.entity.OrderlistId;

public interface OrderListRepository extends JpaRepository<Orderlist, OrderlistId> {
	@Query(value = "select * from " + "(select rownum rn, tt.* from "
			+ "(select * from orderlist order by pdname desc) tt) "
			+ "where rn>=:startNum and rn<=:endNum", nativeQuery = true)
	List<Orderlist> findByStartNumAndEndNum(@Param("startNum") int startNum, @Param("endNum") int endNum);

	// 아이디, 상품으로 기존 장바구니 항목 찾기
	Orderlist findByIdAndPdname(String id, String pdname);

	// 아이디, 상품명으로 존재하는지 확인
	boolean existsByIdAndPdname(String id, String pdname);

	// id별 장바니 목록 리스트 (startNum, endNum 포함)
	@Query(value = "SELECT * FROM " + "(SELECT rownum rn, tt.* FROM "
			+ "(SELECT * FROM orderlist WHERE id = :id ORDER BY pdname DESC) tt)"
			+ " WHERE rn >= :startNum AND rn <= :endNum", nativeQuery = true)
	List<Orderlist> findByIdWithPaging(@Param("id") String id, @Param("startNum") int startNum,
			@Param("endNum") int endNum);

	// id별 전체 개수 조회용 (id별)
	int countById(String id);

	@Query("SELECT SUM(o.total) FROM Orderlist o WHERE o.id = :id")
	Integer sumTotalById(@Param("id") String id);

	// 장바구니에서 특정 상품명을 가진 모든 데이터 조회
	List<Orderlist> findAllByPdname(String pdname);

	List<Orderlist> findAllById(String id);

	@Modifying
	@Query("DELETE FROM Orderlist o WHERE o.id = :id")
	int deleteAllById(@Param("id") String id);
}
