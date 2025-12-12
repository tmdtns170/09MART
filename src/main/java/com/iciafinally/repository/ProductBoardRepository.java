package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.ProductBoard;

public interface ProductBoardRepository extends JpaRepository<ProductBoard ,Long>{

	
	@Query("SELECT pb "
			+ "FROM ProductBoard pb "
			+ "WHERE pb.product.id IN (SELECT p.id "
			+ "                      FROM Product p "
			+ "                      WHERE seller.id = :id)")
	List<ProductBoard> findByMyBoard(@Param("id")Long id);
	@Query("SELECT pb "
			+ "FROM ProductBoard pb "
			+ "WHERE pb.pbstate = 'Y' "
			+"order by pb.pdate desc")
	List<ProductBoard> findByPbstate();
	
	@Query("SELECT pb "
			+ "FROM ProductBoard pb "
			+ "WHERE pb.pbstate = 'Y' "
			+"order by pb.phits desc")
	List<ProductBoard> findByOrderByPhits();
	
	@Query("SELECT pb FROM ProductBoard pb WHERE pb.pbstate = 'Y' ORDER BY pb.pdate ASC")
	List<ProductBoard> findByPbstateAsc();
	@Query("SELECT pb FROM ProductBoard pb WHERE pb.pbstate = 'Y' ORDER BY pb.pdate DESC")
	List<ProductBoard> findByPbstateDesc();
	
	
	
	@Query("SELECT pb "
			+ "FROM ProductBoard pb "
			+ "WHERE pb.ptitle LIKE concat('%', :search, '%')")
	List<ProductBoard> findByPtitle(@Param("search") String search);
	ProductBoard findByProductId(Long id);
	
	
	
	@Query("select SUM(PB.phits)"
			+ " FROM ProductBoard PB"
			+ " WHERE PB.product.id IN (SELECT p.id"
			+ " FROM Product p"
			+ " WHERE p.seller.id= :id)"
			+ " AND PB.pbstate='Y'")
	Object findmyview(@Param("id")Long id);
	
	
	
	
	@Query("SELECT pb "
			+ "FROM ProductBoard pb "
			+ "WHERE pb.product.id IN (SELECT o.product.id "
			+ "FROM Orders o "
			+ "WHERE o.member.id = :id ) ")
	List<ProductBoard> findMember(@Param("id")Long id);
	


	

}