package com.iciafinally.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iciafinally.domain.Comments;
import com.iciafinally.domain.Product;
import com.iciafinally.service.CommentService;

@Controller
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	/* 글 목록 페이지 이동 요청 */
	@GetMapping("/SellList")
	public String sellList(Model model) {
		
		/* 전체 글 목록 조회 */
		List<Product> selllist = commentService.findAll();

		model.addAttribute("selllist", selllist);
		return "sellList";
	}
	  // 댓글 목록 조회 기능 
    @GetMapping("/view")
    public String viewComments(@RequestParam("productId") Long productId, Model model) {
    	
    	
    	List<Comments> comments = commentService.CommentView(productId);
    	

    	model.addAttribute("comments", comments);
    	
		return "commentList";
    	
    }
}