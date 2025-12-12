package com.iciafinally.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OdPbCheck {
	private Long id; // 주문번호(pk)
	private String productname;//배달 물품 이름
}
