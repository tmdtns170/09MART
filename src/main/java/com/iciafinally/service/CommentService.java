package com.iciafinally.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Comments;
import com.iciafinally.domain.Manager;
import com.iciafinally.domain.Member;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.Seller;
import com.iciafinally.repository.CommentRepository;
import com.iciafinally.repository.ManagerRepository;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.repository.ProductRepository;
import com.iciafinally.repository.SellerRepository;


@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private SellerRepository sellerRepository;
	
	//물품 리스트 출력
	public List<Product> findAll() {
		return productRepository.findAll();
		
	}

	//  댓글 목록조회 기능
	public List<Comments> CommentView(Long product_id) {

		return commentRepository.findByProductId(product_id);

	}

	
	 //댓글 등록기능
	public void registComment(String recontents, Long seller_id, Long product_id, Long member_id, Long manager_id) {

		Product product = productRepository.findById(product_id).orElse(null);

		Seller seller = sellerRepository.findById(seller_id).orElse(null);

		Manager manager = managerRepository.findById(manager_id).orElse(null);

		Member member = memberRepository.findById(member_id).orElse(null);

		if (product != null && (member != null || manager != null || seller != null)) {

			Comments comments = Comments.createComment(recontents, member, product, manager, seller);

			commentRepository.save(comments);

		}
	}

}