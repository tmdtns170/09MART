package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Comments;

public interface CommentRepository extends JpaRepository<Comments, Long> {

	List<Comments> findByProductId(Long product_id);

}