package com.iciafinally.controller;

import java.io.IOException;
import java.time.LocalDateTime;
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
import org.springframework.web.multipart.MultipartFile;

import com.iciafinally.domain.Notice;
import com.iciafinally.domain.Reply;
import com.iciafinally.service.NoticeService;
import com.iciafinally.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	@Autowired
	private HttpSession session;
	@Autowired
	private NoticeService noticeService; 
	@Autowired
	private ProductService productService; 
	@GetMapping("/add")
	public String addNotice() {
		System.out.println();
		return"manager/notice";
	}
	@PostMapping("/add")
	public String noticeAdd(Notice notice,@RequestParam("boardFile") MultipartFile[] boardFile) {
		
		LocalDateTime now = LocalDateTime.now();
		Object loginAid =  session.getAttribute("loginAid");
		if(loginAid != null) {
			notice.setNdate(now);
			try {
				noticeService.registNotice(notice,boardFile);
				return "redirect:/notice/list";
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				return"/notice/add";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				return"/notice/add";
			}
		}
		
		return"/notice/add";
	} 
	@GetMapping("/detail/{id}")
	public String detailnotice( @PathVariable("id") Long id, Model model) {
		System.out.println("공지사항 디테일");
		Notice notice = noticeService.findById(id);
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
		model.addAttribute("rp", reply);
		
		return"notice/detailnotice";
	}
	@GetMapping("/list")
	public String noticeList(Model model) {
		List<Notice> notice = noticeService.findNoticeAll();
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
		model.addAttribute("rp", reply);
		return"notice/noticelist";
	}
	
	
}
