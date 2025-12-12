package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iciafinally.domain.Member;

public interface MemberRepository extends JpaRepository<Member ,Long>{
/*SELECT * FROM MEMBER WHERE MID = ?*/
	Member findByMid(String mid);
	/*SELECT * FROM MEMBER WHERE MID = ? AND MPW=?*/
	Member findByMidAndMpw(String mid,String mpw);
	/*SELECT * FROM MEMBER WHERE MNAME = ?*/
	Member findBymname(String mname);
	/*SELECT * FROM MEMBER WHERE MPW = ?*/
	Member findByMpw(String mpw);
	
	@Query("SELECT TO_CHAR(joindate,'YYYY-MM'),COUNT(*) "
			+ "FROM Member "
			+ "GROUP BY TO_CHAR(joindate,'YYYY-MM')")
	List<Object[]> findmemberchart();
	
}