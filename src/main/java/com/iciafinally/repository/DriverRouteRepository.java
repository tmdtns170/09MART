package com.iciafinally.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.Driver;
import com.iciafinally.domain.DriverRoute;

public interface DriverRouteRepository extends JpaRepository<DriverRoute ,Long>{
	@Query("SELECT dr "
			+ "FROM DriverRoute dr "
			+ "WHERE dr.drid = :did AND dr.rtdate >= :tdate")
	List<DriverRoute> findByDridtoday(@Param("did")String Did, @Param("tdate")LocalDate today);

	
	
	@Query("SELECT dr "
			+ "FROM DriverRoute dr "
			+ "WHERE dr.drid = :did AND dr.rtdate < :tdate")
	List<DriverRoute> RoutefindByIdDate(@Param("did")String Did, @Param("tdate")LocalDate today);



	DriverRoute findByDridAndRtdate(String drid, LocalDate rtdate);



	List<DriverRoute> findByDrid(String drid);

	
	
	
}
