package com.iciafinally.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.controller.SellerChart;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.Seller;
import com.iciafinally.repository.OrderRepository;
import com.iciafinally.repository.ProductRepository;
import com.iciafinally.repository.SellerRepository;

@Service
public class SellerService {

	@Autowired 
	private SellerRepository sellerRepository;  
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	
	
	public Seller findBySid(String sid) {

		Seller seller = sellerRepository.findBySid(sid);
		return seller;
	}
	public void registSeller(Seller seller) {
		
		sellerRepository.save(seller);
	}
	
	
	public void saveProduct(Product product) {
		productRepository.save(product);
		
	}
	public Seller findBySidAndSpw(String sid, String spw) {
		
		return sellerRepository.findBySidAndSpw(sid,spw);
	}
	public List<Product> findBySidAndFdate(String sid, LocalDate today) {
		return productRepository.findBySellerIdAndFdate(sellerRepository.findBySid(sid).getId(), today);
	}
	public Seller mypage(String id) {
		
		return sellerRepository.findBySid(id);
	}
	public List<SellerChart> getSellerChartData(Long id) {
		List<SellerChart> SellerChartList = new ArrayList<>();
		List<Object[]> result = orderRepository.findproductchart(id);
		for(Object[] re : result) {
			SellerChart sellerChart = new SellerChart();
			String chartMonth = re[0].toString();
			String product_id = re[1].toString();
			String productname = re[2].toString();
			String totalbea = re[3].toString().replace(".0", "");
			sellerChart.setChartMonth(chartMonth);
			sellerChart.setProduct_id(product_id);
			sellerChart.setProductname(productname);
			sellerChart.setTotalbea(totalbea);
			SellerChartList.add(sellerChart);
		}
		System.out.println(SellerChartList);
		
		return SellerChartList;
	}

}
