package com.iciafinally.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.Manager;
import com.iciafinally.domain.Member;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.mapline.util.KakaoApiUtil;
import com.iciafinally.mapline.util.KakaoApiUtil.Point;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.service.DriverService;
import com.iciafinally.service.ManagerService;
import com.iciafinally.service.MemberService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductBoardService;
import com.iciafinally.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/members")
public class MemberController {

	@Autowired
	private HttpSession session;

	@Autowired
	private MemberService memberService;
	@Autowired
	private DriverService driverService;
	@Autowired
	private ManagerService managerService;
	
	@Autowired
	private ProductBoardService productBoardService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberRepository memberRepository; 

	@GetMapping("/join")
	public String joinForm(Model model) {
		System.out.println("회원 등록 페이지 이동요청");
		MemberForm memberForm = new MemberForm();
		model.addAttribute("memberForm", memberForm);
		return "member/joinForm";
	}

	@PostMapping("/join")
	public String registMember(@Valid MemberForm memberForm, BindingResult result, Model model) {
		System.out.println("회원 등록 요청");
		System.out.println(memberForm);
		model.addAttribute("memberForm", memberForm);
		/* 회원 아이디 중복 확인 */
		if (!result.hasFieldErrors("mid")) { // mid에 Error가 없는 경우
			// 아이디로 회원 정보 조회
			Member checkMember = memberService.findBymid(memberForm.getMid());
			if (checkMember != null) { // 회원 정보가 조회 되는 경우
				// 중복 확인 Error 추가
				result.rejectValue("mid", "duplicateMid", "이미 사용중인 아이디 입니다.");
			}
		}
		/* 유효성검사 */
		if (result.hasErrors()) { // Error가 확인 된 경우
			return "member/joinForm";
		}

		/* 회원 등록 기능 호출 */
		try {

			Double x = KakaoApiUtil.getPointByKeyword(memberForm.getRoadAddress()).getX();
			Double y = KakaoApiUtil.getPointByKeyword(memberForm.getRoadAddress()).getY();
			System.out.println("x : " + x);
			System.out.println("y : " + y);
			memberForm.setX(x);
			memberForm.setY(y);

			Member member = Member.createMember(memberForm);
			LocalDate today = LocalDate.now();
			member.setJoindate(today);
			memberService.registMember(member);
		} catch (Exception e) {
			e.printStackTrace();
			// model.addAttribute("memberForm", memberForm);
			return "member/joinForm";
		}

		return "redirect:/";// redirect - 회원 목록 페이지 이동 요청
	}

	@GetMapping("/list")
	public String memberList(Model model) {
		/* 회원 목록 조회 */
		List<Member> memberList = memberService.findAll();
		model.addAttribute("memberList", memberList);

		return "member/memberList";
	}

	@GetMapping("/login")
	public String memberLogin(MemberForm memberForm, Model model) {
		System.out.println("로그인 페이지 이동 요청");

		return "member/loginForm";

	}

	@PostMapping("/login")
	public String loginMember(MemberForm memberForm) {
		if (memberForm != null) {
			Member checkMember = memberService.findByMidAndMpw(memberForm.getMid(), memberForm.getMpw());
			Manager checkManager = managerService.findByAidAndApw(memberForm.getMid(), memberForm.getMpw());
			if (checkManager != null) {
				System.out.println("관리자 로그인");
				session.setAttribute("loginAid", checkManager.getAid());
				session.setAttribute("loginFid", checkManager.getId());
				return "manager/main";
			}
			if (checkMember == null) {

				System.out.println("아이디 또는 비밀번호가 맞지 않습니다(회원)");
				return "redirect:/members/login";

			} else {
				session.setAttribute("loginFid", checkMember.getId());
				session.setAttribute("loginId", checkMember.getId());
				session.setAttribute("loginMid", checkMember.getMid());
				return "redirect:/";
			}
		}

		return null;

	}

	@GetMapping("/logOut")
	public String logout() {
		session.removeAttribute("loginFid");
		session.removeAttribute("loginId");
		session.removeAttribute("loginMid");
		session.removeAttribute("loginDid");
		session.removeAttribute("loginSid");
		session.removeAttribute("loginDname");
		session.removeAttribute("loginAid");
		session.removeAttribute("sa");
		session.removeAttribute("srp");
		session.removeAttribute("product");
		session.removeAttribute("loginSellerId");
		return "redirect:/";
	}

	@GetMapping("/mypage")
	public String getMypage(Model model, RedirectAttributes ra) {
		Long id = (Long) session.getAttribute("loginId");

		List<Orders> readyList = new ArrayList<>();
		List<Orders> ingList = new ArrayList<>();
		List<Orders> finishList = new ArrayList<>();

		if (id != null) {
			Member member = memberService.mypage(id);
			model.addAttribute("member", member);
			List<Orders> ostate = orderService.findbymember(id);
			for (int i = 0; i < ostate.size(); i++) {
				if (ostate.get(i).getOstate().equals("N")) {
					readyList.add(ostate.get(i));
				}
				else if (ostate.get(i).getOstate().equals("Y")) {
					ingList.add(ostate.get(i));
				}
				else if (ostate.get(i).getOstate().equals("F")) {
					finishList.add(ostate.get(i));
				}
				List<ProductBoard>productBoardList = productBoardService.findMember(id);
				
				model.addAttribute("productBoardList",productBoardList);
			}
			
			
			model.addAttribute("readyList",readyList);
			model.addAttribute("ingList",ingList);
			model.addAttribute("finishList",finishList);
			
			
			return "member/mypage";
		}
		ra.addFlashAttribute("msg", "로그인후 이용가능합니다");
		return "redirect:/";
	}

	@GetMapping("/change")
	public String addrChange(Model model) {
		Long id = (Long) session.getAttribute("loginId");
		if (id != null) {
			Member member = memberService.mypage(id);
			model.addAttribute("member", member);
			return "member/change";
		}
		return "redirect:/members/mypage";
	}

	@PostMapping("/change")
	public String addr(Member member, RedirectAttributes ra) {
	    System.out.println(member);
	    
	    Long memberId = (Long) session.getAttribute("loginId"); // 세션에서 ID 가져오기
	    if (memberId == null) {
	        return "redirect:/members/login"; // 로그인 페이지로 리다이렉트
	    }
	    
	    try {
	        Point point = KakaoApiUtil.getPointByKeyword(member.getRoadAddress());

	        if (point == null) {
	            return "redirect:/members/change?error=주소를 찾을 수 없습니다.";
	        }
   // 기존 회원 정보를 조회
	        Member existingMember = memberService.finById(member.getId()); // 세션 ID 사용

	        if (!member.getMid().equals(existingMember.getMid())) {
	            if (memberService.isMidExists(member.getMid())) {
	                ra.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
	                return "redirect:/members/change"; // 오류 메시지를 포함하여 리다이렉트
	            } else {
	                existingMember.setMid(member.getMid()); // 아이디 변경
	            }
	        }

	        // 필요한 필드 업데이트
	        Double x = point.getX();
	        Double y = point.getY();
	        existingMember.setX(x);
	        existingMember.setY(y);

	        // 비밀번호를 변경하지 않는 경우 기존 비밀번호를 유지
	        if (member.getMpw() == null || member.getMpw().isEmpty()) {
	            existingMember.setMpw(existingMember.getMpw()); // 기존 비밀번호로 설정
	        } else {
	            existingMember.setMpw(member.getMpw()); // 비밀번호 업데이트
	        }

	        // 업데이트된 정보를 저장
	        memberService.registMember(existingMember); // 기존 회원 정보를 업데이트

	        // 세션에 수정된 정보 저장
	        session.setAttribute("loginMid", existingMember.getMid());
	        session.setAttribute("loginId", existingMember.getId());
	        return "redirect:/members/mypage";

	    } catch (IllegalArgumentException e) {
	        ra.addFlashAttribute("error", e.getMessage());
	        return "redirect:/members/change"; // 오류 메시지를 포함하여 리다이렉트
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	        return "redirect:/members/change"; // 오류 발생 시 다시 수정 페이지로 리다이렉트
	    }
	}

}