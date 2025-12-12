package com.iciafinally.domain;

import java.time.LocalDateTime;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comments {

@Id
@GeneratedValue

private Long id;// 댓글 순서 번호
private String recontents;// 내용담기
private LocalDateTime redate;// 댓글 남긴 시간


@ManyToOne
@JoinColumn(name = "member_id")
private Member member;

@ManyToOne
@JoinColumn(name = "product_id")
private Product product;
	
@ManyToOne
@JoinColumn(name = "manager_id")
private Manager manager;

@ManyToOne
@JoinColumn(name = "seller_id")
private Seller seller;



public static Comments createComment(String recontents2, Member member2, Product product2,Manager manager2,Seller seller2) {
	
	Comments comments = new Comments();
	comments.setRecontents(recontents2);
	comments.setProduct(product2);
	comments.setRedate(LocalDateTime.now());
	comments.setMember(member2);
	comments.setManager(manager2);
	comments.setSeller(seller2);
	
	return comments;
	
}





}