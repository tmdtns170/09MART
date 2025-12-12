package com.iciafinally.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Seller;

public interface SellerRepository extends JpaRepository<Seller ,Long>{

	Seller findBySid(String sid);

	Seller findBySidAndSpw(String sid, String spw);

}