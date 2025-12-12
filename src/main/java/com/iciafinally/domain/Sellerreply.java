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
@Getter
@Setter
@ToString
public class Sellerreply {
	@Id
	@GeneratedValue
	private Long id;

	private String srecontents;

	private LocalDateTime sredate;
	
	private Long product_id;
	
	private Long rep_id;
	
	private String restate = "Y";
	
	@ManyToOne
	@JoinColumn(name="reply_id")
	private Reply reply;
	
	@ManyToOne
	@JoinColumn(name="seller_id")
	private Seller seller;
}
