package com.iciafinally.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Notice {
	@Id
	@GeneratedValue
	private Long id;
	
	private String ntitle; // 제목
	@Column(length = 4000)
	private String ncontents; // 내용
	
	private int nhits; // 조회수
	
	@Column(length = 4000)
	private String nfilename; // 업로드 한 파일명
	
	private LocalDateTime ndate;// 작성일
}
