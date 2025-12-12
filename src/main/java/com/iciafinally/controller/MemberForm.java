package com.iciafinally.controller;



import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberForm {
  
	//@NotBlank(message ="아이디는 필수 입니다")//(message="")오류창 멘트 정하기
	@NotBlank(message="아이디는 필수 입력 입니다.")
	private String mid;// 아이디
	@NotBlank(message="비밀번호는 필수 입력 입니다.")
	private String mpw;// 비밀번호
	@NotBlank(message="이름은 필수 입력 입니다.")
	private String mname;// 이름
	
	private String mphone;//회원 폰 번호
	
	private String memail; //회원 이메일

    
    
    private String postcode; //우편번호
    private String roadAddress;//도로명주소
    private String jibunAddress;//지번주소
    private String detailAddress;//상세주소
    private String extraAddress; //참고항목
    
    private Double x;
    private Double Y;

  
    
}