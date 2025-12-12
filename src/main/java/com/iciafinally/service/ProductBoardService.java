package com.iciafinally.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.repository.OrderRepository;
import com.iciafinally.repository.ProductBoardRepository;

@Service
public class ProductBoardService {

	@Autowired
	private ProductBoardRepository productBoardRepository; // JPA 레포지토리
	@Autowired
	private OrderRepository orderRepository;

	public ProductBoard findById(Long id) {
		return productBoardRepository.findById(id).orElse(null); // ID로 게시판 찾기
	}

	public void save(ProductBoard productBoard) {
		productBoardRepository.save(productBoard); // 변경사항 저장
	}

	public List<Member> getBuyersByProductBoardId(Long productBoardId) {
		List<Member> members = new ArrayList<>();
		// ProductBoard를 통해 Product를 찾기
		ProductBoard productBoard = productBoardRepository.findById(productBoardId).orElse(null);
		if (productBoard != null) {
			Product product = productBoard.getProduct(); // ProductBoard에서 Product 조회
			if (product != null) {
				// 해당 Product에 대한 Orders를 찾기
				List<Orders> orders = orderRepository.findByProductId(product.getId());
				for (Orders order : orders) {
					members.add(order.getMember()); // 주문에서 Member 추가
				}
			}
		}
		return members;
	}
	public Object findmyview(Long id) {
        // 특정 ID의 ProductBoard를 가져옴
        return productBoardRepository.findmyview(id);
                
    }

	public  List<ProductBoard> findMember(Long id) {
		return productBoardRepository.findMember(id);
	}
}
