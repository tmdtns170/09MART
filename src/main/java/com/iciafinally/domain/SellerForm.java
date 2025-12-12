package com.iciafinally.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SellerForm {

	//@NotBlank(message ="아이디는 필수 입니다")//(message="")오류창 멘트 정하기
	@NotBlank(message="아이디는 필수 입력 입니다.")
	private String sid;// 아이디
	@NotBlank(message="비밀번호는 필수 입력 입니다.")
	private String spw;// 비밀번호
	@NotBlank(message="이름은 필수 입력 입니다.")
	private String sname;// 이름
	@NotBlank(message="폰 번호는 필수 입력 입니다.")
	private String sphone;//판매자 폰 번호
	
	private String postcode; //우편번호
    private String roadAddress;//도로명주소
    private String jibunAddress;//지번주소
    private String detailAddress;//상세주소
    private String extraAddress; //참고항목
    private Double x; 
    private Double y; 
    
	   
}