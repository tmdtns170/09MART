package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.BookMark;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {

	List<BookMark> findByMemberId(Long memberId);

	List<BookMark> findByProductId(Long productId);

	boolean existsByMemberIdAndProductId(Long memberId, Long productId);

	void deleteByMemberIdAndProductId(Long memberId, Long productId);

	
}