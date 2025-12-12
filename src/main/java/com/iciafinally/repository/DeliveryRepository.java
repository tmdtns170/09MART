package com.iciafinally.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Delivery;
import com.iciafinally.domain.Driver;

public interface DeliveryRepository extends JpaRepository<Delivery ,Long> {
	List<Delivery> findByDriverIdOrderByDdate(Long id);
	
	List<Delivery> findByDriverAndDdate(Driver driver, LocalDate ddate);
}