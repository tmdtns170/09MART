package com.iciafinally.domain;

import com.iciafinally.controller.DriverForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Driver {
	@Id
	@GeneratedValue
	private Long id; // 기사 ID

	@Column(nullable = false, unique = true)
	private String did;// 아이디

	@Column(nullable = false)
	private String dpw;// 비밀번호

	@Column(nullable = false)
	private String dname;// 이름

	private String dphone;// 기사 폰 번호

	private String dcar; // 기사 차종

	private String dnumber;// 기사 차 번호

	private String demail;// 기사 이메일
	
	private double dweight; //탑차할수 있는 무게
	
	
	private String dloc; //기사 주소
	
	private double x;
	
	private double y;

	public static Driver createDriver(DriverForm driverForm) {
		Driver driver = new Driver();
		driver.setDid(driverForm.getDid());
		driver.setDpw(driverForm.getDpw());
		driver.setDname(driverForm.getDname());
		driver.setDphone(driverForm.getDphone());
		driver.setDcar(driverForm.getDcar());
		driver.setDnumber(driverForm.getDnumber());
		driver.setDemail(driverForm.getDemail());
		
		driver.setDweight(driverForm.getDweight());
		driver.setX(driverForm.getX());
		driver.setY(driverForm.getY());
		return driver;
	}

}