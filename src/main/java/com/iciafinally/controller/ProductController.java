package com.iciafinally.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.BookMark;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.domain.Reply;
import com.iciafinally.domain.Seller;
import com.iciafinally.domain.Sellerreply;
import com.iciafinally.repository.SellerRepository;
import com.iciafinally.service.BookMarkService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductService;
import com.iciafinally.service.SellerRepllyService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private HttpSession session;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private ProductService productService;
	@Autowired
	private SellerRepllyService sellerReplyService;
	@Autowired
	private BookMarkService bookMarkService;
	@Autowired
	private OrderService orderService;

	@GetMapping("/add")
	public String addProduct(Model model) {
		System.out.println("판매물품 등록페이지 이동");
		List<Reply> rp = productService.findReplyAll();
		List<Reply> reply = new ArrayList<>();
		for (Reply rep : rp) {
			String sid = rep.getProductBoard().getProduct().getSeller().getSid();
			if (sid.equals((String) session.getAttribute("loginSid"))) {

				if (rep.getProductBoard().getPbstate().equals("Y")) {

					if (rep.getRestate().equals("Y")) {

						reply.add(rep);
						session.setAttribute("sa", "게시글에 댓글이 달렸습니다.");

					}
				}
			}
		}
		model.addAttribute("rp", reply);
		return "product/addProduct";
	}

	@PostMapping("/add")
	public String productAdd(ProductForm productForm, RedirectAttributes ra) {
		LocalDateTime now = LocalDateTime.now();
		String loginSid = (String) session.getAttribute("loginSid");
		if (loginSid != null) {
			Seller seller = sellerRepository.findBySid(loginSid);
			Product product = Product.createBoard(productForm);
			product.setSelldate(now);
			product.setSeller(seller);
			product.setSloc(seller.getSaddress());

			productService.registProduct(product);

			try {

				productService.registBoard(productForm, seller.getId());
				ra.addFlashAttribute("msg", "상품이 등록되었습니다");

			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "redirect:/sellers/main";
		}

		return "home";
	}

	@GetMapping("/list")
	public String productBoardList(Model model) {
		List<ProductBoard> prBoard = productService.productList();
		model.addAttribute("productBoard", prBoard);
		return "product/boardList";
	}

	@GetMapping("/detail/{id}")
	public String productBoardDetail(@PathVariable("id") Long pdb_id, Model model) {
		System.out.println("프로덕트 보드 디테일 컨트롤러");
		System.out.println("pdb Id: " + pdb_id);

		ProductBoard productBoard = productService.boardDetail(pdb_id);
		String pcontents = productBoard.getPcontents();
		pcontents = pcontents.replace(" ", "&nbsp;");
		pcontents = pcontents.replace("\n", "<br>");
		model.addAttribute("product", productBoard);
		model.addAttribute("pcontents", pcontents);
		double count = orderService.findSea(productBoard.getProduct().getId());
		double total = (double) productBoard.getProduct().getSea() - count;
		model.addAttribute("total", (int) total);
		List<Reply> replyList = productService.findReply(pdb_id);
		model.addAttribute("replyList", replyList);

		List<Sellerreply> sellerRepltList = sellerReplyService.findAll();
		model.addAttribute("srList", sellerRepltList);

		Object loginId = session.getAttribute("loginId");
		if (loginId != null) {
			List<BookMark> bookMarkList = bookMarkService.findByPorductId(productBoard.getId());
			Long memberId = (Long) loginId;
			for (BookMark bookmark : bookMarkList) {
				if (bookmark.getMember().getId().equals(memberId)) {
					System.out.println("bookMark : " + bookmark);
					model.addAttribute("isBookmark", bookmark);
				}
			}

		}

		if (loginId != null) {
			// 댓글확인시 알림 지우기
			List<Sellerreply> sellerReply = sellerReplyService.findByProductBoard(pdb_id);
			List<Long> sreplyIdList = new ArrayList<>();
			for (Sellerreply srp : sellerReply) {
				if (srp.getReply().getProductBoard().getId().equals(pdb_id)) {

					if (srp.getReply().getMember().getId() == (Long) loginId) {
						srp.setRestate("N");
						sellerReplyService.save(srp);
						session.removeAttribute("srp");

					}
				}
			}
			List<Sellerreply> sellerRp = sellerReplyService.findByMemberId((Long) loginId);
			for (Sellerreply srp : sellerRp) {
				if (srp.getRestate().equals("Y")) {
					if (srp.getReply().getProductBoard().getPbstate().equals("Y")) {
						System.out.println(srp.getReply().getMember().getId());

						if (srp.getReply().getMember().getId().equals((Long) loginId)) {
							System.out.println("세번째");
							sreplyIdList.add(srp.getReply().getProductBoard().getId());

						}

					}

				}
			}
			if (sreplyIdList.size() > 0) {
				session.setAttribute("srp", sreplyIdList);
			} else {
				session.removeAttribute("srp");
			}
		}

		List<Reply> rp = productService.findReplyAll();
		List<Reply> reply = new ArrayList<>();
		for (Reply rep : rp) {
			String sid = rep.getProductBoard().getProduct().getSeller().getSid();
			if (sid.equals((String) session.getAttribute("loginSid"))) {

				if (rep.getProductBoard().getPbstate().equals("Y")) {

					if (rep.getRestate().equals("Y")) {

						reply.add(rep);
						session.setAttribute("sa", "게시글에 댓글이 달렸습니다.");

					}
				}
			}
		}
		model.addAttribute("rp", reply);
		return "product/boardDetail";
	}

	@PostMapping("/replyWrite")
	public String replyWrite(Reply reply) {
		System.out.println("댓글작성 컨트롤러");
		Object loginId = session.getAttribute("loginId");
		if (loginId == null) { // 로그인이 되어 있지 않은 경우
			return "redirect:/members/login"; // 로그인 페이지로 이동
		}

		productService.saveReply((Long) loginId, reply);
		return "redirect:/product/detail/" + reply.getProductBoard().getId();
	}

	@PostMapping("/search")
	public String searchProduct(@RequestParam("search") String search, Model model) {
		System.out.println("search 컨트롤러");
		List<ProductBoard> pb = productService.findSearch(search);
		System.out.println(pb);
		model.addAttribute("pb", pb);
		return "all";
	}

	@GetMapping("/getReply")
	@ResponseBody
	public List<Reply> productBoardReplys(@RequestParam("id") String id) {
		Long pdb_id = Long.parseLong(id);
		List<Reply> replyList = productService.findReply(pdb_id);
		return replyList;
	}

	@GetMapping("/myBoardList")
	public String myBoardList(Model model) {

		String loginSid = (String) session.getAttribute("loginSid");
		if (loginSid != null) {
			List<ProductBoard> prBoard = productService.productMyList(loginSid);
			for (ProductBoard pb : prBoard) {
				int sea = pb.getProduct().getSea(); // 상품 개수
				double count = orderService.findSea(pb.getProduct().getId()); //
				pb.getProduct().setSea(sea - (int) count);

			}

			model.addAttribute("sellerBoard", prBoard);
			List<Reply> rp = productService.findReplyAll();
			List<Reply> reply = new ArrayList<>();
			for (Reply rep : rp) {
				String sid = rep.getProductBoard().getProduct().getSeller().getSid();
				if (sid.equals((String) session.getAttribute("loginSid"))) {

					if (rep.getProductBoard().getPbstate().equals("Y")) {

						if (rep.getRestate().equals("Y")) {

							reply.add(rep);
							session.setAttribute("sa", "게시글에 댓글이 달렸습니다.");

						}
					}
				}
			}
			model.addAttribute("rp", reply);
			return "seller/sellerBoardList";
		}

		return "seller/sellerLoginForm";

	}

	@GetMapping("/edit")
	public String editProduct(@RequestParam("no") Long productBoardId, Model model) {
		// 상품 ID로 상품 정보를 조회
		Optional<ProductBoard> productOpt = productService.findProductBoard(productBoardId);
		ProductBoard productBoard = productService.boardDetail(productBoardId);
		double count = orderService.findSea(productBoard.getProduct().getId()); // 주문된 수량 조회
		double total = (double) productBoard.getProduct().getSea() - count; // 남은 재고 계산
		model.addAttribute("total", (int) total); // 남은 재고를 모델에 추가

		// 모델에 상품 정보를 추가하여 수정 페이지로 전달
		model.addAttribute("productboard", productOpt.get());
		return "product/edit"; // 수정 페이지로 이동

	}

	@PostMapping("/update")
	public String updateProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes,@RequestParam("mfile")MultipartFile  mfile) throws IllegalStateException, IOException {
		// 상품 정보를 업데이트하는 로직

		System.out.println("재고 : " + product.getSea());
		double count = orderService.findSea(product.getId());
		product.setSea(product.getSea() + (int) count);

		productService.updateProduct(product, mfile);
		redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 수정되었습니다.");
		return "redirect:/product/myBoardList"; // 수정 후 상품 목록 페이지로 리디렉션
	}

}
