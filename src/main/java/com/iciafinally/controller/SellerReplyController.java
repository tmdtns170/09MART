package com.iciafinally.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iciafinally.domain.Reply;
import com.iciafinally.domain.Sellerreply;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.SellerRepllyService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/sellerReply")
public class SellerReplyController {
	@Autowired
	private HttpSession session;
	@Autowired
	private SellerRepllyService sellerReplyService;
	@Autowired
	private ProductService productService;

	@PostMapping("write")
	public String writeSellerReply(Sellerreply srp) {
		System.out.println("답글작성 컨트롤러");
		System.out.println(srp);
		Object loginSid = (String) session.getAttribute("loginSid");
		if (loginSid == null) {
			return "redirect:/sellers/login";
		}
		Reply rp = productService.findMemberReply(srp.getRep_id());
		rp.setRestate("N");

		productService.updateReply( rp);
		sellerReplyService.saveSrp(loginSid, srp);
		List<Reply> rp1 = productService.findReplyAll();
		List<Reply> reply = new ArrayList<>();
		for (Reply rep : rp1) {
			String sid = rep.getProductBoard().getProduct().getSeller().getSid();
			
			if (sid.equals((String) loginSid)) {

				if (rep.getProductBoard().getPbstate().equals("Y")) {

					if (rep.getRestate().equals("Y")) {

						reply.add(rep);

					}
				}
			}
		}
		boolean check = true;
		for (Reply rep : reply) {
			if (rep.getRestate().equals("Y")) {
				check = false;
				break;
			}
		}
		if (check == true) {
			session.removeAttribute("sa");
		}

		return "redirect:/product/detail/" + srp.getProduct_id();
	}
}
