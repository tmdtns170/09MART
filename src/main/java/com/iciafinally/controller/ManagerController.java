package com.iciafinally.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iciafinally.domain.Memberchart;
import com.iciafinally.domain.Pagevisit;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.domain.Qna;
import com.iciafinally.service.ManagerService;
import com.iciafinally.service.MemberService;
import com.iciafinally.service.ProductBoardService;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.QnaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/managers")
public class ManagerController {
	@Autowired
	private ProductBoardService productBoardService;
	
	@Autowired
	private ProductService productService;
	@Autowired
	private HttpSession session;
	
	@Autowired 
	private ManagerService managerService;
	
	@Autowired
	private QnaService qnaService;
	
	@Autowired
	private MemberService memberService;
	@GetMapping("/main")
	public String getMangerMain() {
		
		return "manager/main";
	}
	@GetMapping("/qnaList")
	public String managerQnaList(Model model) {
		
		Object loginAid = session.getAttribute("loginAid");
		if(loginAid == null) {
			return"redirect:/";
		}
		List<Qna> qna = qnaService.findQnaList();
		model.addAttribute("qna", qna);
		return "manager/managerQnaList";
	}
	
@GetMapping("List")
public String productBoardList(Model model) {
	List<ProductBoard> prBoard = productService.productList();
	model.addAttribute("productBoard", prBoard);
	return "manager/mList";
}

@GetMapping("memberchart")
@ResponseBody
public List<Memberchart> getMethodName() {
	System.out.println("실행");
	
	
	return memberService.findmemberchart();
	 
}

@GetMapping("visitcheck")
@ResponseBody
public String visitcheck() {
	LocalDate today = LocalDate.now();
	Pagevisit pagevisit = managerService.findByVisitday(today);
	System.out.println("oi");
	if(pagevisit==null) {
		 pagevisit = new Pagevisit();
		 pagevisit.setVisitea(1);
		 pagevisit.setVisitday(today);
		 System.out.println("플러스1");
	}
	else {
		pagevisit.setVisitea(pagevisit.getVisitea()+1);		
	} 
	managerService.save(pagevisit);
    return null;
}


@GetMapping("visitusercheak")
@ResponseBody
public List<VisitChart> visitusercheak() {
	
	return managerService.visitusercheak();
	 
}




	
	
}