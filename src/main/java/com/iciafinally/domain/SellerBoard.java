package com.iciafinally.domain;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
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
public class SellerBoard {
	 
	@Id
	@GeneratedValue
	private Long id; 		    // 글번호(PK)
	
	@Column( nullable = false )
	private String stitle;      // 제목
	
	private String scontents;   // 내용
	
	private int shits;		    // 조회수
	
	@Column( length = 4000 )
	private String sfilename;   // 업로드 한 파일명
	
	
	
	
	
	/* 작성자 정보 조인 */
	@ManyToOne
	@JoinColumn(name="product_id") // 회원
	private Product product;
}
