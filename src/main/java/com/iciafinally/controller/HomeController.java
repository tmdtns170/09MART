package com.iciafinally.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.iciafinally.domain.BookMark;
import com.iciafinally.domain.Notice;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.domain.Sellerreply;
import com.iciafinally.service.BookMarkService;
import com.iciafinally.service.NoticeService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.SellerRepllyService;
import com.iciafinally.socketUtil.ChatHandler;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private ChatHandler chatHandler;
	@Autowired
	private ProductService productService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private HttpSession session;
	@Autowired
	private SellerRepllyService sellerRepllyService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private BookMarkService bookMarkService;

	@GetMapping("/")
	public String getHome(Model model, MemberForm memberForm) {

		System.out.println("home");
		Long id = (Long) session.getAttribute("loginId");
		List<BookMark> bmk = new ArrayList<>();
		if (id != null) {
			List<BookMark> bookMark = bookMarkService.getFavoritesByMember(id);
			for (BookMark bMark : bookMark) {
				if (bMark.getProduct().getPbstate().equals("Y")) {
					bmk.add(bMark);
				}
			}
		}
		if (bmk.size() > 0) {

			session.setAttribute("bookMark", bmk);
		}
// 

		LocalDate today = LocalDate.now();
		List<ProductBoard> pb = productService.findBoardAll();

		for (int i = 0; i < pb.size(); i++) {
			if (pb.get(i).getPbstate().equals("Y")) {
				if (pb.get(i).getProduct().getSdate().equals(today)) {
					pb.get(i).setPbstate("N");
					productService.updatePbstate(pb.get(i));
				}
			}
		}
		pb = productService.productList();
		List<ProductBoardDto> productBoardDtoList = new ArrayList<>();
		List<BookMark> myBookMarkList = bookMarkService.getFavoritesByMember(id);

		for (ProductBoard productBoard : pb) {
			ProductBoardDto productBoardDto = ProductBoardDto.createProductBoardDto(productBoard);
			productBoardDto.setProduct(productBoard.getProduct());
			if (productBoard.getPbstate().equals("Y")) {

				Long product_Id = productBoard.getId();
				for (BookMark bookMark : myBookMarkList) {
					Long bmkProductId = bookMark.getProduct().getId();
					if (product_Id.equals(bmkProductId)) {
						productBoardDto.setBookMark(bookMark);
					}
				}
			}
			productBoardDtoList.add(productBoardDto);
		}
		System.out.println("productBoardDtoList");
		System.out.println(productBoardDtoList);
		

		model.addAttribute("pbList", productBoardDtoList);
		//마감임박

		List<ProductBoardDto>fpb = new ArrayList<>(); 
		for(ProductBoardDto pbdto : productBoardDtoList) {
			long daysUntilTarget = ChronoUnit.DAYS.between(today, pbdto.getProduct().getFdate()); // 남은 일수 계산
			  if (daysUntilTarget >= 0 && daysUntilTarget <= 4) {
				  fpb.add(pbdto);
		        }
		}
        model.addAttribute("fpb", fpb);
		
		
		
		
		// 조회수순
		List<ProductBoardDto> HitDtoList = new ArrayList<>();
		List<ProductBoard> bhitBoard = productService.productListPhits();
		for (ProductBoard productBoard : bhitBoard) {
			ProductBoardDto productBoardDto = ProductBoardDto.createProductBoardDto(productBoard);
			productBoardDto.setProduct(productBoard.getProduct());
			if (productBoard.getPbstate().equals("Y")) {

				Long product_Id = productBoard.getId();
				for (BookMark bookMark : myBookMarkList) {
					Long bmkProductId = bookMark.getProduct().getId();
					if (product_Id.equals(bmkProductId)) {
						productBoardDto.setBookMark(bookMark);
					}
				}
			}
			HitDtoList.add(productBoardDto);
		}
		model.addAttribute("phits", HitDtoList);
		// 판매순
		// List<Orders>bestOrders = orderService.findBestBea();
		List<ProductBoard> BestProduct = orderService.findBestBea();
//		for (Orders od : bestOrders){
//			ProductBoard bePb =  productService.findByProductId(od.getProduct().getId());
//			if(bePb.getPbstate().equals("Y")) {
//				BestProduct.add(bePb);
//			}
//			
//			
//			     
//		}
		List<ProductBoardDto> BestDtoList = new ArrayList<>();

		for (ProductBoard productBoard : BestProduct) {
			ProductBoardDto productBoardDto = ProductBoardDto.createProductBoardDto(productBoard);
			productBoardDto.setProduct(productBoard.getProduct());
			if (productBoard.getPbstate().equals("Y")) {

				Long product_Id = productBoard.getId();
				for (BookMark bookMark : myBookMarkList) {
					Long bmkProductId = bookMark.getProduct().getId();
					if (product_Id.equals(bmkProductId)) {
						productBoardDto.setBookMark(bookMark);
					}
				}
				BestDtoList.add(productBoardDto);
			}
			
		}

		model.addAttribute("bpb", BestDtoList);
		List<Notice> notice = noticeService.findNoticeAll();
		model.addAttribute("notice", notice);

		if (id != null) {
			List<Sellerreply> sellerreply = sellerRepllyService.findAll();
			List<Long> sreplyIdList = new ArrayList<>();
			for (Sellerreply srp : sellerreply) {
				if (srp.getRestate().equals("Y")) {

					if (srp.getReply().getProductBoard().getPbstate().equals("Y")) {
						System.out.println(srp.getReply().getMember().getId());
						System.out.println("id: " + id);
						if (srp.getReply().getMember().getId().equals(id)) {
							System.out.println("세번째");
							sreplyIdList.add(srp.getReply().getProductBoard().getId());

						}

					}
				}
			}
			System.out.println("srp : " + sreplyIdList.size());
			if (sreplyIdList.size() > 0) {
				session.setAttribute("srp", sreplyIdList);
			}
		}
		List<OdPbCheck> product = new ArrayList<>();
		if (id != null) {
			List<Orders> orders = orderService.findByMember(id);

			for (Orders od : orders) {
				if (od.getOstate().equals("F")) {
					OdPbCheck check = new OdPbCheck();
					check.setId(od.getId());
					check.setProductname(od.getProduct().getProductname());
					product.add(check);
				}
			}
		}
		if (product.size() > 0) {
			session.setAttribute("product", product);
		}



		return "home";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	@GetMapping("/chatPage")
	public String chatPage() {

		return "chatPage";
	}

	@PostMapping("/chatImgUpload")
	@ResponseBody
	public String postMethodName(@RequestParam("imgFile") MultipartFile chatImg) {
		System.out.println("chatImgUpload 요청");
		System.out.println(chatImg.getOriginalFilename());
		String savePath = "C:\\bootWorkspace\\memberBoard\\src\\main\\resources\\static\\chatImgUpload";

		String imgPath = UUID.randomUUID().toString();

		String originFilename = chatImg.getOriginalFilename();
		int suffixIndex = originFilename.lastIndexOf(".");
		String suffixStr = originFilename.substring(suffixIndex);

		imgPath = imgPath + suffixStr;
		// 파일저장
		try {
			chatImg.transferTo(new File(savePath, imgPath));
			return "/chatImgUpload/" + imgPath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GetMapping("/join")
	public String join() {
		return "join";
	}

	@GetMapping("/check")
	@ResponseBody
	public String stateCheck(@RequestParam("orderid") String orderid) {
		Orders orders = orderService.findById(Long.parseLong(orderid));

		orders.setOstate("FF");
		orderService.save(orders);
		Long id = (Long) session.getAttribute("loginId");
		List<OdPbCheck> product = new ArrayList<>();
		if (id != null) {
			List<Orders> orders1 = orderService.findByMember(id);

			for (Orders od : orders1) {
				if (od.getOstate().equals("F")) {
					OdPbCheck check = new OdPbCheck();
					check.setId(od.getId());
					check.setProductname(od.getProduct().getProductname());
					product.add(check);
				}
			}
		}

		if (product.size() > 0) {
			session.setAttribute("product", product);

		} else {

			session.removeAttribute("product");

		}
		return "성공";

	}

}