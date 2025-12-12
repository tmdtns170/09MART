package com.iciafinally.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iciafinally.controller.ProductForm;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter 
@Setter
@ToString
public class Product {
	@Id
	@GeneratedValue
	private Long id; // 주문번호(pk)
	
	private String comments;// 요청사항
	
	private double weight; //무게 
	
	private int sprice; //물품비 가격
	
	private int sea; // 총 등록갯수
	
	private String startea;//배송시작 개수
	
	private String productname;//배달 물품 이름
	
	private String sloc; //출발 위치
	
	private String pickup = "n";
	
	private String pstate = "n";
	
	private LocalDateTime selldate;// 등록시간
	private LocalDate sdate; //배송시작일
	private LocalDate fdate; //주문마감일
	
	
	@ManyToOne
	@JoinColumn(name = "seller_id")
	private Seller seller;
	
	public static Product createBoard(ProductForm productForm) {
		Product product = new Product();
		product.setComments(  productForm.getComments() );
		product.setWeight( productForm.getWeight() );
		product.setSprice( productForm.getSprice()  );
		product.setSea(productForm.getSea());
		product.setStartea(productForm.getStartea());
		product.setProductname(productForm.getProductname());
		product.setSdate(productForm.getSdate());
		product.setFdate(productForm.getFdate());
		
		
		return product;
	}
	
}