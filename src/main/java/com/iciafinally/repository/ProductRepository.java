package com.iciafinally.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.Product;

public interface ProductRepository extends JpaRepository<Product ,Long>{

	List<Product> findBySellerIdAndFdate(Long seller_id, LocalDate today);

	
	
	@Query("SELECT p "
			+ "FROM Product p "
			+ "WHERE p.pstate = 'n' AND p.seller.id = :seller_id")
	Product findProductId(@Param("seller_id") long seller_id);


	@Query("SELECT p "
			+ "FROM Product p "
			+ "WHERE p.productname LIKE concat('%', :search, '%')")
	List<Product> findByProductname(@Param("search") String search);

	

}