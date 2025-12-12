package com.iciafinally.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iciafinally.domain.Pagevisit;

public interface PagevisitRepository extends JpaRepository<Pagevisit ,LocalDate> {
	
	public Pagevisit findByVisitday(LocalDate today);
	
	
	@Query("SELECT TO_CHAR(p.visitday, 'YYYY-MM'),SUM(p.visitea) "
			+ "FROM Pagevisit p "
			+ "GROUP BY TO_CHAR(p.visitday, 'YYYY-MM')")
	public List<Object[]> findBydate();

	
	
}