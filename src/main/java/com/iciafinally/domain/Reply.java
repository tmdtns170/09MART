package com.iciafinally.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Reply {

	@Id
	@GeneratedValue
	private Long id;	
	
	private String recontents;
	
	private LocalDateTime redate;
	
	private Long product_id;
	
	private String restate = "Y";
	
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="productboard_id")
	private ProductBoard productBoard;
	
	@OneToMany(mappedBy = "reply")
	private List<Sellerreply> sellerReplys = new ArrayList<>();
	
}












