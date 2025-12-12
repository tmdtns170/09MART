package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {

	

	

	List<Orders> findByProductId(Long id);

	List<Orders> findByMemberId(Long id);
	
	@Query("SELECT OD.product.id, SUM(OD.bea) AS bea "
			+ "FROM Orders OD "
			+ "INNER JOIN Product PR ON OD.product.id = PR.id "
			+ "GROUP BY OD.product.id "
			+ "ORDER BY bea DESC ")
	List<Object[]> findBestBea();

	@Query("SELECT o "
			+ "FROM Orders o "
			+ "WHERE o.product.seller.id = :sellerId ORDER BY o.orderdate DESC")
	List<Orders> findbyseller(@Param("sellerId") Long id);
	
	@Query("SELECT TO_CHAR(od.orderdate, 'YYYY-MM'), od.product.id, od.product.productname, SUM(od.bea) "
		      + " FROM Orders od "
		      + " WHERE od.product.id IN (SELECT p.id FROM Product p WHERE p.seller.id = :id) "
		      + " GROUP BY TO_CHAR(od.orderdate, 'YYYY-MM'), od.product.id, od.product.productname")
	List<Object[]> findproductchart(@Param("id")Long id);
	@Query("SELECT o "
			+ "FROM Orders o "
			+ "WHERE o.member.id = :memberid ORDER BY o.orderdate DESC")
	List<Orders> findbymember(@Param("memberid")Long id);
	
	/*
	 * 	SELECT B.*, M.*
	 *  FROM BOARD B
	 *    INNER JOIN MEMBER M
	 *    ON B.MEMBER_ID = M.ID
	 *  WHERE M.MID = ? 
	 * */
	
}









