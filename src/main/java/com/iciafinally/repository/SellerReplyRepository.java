package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iciafinally.domain.Sellerreply;

public interface SellerReplyRepository extends JpaRepository<Sellerreply, Long> {
	
	
	@Query("SELECT srp "
			+ "FROM Sellerreply srp "
			+ "WHERE srp.reply.id IN ( SELECT rp.id "
			+ "                              FROM Reply rp "
			+ "                              WHERE rp.productBoard.id = (SELECT pb.id "
			+ "                                                                           FROM ProductBoard pb "
			+ "                                                                           WHERE pb.id = :pdb_id))")
	List<Sellerreply> findByProductBoard(@Param("pdb_id") Long pdb_id);
	@Query("SELECT srp FROM "
			+ "Sellerreply srp "
			+ "WHERE srp.reply.id IN (SELECT rp.id "
			+ "                             FROM Reply rp "
			+ "                             WHERE rp.member.id = :id )")
	List<Sellerreply> findByMemberId(@Param("id") Long id);

	

}