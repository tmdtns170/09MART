package com.iciafinally.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Qna;
import com.iciafinally.repository.ManagerRepository;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.repository.QnaRepository;

@Service
public class QnaService {

	@Autowired
	private QnaRepository qnaRepository;
	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private MemberRepository memberRepository;
	private List<Qna> qnaList;

	// 모든 문의 리스트 출력
	public List<Qna> findAll() {
		return qnaRepository.findAll();

	}

//삭제 기능
	public void deleteById(Long id) {
		qnaRepository.deleteById(id);
	}

	/*
	 * //전체 문의목록 조회 public List<Qna> QnaView(Long qna_id) {
	 * 
	 * return qnaRepository.findByQnaId(qna_id);
	 * 
	 * }
	 */

	// Qna 등록에 필요한 데이터 제공
	public List<Member> getAllMembers() {
		return memberRepository.findAll(); // 모든 회원 정보를 조회
	}

//문의등록기능
	public void registQna(String qnacontents, Long member_id,String qnatitle) {
		Member member = memberRepository.findById(member_id).orElse(null);
		if (member != null) {
			Qna qna = Qna.createQna(qnacontents, member,qnatitle);
			qnaRepository.save(qna);
		}
	}
	/*
	 * //문의 답변 기능 public void registanswer(Qna qna, String managercontents2) {
	 * 
	 * qna.setManagercontents(managercontents2);
	 * qna.setManagerdate(LocalDateTime.now()); qnaRepository.save(qna); }
	 */

	public void registanswer(Qna qna, String managercontents) {
		LocalDateTime now = LocalDateTime.now(); // 현재 시간을 한 번만 호출합니다.
		qna.setManagercontents(managercontents);
		qna.setManagerdate(now);
		qnaRepository.save(qna); // 리스트의 모든 Qna 객체를 저장합니다.
	}
	// 아이디로 조회기능

	public Qna findById(Long id) { 
		return qnaRepository.findById(id).orElse(null);
	}
		 
	
	

	public List<Qna> findByMemberId(Long memberId) {
		return qnaRepository.findByMemberId(memberId);
	}

	public List<Qna> findQnaList() {

		return qnaRepository.findQnaList();
	}
	
	

}