package com.iciafinally.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iciafinally.domain.BookMark;
import com.iciafinally.service.BookMarkService;
import com.iciafinally.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/BookMark")
public class BookMarkController {

	@Autowired
	private BookMarkService bookMarkService;
	@Autowired
	private HttpSession session;
	@Autowired
	private ProductService productService;

	@GetMapping("/list")
	public String showBookmarkPage(Model model, RedirectAttributes ra) {
		Long memberId = (Long) session.getAttribute("loginId");
		// Debugging output
		if (memberId != null) {
			System.out.println("MemberId: " + memberId);

			List<BookMark> bookmarks = bookMarkService.getFavoritesByMember(memberId);
			List<BookMark> bookmark = new ArrayList<>();
			for (BookMark bm : bookmarks) {
				if (bm.getProduct().getPbstate().equals("Y")) {
					bookmark.add(bm);
				}
			}
			model.addAttribute("bookmarks", bookmark);

			return "Bookmark/BookmarkList";
		}
		ra.addFlashAttribute("msg", "로그인후 이용가능합니다");
		return "redirect:/";
	}

///Bookmark/add
// 즐겨찾기 추가 처리
	@PostMapping("/add")
	public String addBookMark(@RequestParam("productId") String productId, @RequestParam("boardId") String boardId,
			RedirectAttributes ra) {

		Long memberId = (Long) session.getAttribute("loginId");

		// Debugging output
		System.out.println("MemberId: " + memberId);
		System.out.println("ProductId (String): " + productId);

		if (memberId != null) {
			bookMarkService.addBookMark(memberId, Long.parseLong(boardId));
			return "redirect:/product/detail/" + boardId; // 추가 후 목록 페이지로 리디렉션
		}
		ra.addFlashAttribute("msg", "로그인후 이용가능합니다");
		return "redirect:/"; // 추가 후 목록 페이지로 리디렉션
	}

// 즐겨찾기 삭제
	@GetMapping("/remove")
	public String removeBookMark(@RequestParam("productId") String productId) {

		Long memberId = (Long) session.getAttribute("loginId");

		// Debugging output
		System.out.println("MemberId: " + memberId);
		System.out.println("ProductId (String): " + productId);
		if (memberId != null) {

			bookMarkService.removeBookMark(memberId, Long.parseLong(productId));
			return "redirect:/BookMark/list"; // 삭제 후 목록 페이지로 리디렉션
		}
		/*
		 * // 회원 id로 즐겨찾기 항목 조회
		 * 
		 * @GetMapping("/list") public String getFavoritesByMember(@RequestParam Long
		 * memberId, Model model) { List<BookMark> bookmarks =
		 * bookMarkService.getFavoritesByMember(memberId);
		 * model.addAttribute("bookmarks", bookmarks); return "bookmarkList"; //
		 * bookmarkList.html 뷰를 반환 }
		 */
		return "redirect:/";

	}

	@PostMapping("/remove")
	public String removeBookMark1(@RequestParam("productId") String productId) {

		Long memberId = (Long) session.getAttribute("loginId");

		// Debugging output
		System.out.println("MemberId: " + memberId);
		System.out.println("ProductId (String): " + productId);
		if (memberId != null) {

			bookMarkService.removeBookMark(memberId, Long.parseLong(productId));
			return "redirect:/product/detail/" + productId;
		}
		/*
		 * // 회원 id로 즐겨찾기 항목 조회
		 * 
		 * @GetMapping("/list") public String getFavoritesByMember(@RequestParam Long
		 * memberId, Model model) { List<BookMark> bookmarks =
		 * bookMarkService.getFavoritesByMember(memberId);
		 * model.addAttribute("bookmarks", bookmarks); return "bookmarkList"; //
		 * bookmarkList.html 뷰를 반환 }
		 */
		return "redirect:/members/login";

	}

	@GetMapping("/addWish")
	@ResponseBody
	public String addWishList(@RequestParam("boardId") String boardId, RedirectAttributes ra) {

		Long memberId = (Long) session.getAttribute("loginId");
		List<BookMark> bmk = new ArrayList<>();
		// Debugging output
		System.out.println("MemberId: " + memberId);

		if (memberId != null) {
			
			bookMarkService.addBookMark(memberId, Long.parseLong(boardId));
			List<BookMark> bookMark = bookMarkService.getFavoritesByMember(memberId);
			for (BookMark bMark : bookMark) {
				if (bMark.getProduct().getPbstate().equals("Y")) {
					bmk.add(bMark);
				}
			}
			if (bmk.size() > 0) {

				session.setAttribute("bookMark", bmk);
			}
			return "N"; // 추가 후 목록 페이지로 리디렉션
		}
		ra.addFlashAttribute("msg", "로그인후 이용가능합니다");
		return "Y"; 
	}

	@GetMapping("/removeWish")
	@ResponseBody
	public String removeWishList(@RequestParam("bookMarkId") String bookMarkId) {

		Long memberId = (Long) session.getAttribute("loginId");
		List<BookMark> bmk = new ArrayList<>();
		// Debugging output
		System.out.println("MemberId: " + memberId);

		if (memberId != null) {

			bookMarkService.removeWish(Long.parseLong(bookMarkId));
			List<BookMark> bookMark = bookMarkService.getFavoritesByMember(memberId);

			for (BookMark bMark : bookMark) {
				if (bMark.getProduct().getPbstate().equals("Y")) {
					bmk.add(bMark);
				}
			}
			if (bmk.size() > 0) {

				session.setAttribute("bookMark", bmk);
			}

			return "Y";
		}

		return "N";
	}

}