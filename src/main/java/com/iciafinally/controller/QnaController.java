package com.iciafinally.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Notice;
import com.iciafinally.domain.Qna;
import com.iciafinally.domain.Reply;
import com.iciafinally.service.NoticeService;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.QnaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Qna")
public class QnaController {

	@Autowired
	private QnaService qnaService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
    private ProductService productService; 
	/*
	 * 문의 목록 페이지 이동 요청
	 * 
	 * @GetMapping("/QnaList") public String QnaList(Model model) {
	 * System.out.println("qnaListd이동요청"); 내가한 문의 목록 조회 List<Qna> qnalist =
	 * qnaService.findAll();
	 * 
	 * model.addAttribute("qnalist", qnalist);
	 * 
	 * return "Qna/QnaList"; }
	 */

	// 문의 삭제 요청
	@PostMapping("Qna/{id}")
	public String delete(@PathVariable("id") Long id) {

		qnaService.deleteById(id);

		return "redirect:/QnaList";

	}

	@Autowired
	private HttpSession session;

	// 내가 쓴 문의 목록 페이지 이동 요청
	@GetMapping("/QnaList")
	public String qnaList(Model model,RedirectAttributes ra) {
		/*
		 * System.out.println("조회할 글 ID :" + qna_id); Qna 조회 Qna qna =
		 * qnaService.findById(qna_id); model.addAttribute("qna",qna);
		 * 
		 * return "Qna/qnaDetail";
		 */
		Object loginId = session.getAttribute("loginId");
		if(loginId == null) {
			System.out.println("로그인후 필요");
			ra.addFlashAttribute("msg", "로그인후 이용가능합니다");
			return"redirect:/";
		}	
		List<Notice> notice = noticeService.findNoticeAll();
		  System.out.println("공지사항 개수: " + notice.size()); // 추가된 로그
		model.addAttribute("notice", notice);
		
		List<Reply>rp = productService.findReplyAll();
		List<Reply>reply = new ArrayList<>();
		for(Reply rep : rp) {
			String sid  =rep.getProductBoard().getProduct().getSeller().getSid();
			if(sid.equals((String)session.getAttribute("loginSid"))) {
				
				if(rep.getProductBoard().getPbstate().equals("Y")) {
					
					if(rep.getRestate().equals("Y")) {
						
						reply.add(rep);
						session.setAttribute("sa","게시글에 댓글이 달렸습니다.");
						
					}
				}
			}
		}
		List<Qna> qna = qnaService.findQnaList();
		
		model.addAttribute("qna", qna);
		System.out.println(qna);
		return "Qna/QnaList";
	}

	@GetMapping("/QnaDetail/{id}")
	public String qnaDetail(@PathVariable("id") Long id, Model model) {
		/*
		 * System.out.println("조회할 글 ID :" + qna_id); Qna 조회 Qna qna =
		 * qnaService.findById(qna_id); model.addAttribute("qna",qna);
		 * 
		 * return "Qna/qnaDetail";
		 */
		Qna qna = qnaService.findById(id);
		model.addAttribute("qna", qna);
		System.out.println(qna);
		return "Qna/QnaDetail";
	}

	// Qna 등록 페이지 이동 요청
	@GetMapping("/register")
	public String showRegisterPage(Model model) {

		List<Member> members = qnaService.getAllMembers(); // 모든 회원 정보 조회
		model.addAttribute("members", members); // 뷰에 회원 정보 전달
		return "Qna/register"; // 등록 폼을 위한 뷰
	}

	// Qna 등록 요청
	@PostMapping("/register")
	public String registerQna(@RequestParam("qnacontents") String qnacontents,@RequestParam("qnatitle") String qnatitle) {
		Object loginId = session.getAttribute("loginId");
		qnaService.registQna(qnacontents, (Long)loginId,qnatitle);
		System.out.println("글등록요청");
		return "redirect:/Qna/QnaList"; // 등록 후 목록 페이지로 리다이렉트
	}

	// 답변 등록 요청
	@PostMapping("/answer/{id}")
	public String registAnswer(@PathVariable("id") Long id, @RequestParam("managercontents") String managercontents) {
		Qna qna = qnaService.findById(id);
		System.out.println("답변등록 요청");
		if (qna != null) {
			qnaService.registanswer(qna, managercontents);
			System.out.println("답변등록 성공");
			return "redirect:/managers/qnaList";
		}
		System.out.println("답변등록 실패");
		return"redirect:/managers/main";
	}
	

} 