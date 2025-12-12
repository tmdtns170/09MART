package com.iciafinally.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iciafinally.domain.NodeCost;
import com.iciafinally.domain.NodeCostParam;
import com.iciafinally.repository.NodeCostRepository;



@Transactional(readOnly = true)
@Service
public class NodeCostService  {

    @Autowired
    private NodeCostRepository nodeCostRepository;
    
    public NodeCost getOneByParam(NodeCostParam nodeCostParam) {
        return nodeCostRepository.findByStartNodeIdAndEndNodeId(nodeCostParam.getStartNodeId(),nodeCostParam.getEndNodeId());
    }

    @Transactional
    public void add(NodeCost nodeCost) {
    	nodeCostRepository.save(nodeCost);
    }
}