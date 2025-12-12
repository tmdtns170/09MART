package com.iciafinally.controller;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DriverForm {

	//@NotBlank(message ="아이디는 필수 입니다")//(message="")오류창 멘트 정하기
	@NotBlank(message="아이디는 필수 입력 입니다.")
	private String did;// 아이디
	@NotBlank(message="비밀번호는 필수 입력 입니다.")
	private String dpw;// 비밀번호
	@NotBlank(message="이름은 필수 입력 입니다.")
	private String dname;// 이름
	@NotBlank(message="폰 번호는 필수 입력 입니다.")
	private String dphone;//기사 폰 번호
	@NotBlank(message="차종은 필수 입력 입니다.")
    private String dcar; //기사 차종
	
	private String dnumber;//기사 차 번호
	
	private double dweight;//탑차할수 있는 무게
	
	private String demail;//기사 이메일
	
	private double x = 126.675113024566;
	
	private double y = 37.4388938204128;

   
    
}