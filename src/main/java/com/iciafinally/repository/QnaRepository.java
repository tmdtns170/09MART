package com.iciafinally.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iciafinally.domain.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {

	/*
	 * Optional<Qna> findById(Long id);
	 */

	List<Qna> findByMemberId(Long memberId);

	@Query("SELECT q "
			+ "FROM Qna q "
			+ "order by q.id desc")
	List<Qna> findQnaList();

}