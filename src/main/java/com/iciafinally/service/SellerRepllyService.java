package com.iciafinally.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Reply;
import com.iciafinally.domain.Seller;
import com.iciafinally.domain.Sellerreply;
import com.iciafinally.repository.ReplyRepository;
import com.iciafinally.repository.SellerReplyRepository;
import com.iciafinally.repository.SellerRepository;

@Service
public class SellerRepllyService {
	@Autowired
	private ReplyRepository replyRepository; 
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private SellerReplyRepository sellerReplyRepository;
	
	
	public void saveSrp(Object loginSid, Sellerreply srp) {
		Reply reply = replyRepository.findById(srp.getRep_id()).orElse(null);
		String sid = (String)loginSid;
		Seller seller = sellerRepository.findBySid(sid);
		srp.setReply(reply);
		srp.setSeller(seller);
		srp.setSredate(LocalDateTime.now());
		sellerReplyRepository.save(srp);
	}


	public List<Sellerreply> findAll() {

		return sellerReplyRepository.findAll();
	}


	public List<Sellerreply> findByProductBoard(Long pdb_id) {
		
		return sellerReplyRepository.findByProductBoard(pdb_id);
	}


	public void save(Sellerreply srp) {
		sellerReplyRepository.save(srp);
		
	}


	public List<Sellerreply> findByMemberId(Long id) {
		
		return sellerReplyRepository.findByMemberId(id);
	}

}
