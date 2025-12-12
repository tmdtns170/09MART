package com.iciafinally.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
public class Orders {
	@Id
	@GeneratedValue
	private Long id; // 주문번호(pk)

	private String comments;// 요청사항

	private String receipt;// 배달 수취인

	private double bea;// 주문갯수

	private LocalDateTime orderdate;// 주문시간
	
	
	
	//배달 상태
	private String ostate = "N";//Y N F

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
}