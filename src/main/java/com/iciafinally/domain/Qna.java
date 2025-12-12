package com.iciafinally.domain;


import java.time.LocalDateTime;

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
public class Qna {
	@Id
	@GeneratedValue
	private Long id; // ID
	
	private String qnatitle;//제목
	
	private String qnacontents;// 내용담기
	
	private LocalDateTime qnadate;// QnA 작성시간
	
	@Column(nullable = true)
    private String managercontents;// 관리자 답변 내용담기
	
	@Column(nullable = true)
    private LocalDateTime managerdate;// 관리자 답변 시간
	

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member; //질문한 회원
	
	
	
	
	
	
		/*
		 * this.managercontents = managerResponse2; this.managerdate =
		 * LocalDateTime.now();
		 */
	
	
	public static Qna createQna(String qnacontents, Member member,String qnatitle) {
		Qna qna = new Qna();
		qna.setQnacontents(qnacontents);
		qna.setQnadate(LocalDateTime.now());
		qna.setMember(member);
		qna.setQnatitle(qnatitle);
		
		
		
		
		return qna;
	}

	
	
}