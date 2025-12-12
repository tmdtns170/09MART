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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.Notice;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.Reply;
import com.iciafinally.domain.Seller;
import com.iciafinally.domain.SellerForm;
import com.iciafinally.mapline.util.KakaoApiUtil;
import com.iciafinally.service.DriverService;
import com.iciafinally.service.NoticeService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductBoardService;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.SellerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
 
@Controller
@RequestMapping("/sellers")
public class SellerController {
	@Autowired
	private SellerService sellerService;

	@Autowired
	private HttpSession session;
	
	@Autowired
	private DriverService driverService;
	@Autowired
	private ProductService productService;
	
	
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private ProductBoardService productBoardService;
	
	@Autowired
	private OrderService orderService;
	@GetMapping("/main")
	public String getSellerMain(Model model,RedirectAttributes ra) {
		String id = (String) session.getAttribute("loginSid");
		
		if(id == null) {
			model.addAttribute("sellerForm", new SellerForm());
			return "seller/sellerLoginForm";
		}
		List<Reply>rp = productService.findReplyAll();
		List<Reply>reply = new ArrayList<>();
		for(Reply rep : rp) {
			String sid  =rep.getProductBoard().getProduct().getSeller().getSid();
			if(sid.equals(id)) {
				
				if(rep.getProductBoard().getPbstate().equals("Y")) {
					
					if(rep.getRestate().equals("Y")) {
						
						reply.add(rep);
						session.setAttribute("sa","게시글에 댓글이 달렸습니다.");
						
					}
				}
			}
		}
		boolean check = true;
		for(Reply rep : reply) {
			if(rep.getRestate().equals("Y")) {
				check = false;
				break;
			}
		}
		if(check == true) {
			session.removeAttribute("sa");
		}
		model.addAttribute("rp", reply);
		
		List<Notice> notice = noticeService.findNoticeAll();
		model.addAttribute("notice", notice);
		Object view = productBoardService.findmyview((Long)session.getAttribute("loginSellerId"));
		System.out.println("view"+view);
		model.addAttribute("view", view);
	    List<Orders> orderList = orderService.findbyseller((Long)session.getAttribute("loginSellerId"));
	    model.addAttribute("orders",orderList);
	    System.out.println("Order List: " + orderList);

		return "seller/sellerMain";
	}

	@GetMapping("/join")
	public String getSellerJoin(Model model) {
		System.out.println("join");
		SellerForm sellerForm = new SellerForm();
		model.addAttribute("sellerForm", sellerForm);
		System.out.println(sellerForm);
		return "seller/sellerJoinForm";
	}

	@GetMapping("/login")
	public String getSellerLogin(Model model) {
		System.out.println("login");
		SellerForm sellerForm = new SellerForm();
		model.addAttribute("sellerForm", sellerForm);
		System.out.println(sellerForm);
		return "seller/sellerLoginForm";
	}

	@PostMapping("/join")
	public String registMember(@Valid SellerForm sellerForm, BindingResult result, Model model) {
		System.out.println("판매자 회원 등록 요청");
		System.out.println(sellerForm);
		model.addAttribute("sellerForm", sellerForm);
		/* 회원 아이디 중복 확인 */
		if (!result.hasFieldErrors("sid")) { // mid에 Error가 없는 경우
			// 아이디로 회원 정보 조회
			Seller checkSeller = sellerService.findBySid(sellerForm.getSid());
			if (checkSeller != null) { // 회원 정보가 조회 되는 경우
				// 중복 확인 Error 추가
				result.rejectValue("sid", "duplicateSid", "이미 사용중인 아이디 입니다.");
			}
		}
		/* 유효성검사 */
		if (result.hasErrors()) { // Error가 확인 된 경우
			return "seller/sellerJoinForm";
		}

		/* 회원 등록 기능 호출 */
		try {
			
			Double x = KakaoApiUtil.getPointByKeyword(sellerForm.getRoadAddress()).getX();
			Double y = KakaoApiUtil.getPointByKeyword(sellerForm.getRoadAddress()).getY();
			System.out.println("x : "+x);
			System.out.println("y : "+y);
			sellerForm.setX(x);
			sellerForm.setY(y);
			Seller seller = Seller.createSeller(sellerForm);
			sellerService.registSeller(seller);
		} catch (Exception e) {
			e.printStackTrace();
			// model.addAttribute("memberForm", memberForm);
			return "seller/sellerJoinForm";
		}
		 // 회원가입 완료 후 로그인 페이지로 리다이렉트
	    return "redirect:/sellers/login";

	}

	// 물품 판매 등록
	@PostMapping("/sellregist")
	public String registSell(Product product, HttpSession session,RedirectAttributes ra) {
		System.out.println("orderController - sellregist호출");

		/*
		 * if(loginId == null) { // 로그인이 되어 있지 않은 경우 return "redirect:/members/login";
		 * // 로그인 페이지로 이동 }
		 */
//		Object loginId = session.getAttribute("loginId");
		 Long loginSid = (Long)session.getAttribute("loginSid");
		
		try {
			product.getSeller().setId(loginSid); 
			
			sellerService.saveProduct(product);
			System.out.println(product);
			ra.addFlashAttribute("msg", "상품이 등록되었습니다");
			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GetMapping("/SellerForm")
	public String showForm(Model model) {
		model.addAttribute("sellList", new Product());
		System.out.println("SellerForm 컨트롤러");
		return "seller/SellerForm";
	}

	@PostMapping("/login")
	public String loginMember(Seller seller,Model model) {
		
			Seller checkSeller = sellerService.findBySidAndSpw(seller.getSid(), seller.getSpw());
			if (checkSeller == null) {

				System.out.println("아이디 또는 비밀번호가 맞지 않습니다");
				
				return "redirect:/sellers/login";

			} else {
				session.setAttribute("loginSid", checkSeller.getSid());
				session.setAttribute("loginFid", checkSeller.getSid());
				session.setAttribute("loginSellerId",checkSeller.getId() );
				LocalDate today = LocalDate.now();
				List<Product> productList = sellerService.findBySidAndFdate(checkSeller.getSid(), today);
				
				System.out.println("PRSSS"+productList);
				if(productList != null) {
					driverService.registDelivery(productList);	
					
				}
				
				return "redirect:/sellers/main";
			}
	}
	@GetMapping("/mypage")
	public String getMypage(Model model) {
		String id = (String) session.getAttribute("loginSid");
		if(id != null) {
			Seller seller = sellerService.mypage(id);
			model.addAttribute("seller", seller);
			return"seller/smypage";
		}
		List<Reply>rp = productService.findReplyAll();
		List<Reply>reply = new ArrayList<>();
		for(Reply rep : rp) {
			String sid  =rep.getProductBoard().getProduct().getSeller().getSid();
			if(sid.equals(id)) {
				
				if(rep.getProductBoard().getPbstate().equals("Y")) {
					
					if(rep.getRestate().equals("Y")) {
						
						reply.add(rep);
						session.setAttribute("sa","게시글에 댓글이 달렸습니다.");
						
					}
				}
			}
		}
		model.addAttribute("rp", reply);
		return"/sellers/login";
	}
	@GetMapping("/change")
	public String sellerChange(Model model) {
		String id = (String) session.getAttribute("loginSid");
		if(id != null) {
			Seller seller = sellerService.mypage(id);
			model.addAttribute("seller", seller);
			return"seller/schange";
		}
		List<Reply>rp = productService.findReplyAll();
		List<Reply>reply = new ArrayList<>();
		for(Reply rep : rp) {
			String sid  =rep.getProductBoard().getProduct().getSeller().getSid();
			if(sid.equals(id)) {
				
				if(rep.getProductBoard().getPbstate().equals("Y")) {
					
					if(rep.getRestate().equals("Y")) {
						
						reply.add(rep);
						session.setAttribute("sa","게시글에 댓글이 달렸습니다.");
						
					}
				}
			}
		}
		model.addAttribute("rp", reply);
		return"seller/login";
	}
	@PostMapping("/change")
	public String selleraddr(Seller seller) {
		try {
			  // 기존 셀러 정보를 가져옴 (SID를 통해)
	        Seller existingSeller = sellerService.findBySid(seller.getSid()); // ID를 사용하여 기존 정보를 가져오는 메서드
	        // 현재 아이디와 변경하려는 아이디가 같은 경우
		    if (seller.getSid().equals(existingSeller.getSid())) {
		        // 그냥 넘어가도록 설정
		        return "redirect:/sellers/mypage"; // 또는 다른 적절한 경로로 리다이렉트
		    }

	        // 비밀번호가 비어 있으면 기존 비밀번호로 설정
	        if (seller.getSpw() == null || seller.getSpw().isEmpty()) {
	            seller.setSpw(existingSeller.getSpw()); // 기존 비밀번호로 설정
	        }
	        
			Double x = KakaoApiUtil.getPointByKeyword(seller.getRoadAddress()).getX();
			Double y = KakaoApiUtil.getPointByKeyword(seller.getRoadAddress()).getY();
			seller.setX(x);
			seller.setY(y);
			seller.setSaddress(
					seller.getPostcode()
	                + " " + seller.getRoadAddress()
	                + " " + seller.getJibunAddress()
	                + " " + seller.getExtraAddress()
					);
			sellerService.registSeller(seller);
			return "redirect:/sellers/mypage";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/sellers/change";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/sellers/change";
		}
		
	}

	@GetMapping("productchart")
	@ResponseBody
	public List<SellerChart> getMethodName() {
		
		//sellerService.productchart((Long)session.getAttribute("loginSellerId"));
		
		return sellerService.getSellerChartData((Long)session.getAttribute("loginSellerId"));
		 
	}
	@GetMapping("/orders")
	public String getAllOrders(Model model) {
		List<Orders> orderList = orderService.findbyseller((Long)session.getAttribute("loginSellerId"));
	    model.addAttribute("orders",orderList);
	    return "seller/sellerallOrders"; // 모든 주문을 표시할 뷰 페이지
	}

	
	
	
}