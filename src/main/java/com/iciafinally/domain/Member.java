package com.iciafinally.domain;

import java.time.LocalDate;

import com.iciafinally.controller.MemberForm;

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
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String mid; // 아이디

    @Column(nullable = false)
    private String mpw; // 비밀번호

    @Column(nullable = false)
    private String mname; // 회원 이름

    private String mphone; // 회원 폰 번호
    
    
    private Double x;//경도
    private Double y;//위도
    private String memail; // 회원 이메일
    private String postcode;
    private String roadAddress;
    private String jibunAddress;
    private String extraAddress;
    private String detailAddress;//상세주소 
    private LocalDate joindate;
    public static Member createMember(MemberForm memberForm) {
        Member member = new Member();
        member.setMid(memberForm.getMid());
        member.setMpw(memberForm.getMpw());
        member.setMname(memberForm.getMname());
        member.setMphone(memberForm.getMphone());
        member.setMemail(memberForm.getMemail());
        member.setPostcode(memberForm.getPostcode());
        member.setRoadAddress(memberForm.getRoadAddress());         
        member.setJibunAddress(memberForm.getJibunAddress());         
        member.setExtraAddress(memberForm.getExtraAddress());                 
        
        member.setDetailAddress(memberForm.getDetailAddress());
        member.setX(memberForm.getX());
        member.setY(memberForm.getY());
        return member;
    }
}