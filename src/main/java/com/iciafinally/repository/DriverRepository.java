package com.iciafinally.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
	/* SELECT * FROM MEMBER WHERE MID = ? */
	Driver findByDid(String did);

	/* SELECT * FROM MEMBER WHERE MID = ? AND MPW=? */
	Driver findByDidAndDpw(String did, String dpw);

	/* SELECT * FROM MEMBER WHERE MNAME = ? */
	Driver findBydname(String dname);

	/* SELECT * FROM MEMBER WHERE MPW = ? */
	Driver findByDpw(String dpw);

	@Query("SELECT d FROM Driver d " + "WHERE d.id NOT IN (SELECT dl.driver.id " + "FROM Delivery dl "
			+ "WHERE dl.ddate = :sdate)"
			+ "ORDER BY d.id ASC")
	List<Driver> findByWaitDriverOrderById(@Param("sdate") LocalDate sdate);

	@Query("SELECT d FROM Driver d " + "WHERE d.id  IN (SELECT dl.driver.id " 
	+ "FROM Delivery dl "
			+ "WHERE dl.ddate = :sdate)"
			+ "ORDER BY d.id ASC")

	List<Driver> findByNoWaitDriverOrderById(@Param("sdate") LocalDate sdate);

}