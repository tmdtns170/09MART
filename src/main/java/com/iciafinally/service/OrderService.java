package com.iciafinally.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.mapline.util.KakaoApiUtil;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.repository.OrderRepository;
import com.iciafinally.repository.ProductBoardRepository;
import com.iciafinally.repository.SellerRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderrepository;

	@Autowired
	private SellerRepository sellerRepository;
	
	
	@Autowired
	private ProductBoardRepository productBoardRepository;

	@Autowired
	private KakaoApiUtil kakaoapiutil;
	@Autowired
	private DriverService driveService;
	@Autowired
	private MemberRepository memberRepository;

	public void saveOrder(Orders orderlist, Member member, Product product) {
		LocalDateTime now = LocalDateTime.now();
		orderlist.setOrderdate(now);
		orderlist.setMember(member);
		orderlist.setProduct(product);
		orderrepository.save(orderlist);
	}

	public List<Orders> findAll() {

		return orderrepository.findAll();
	}

	public Orders findById(Long orderid) {

		return orderrepository.findById(orderid).orElse(null);
	}

	public Orders save(Orders orders) {
		return orderrepository.save(orders);

	}

	public double findSea(Long id) {
		List<Orders> orders = orderrepository.findByProductId(id);
		double count = 0;
		for (Orders od : orders) {
			count += od.getBea();
		}
		return count;

	}

	public void saveOrder(Orders orders) {
		orderrepository.save(orders);
	}

	public List<Orders> findByMember(Long id) {
		
		return orderrepository.findByMemberId(id);
	}

	public List<ProductBoard> findBestBea() {
		List<Object[]> results = orderrepository.findBestBea();
		List<ProductBoard> bestProductList = new ArrayList<>();
		for(Object[] result : results ) {
			
			Long productId = (Long)result[0];
			ProductBoard productBoard =	productBoardRepository.findByProductId(productId); 
			//double bea = (double)result[1];
			bestProductList.add(productBoard);
		}
			
		return bestProductList;
	}
	public List<Orders> findbyseller(Long id) {
		
		return orderrepository.findbyseller(id);
	}

	public List<Orders> findbymember(Long id) {
		return orderrepository.findbymember(id);
	}

	

}