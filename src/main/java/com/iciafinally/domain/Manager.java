package com.iciafinally.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Manager {
	
	@Id
	@GeneratedValue
	private long id; // 아이디
	
	@Column(nullable = false)
	private String aid; // 관리자 아이디
	
	@Column(nullable = false)
	private String apw; // 관리자 비밀번호
	
	
	
	
	

}
