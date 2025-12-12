package com.iciafinally.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.domain.BookMark;
import com.iciafinally.domain.Member;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.repository.BookMarkRepository;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.repository.ProductBoardRepository;
import com.iciafinally.repository.ProductRepository;

import jakarta.transaction.Transactional;


@Service
public class BookMarkService {

	@Autowired
	private BookMarkRepository bookMarkRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductBoardRepository productBoardRepository;

	// 즐겨찾기 추가
	public void addBookMark(Long memberId, Long productId) {
		System.out.println("서비스 실행");
		if (!bookMarkRepository.existsByMemberIdAndProductId(memberId, productId)) {
			Member member = memberRepository.findById(memberId).orElse(null);
			ProductBoard product = productBoardRepository.findById(productId).orElse(null);
			
			   // Additional debugging
	        System.out.println("Member: " + member);
	        System.out.println("Product: " + product);
			
			
			
			if (member != null && product != null) {
				BookMark bookmark = new BookMark();
				bookmark.setMember(member);
				bookmark.setProduct(product);
				
				bookMarkRepository.save(bookmark);
			} else {
                System.out.println("Member or Product is null");
            }
        } else {
            System.out.println("Bookmark already exists");
		}
	}

	// 즐겨찾기 삭제 기능
	@Transactional
	public void removeBookMark(Long memberId, Long productId) {
		System.out.println("즐찾 삭제 실행");
		bookMarkRepository.deleteByMemberIdAndProductId(memberId, productId);
	}

	// 회원 id로 즐겨찾기 항목 조회
	public List<BookMark> getFavoritesByMember(Long memberId) {

		return bookMarkRepository.findByMemberId(memberId);
	}

	public List<BookMark> getAllBookmarks() {

		return bookMarkRepository.findAll();
	}
	
	
	public List<BookMark>findByPorductId(Long Id){
		
		return bookMarkRepository.findByProductId(Id);
	}

	public void removeWish(Long Id) {
		System.out.println("removewishService");
		bookMarkRepository.deleteById(Id);
		
	}



}