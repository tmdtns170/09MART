package com.iciafinally.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class DriverRoute {
	// 배송기사 아이디, 배송일,  최적이동 경로 
	@Id
	@GeneratedValue
	private Long id; // 경로 ID
	
	private LocalDate rtdate;//배송일
	
	private String drid; //기사아이디
	
	private int distance; //거리
	
	private int duration; //소요시간
	
	@Lob
	private String pathlist; //전체이동경로
	
	@Lob
	private String nodelist; //방문지목록
	
	@Lob
	private String productBySellernodelist; //중복된 픽업목록
	@Lob
	private String productByMembernodelist; //중복된 배송목록
	
	
	
}