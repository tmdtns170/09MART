package com.iciafinally.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter 
@Setter
public class Seller {
	@Id
	@GeneratedValue
	
	private Long id; // 판매자 ID
	
	@Column(nullable = false, unique = true)
	private String sid; // 아이디
	
	@Column(nullable = false)
	private String spw; // 비밀번호
	
	@Column(nullable = false)
	private String sname; // 이름
	
	@Column(nullable = false)
	private String sphone; // 핸드폰 번호
	
	private String saddress;//총주소
	
	private String postcode; //우편번호
    private String roadAddress;//도로명주소
    private String jibunAddress;//지번주소
    private String detailAddress;//상세주소
    private String extraAddress; //참고항목
	
	private Double x; 
    private Double y;
	
	public static Seller createSeller(SellerForm sellerForm) {
		Seller seller = new Seller();
		seller.setSid(sellerForm.getSid());
		seller.setSpw(sellerForm.getSpw());
		seller.setSname(sellerForm.getSname());
		seller.setSphone(sellerForm.getSphone());
		String Saddress = sellerForm.getPostcode()
                + " " + sellerForm.getRoadAddress()
                + " " + sellerForm.getJibunAddress()
                + " " + sellerForm.getExtraAddress();
		seller.setSaddress(Saddress);
		seller.setX(sellerForm.getX());
		seller.setY(sellerForm.getY());
		seller.setPostcode(sellerForm.getPostcode());
		seller.setRoadAddress(sellerForm.getRoadAddress());
		seller.setJibunAddress(sellerForm.getJibunAddress());
		seller.setDetailAddress(sellerForm.getDetailAddress());
		seller.setExtraAddress(sellerForm.getExtraAddress());
		return seller;
	}



	
	
}