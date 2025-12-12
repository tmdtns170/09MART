package com.iciafinally.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductForm {

	
	//게시글
	private String ptitle;

	private String pcontents;

	private MultipartFile mainFile;
	
	private MultipartFile[] boardFile;
	
	
	//상품
	
	private String comments;// 요청사항

	private double weight; // 무게

	private int sprice; // 물품비 가격

	private int sea; // 총 등록갯수

	private String startea;// 배송시작 개수

	private String productname;// 배달 물품 이름

	

	

	private LocalDate sdate; // 배송시작일
	private LocalDate fdate; // 배송마감일

}
