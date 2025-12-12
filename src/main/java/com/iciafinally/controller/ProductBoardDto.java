package com.iciafinally.controller;

import java.time.LocalDateTime;

import com.iciafinally.domain.BookMark;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProductBoardDto {
	
	private Long id; // 글번호(PK)

	private String ptitle; // 제목

	private String pcontents; // 내용

	private int phits; // 조회수
	
	private String mainFile;

	private String pfilename; // 업로드 한 파일명

	private LocalDateTime pdate;// 작성일
	
	private String pbstate;//판매상태
	
	private Product product;
	
	private BookMark bookMark;
	
	public static ProductBoardDto createProductBoardDto(ProductBoard pboard) {
		ProductBoardDto productBoardDto = new ProductBoardDto();
		productBoardDto.setId(pboard.getId());
		productBoardDto.setPcontents(pboard.getPcontents());
		productBoardDto.setPhits(pboard.getPhits());
		
		productBoardDto.setMainFile(pboard.getMainFile());
		productBoardDto.setPfilename(pboard.getPfilename());
		
		productBoardDto.setPdate(pboard.getPdate());
		productBoardDto.setPbstate(pboard.getPbstate());
		return productBoardDto;
	}

}
