package com.iciafinally.domain;

import java.time.LocalDateTime;

import com.iciafinally.controller.ProductForm;

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
public class ProductBoard {
	@Id
	@GeneratedValue
	private Long id; // 글번호(PK)

	@Column(nullable = false)
	private String ptitle; // 제목
	
	@Column(length = 10000)
	private String pcontents; // 내용

	private int phits; // 조회수
	@Column(length = 4000)
	private String mainFile;

	@Column(length = 4000)
	private String pfilename; // 업로드 한 파일명

	private LocalDateTime pdate;// 작성일
	
	private String pbstate = "Y";//판매상태

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	public void deleteBoard() {
	    this.pbstate = "N"; // 삭제 시 상태를 'N'으로 변경
	}

	/*
	 * public String getImageUrl() { // pfilename을 기반으로 이미지 URL을 생성 return pfilename
	 * != null ? "/images/" + pfilename : "/images/default.png"; // 기본 이미지 URL }
	 */
	

	public static ProductBoard createBoard(ProductForm productForm) {
		ProductBoard productBoard = new ProductBoard();
		LocalDateTime now = LocalDateTime.now();
		productBoard.setPdate(now);
		productBoard.setPcontents(productForm.getPcontents());
		productBoard.setPtitle(productForm.getPtitle());
		return productBoard;
	}

}
