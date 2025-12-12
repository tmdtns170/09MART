package com.iciafinally.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Memberchart;
import com.iciafinally.repository.MemberRepository;



@Service
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepository;
	

	
	public void registMember(Member member) {
	    // 중복 아이디 체크
	    if (isMidExists(member.getMid())) {
	        throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
	    }
	    memberRepository.save(member);
	}

	public Member findBymid(String mid) {
		Member member = memberRepository.findByMid(mid);
		
		return member;
	}
	public Member findBympw(String mpw) {
		Member member = memberRepository.findByMpw(mpw);
		return member;
	}

	public List<Member> findAll() {
		/*SELECT * FROM MEMBER */
		return memberRepository.findAll();
	}

	public Member findByMidAndMpw(String mid, String mpw) {
		Member member = memberRepository.findByMidAndMpw(mid,mpw);
	
		return member;
	}

	public Member mypage(Long id) {
		Member member = memberRepository.findById(id).orElse(null);
		return member;
	}

	public Member finById(Long loginId) {
		Member member = memberRepository.findById(loginId).orElse(null);
		return member;
	}public List<Memberchart> findmemberchart() {
		List<Memberchart> memberchartList = new ArrayList<>();
		List<Object[]> result = memberRepository.findmemberchart();
		
		for(Object[] re : result) {
			if(re != null && re.length > 0 && re[0] != null) {
			Memberchart memberchart = new Memberchart();
			String chartMonth = re[0].toString();
			String product_id = re[1].toString();
			memberchart.setChartMonth(chartMonth);
			memberchart.setMemberea(product_id);
			
			memberchartList.add(memberchart);
			}
		}
		
		System.out.println("memberchartList"+memberchartList);
		return memberchartList;
	}

	
	public boolean isMidExists(String mid) {
	    return memberRepository.findByMid(mid) != null;
	} 

		
	

}