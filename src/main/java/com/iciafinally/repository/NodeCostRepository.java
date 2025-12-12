package com.iciafinally.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iciafinally.domain.NodeCost;



public interface NodeCostRepository extends JpaRepository<NodeCost, Long>{
	 NodeCost findByStartNodeIdAndEndNodeId(Long startNodeId, Long endNodeId);
}