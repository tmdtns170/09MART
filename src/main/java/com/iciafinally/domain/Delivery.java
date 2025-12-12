package com.iciafinally.domain;



import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Entity
@Getter @Setter @ToString
public class Delivery {
	@Id
	@GeneratedValue
	private Long id; // 배송정보 ID
	
	private LocalDate ddate; // 배송일
	
	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@ManyToOne
	@JoinColumn(name = "orders_id")
	private Orders orders;
	
}