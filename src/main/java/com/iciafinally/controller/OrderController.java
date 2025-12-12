package com.iciafinally.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.service.MemberService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductBoardService;
import com.iciafinally.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderservice;
	@Autowired
	private ProductService productService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private ProductBoardService productBoardService;

	@GetMapping("/order")
	public String pageOrder(@RequestParam("productId") Long productId, @RequestParam("boardId") Long boardId,
			Model model) {
		
		ProductBoard pb = productService.findByProductId(productId);
		model.addAttribute("pb", pb);

		return "product/order";
	}

	@PostMapping("/order")
	public String getOrder(HttpSession session, Model model, @RequestParam("productId") Long productId,
			@RequestParam("boardId") Long boardId, @RequestParam("bea") int bea) {
		System.out.println("orderController - getOrder호출");

		Long loginId = (Long) session.getAttribute("loginId");
		if (loginId == null) {
			return "redirect:/members/login";
		}

		
		try {

			Product product = productService.findById(productId);
			ProductBoard pb = productBoardService.findById(boardId);
			Member member = memberService.finById((Long) loginId);
			model.addAttribute("pb", pb);
			double count = orderservice.findSea(pb.getProduct().getId()); //
			double total = (double) pb.getProduct().getSea() - count; // 세줄로 받아옴
			model.addAttribute("total", (int) total); //
			model.addAttribute("bea", bea);
			// orderservice.saveOrder(orderlist, member, product);
			return "product/order";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@PostMapping("/add")
	public String addorder(@RequestParam("productId") Long productId, HttpSession session, RedirectAttributes ra,
			Orders orders) {
		// 로그인된 사용자 정보 가져오기
		Long loginId = (Long) session.getAttribute("loginId");

		if (loginId != null) {
			// 판매자 또는 구매자의 정보를 가져오기
			Member member = memberService.finById(loginId);
			Product product = productService.findById(productId);

			if (product != null) {
				// 주문 정보 생성
				
				orders.setProduct(product);
				orders.setMember(member);
				orders.setOrderdate(LocalDateTime.now());

				// 주문 저장
				orderservice.saveOrder(orders);

				ra.addFlashAttribute("msg", "주문이 완료되었습니다.");
				return "redirect:/"; // 주문 확인 페이지로 리다이렉트
			} else {
				ra.addFlashAttribute("errorMsg", "상품을 찾을 수 없습니다.");
				return "redirect:/product/list"; // 상품 목록 페이지로 리다이렉트
			}
		}

		return "redirect:/members/login"; // 로그인이 안된 경우 로그인 페이지로 리다이렉트
	}

}