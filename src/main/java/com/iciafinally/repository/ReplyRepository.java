package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

	List<Reply> findByproductBoardIdOrderByRedateDesc(Long pdb_id);

	

}