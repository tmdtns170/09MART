package com.iciafinally.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iciafinally.controller.ProductForm;
import com.iciafinally.domain.Member;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.domain.Reply;
import com.iciafinally.domain.Seller;
import com.iciafinally.repository.MemberRepository;
import com.iciafinally.repository.ProductBoardRepository;
import com.iciafinally.repository.ProductRepository;
import com.iciafinally.repository.ReplyRepository;
import com.iciafinally.repository.SellerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductBoardRepository productBoardRepository;

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ReplyRepository replyRepository;
	@Autowired
	private SellerRepository sellerRepository;

	public void registProduct(Product product) {
		System.out.println("물품등록 서비스 호출");
		productRepository.save(product);

	}

	public void registBoard(ProductForm productForm, Long seller_id) throws IllegalStateException, IOException {
		System.out.println("판매보드 등록 서비스 호출");
		Product product = productRepository.findProductId(seller_id);
		product.setPstate("y");
		Product prduct1 = productRepository.save(product);
		ProductBoard producBoard = ProductBoard.createBoard(productForm);
		producBoard.setProduct(prduct1);
		MultipartFile boardFiles1 = productForm.getMainFile();
		String fileNameList1 = null;
		if (!boardFiles1.isEmpty()) {
//			String savePath = "I:\\bootWorkspace\\finallyProject\\src\\main\\resources\\static\\productImg";
			String savePath = "D:\\spring-work\\finallyProject\\src\\main\\resources\\static\\mainProduct";
//			String savePath = "C:\\Users\\user\\Desktop\\finallyProject\\src\\main\\resources\\static\\mainProduct";
			/* 파일명 생성 */
			String bfileName = UUID.randomUUID().toString();

			String originFilename = boardFiles1.getOriginalFilename(); // 원본 파일명
			int suffixIndex = originFilename.lastIndexOf("."); // 확장자의 시작 위치
			String suffixStr = originFilename.substring(suffixIndex); // 파일 확장자

			bfileName = bfileName + suffixStr;
			/* 파일 저장 */
			boardFiles1.transferTo(new File(savePath, bfileName));
			if (fileNameList1 == null) {
				fileNameList1 = "/mainProduct/" + bfileName;
			}
		}
		producBoard.setMainFile(fileNameList1);

		MultipartFile[] boardFiles = productForm.getBoardFile();
		String fileNameList = null;

		for (MultipartFile boardFile : boardFiles) {

			if (!boardFile.isEmpty()) {
//				String savePath = "I:\\bootWorkspace\\finallyProject\\src\\main\\resources\\static\\productImg";
				String savePath = "D:\\spring-work\\finallyProject\\src\\main\\resources\\static\\productImg";
//				String savePath = "C:\\Users\\user\\Desktop\\finallyProject\\src\\main\\resources\\static\\productImg";
				/* 파일명 생성 */
				String bfileName = UUID.randomUUID().toString();

				String originFilename = boardFile.getOriginalFilename(); // 원본 파일명
				int suffixIndex = originFilename.lastIndexOf("."); // 확장자의 시작 위치
				String suffixStr = originFilename.substring(suffixIndex); // 파일 확장자

				bfileName = bfileName + suffixStr;
				/* 파일 저장 */
				boardFile.transferTo(new File(savePath, bfileName));
				if (fileNameList == null) {
					fileNameList = "/productImg/" + bfileName;
				} else {
					fileNameList += ",/productImg/" + bfileName;
				}
			}
		}
		producBoard.setPfilename(fileNameList);
		productBoardRepository.save(producBoard);

	}

	public List<ProductBoard> productList() {
		System.out.println("프로덕트 리스트 서비스 호출");

		return productBoardRepository.findByPbstate();
	}

	public ProductBoard boardDetail(Long pdb_id) {
		System.out.println("프로덕트 디테일 서비스 호출");
		ProductBoard productBoard = productBoardRepository.findById(pdb_id).orElse(null);

		int phits = productBoard.getPhits() + 1;
		productBoard.setPhits(phits);
		productBoardRepository.save(productBoard);
		return productBoard;
	}

	public void saveReply(Long loginId, Reply reply) {
		ProductBoard productBoard = productBoardRepository.findById(reply.getProduct_id()).orElse(null);
		Member member = memberRepository.findById(loginId).orElse(null);
		reply.setMember(member);
		reply.setProductBoard(productBoard);
		reply.setRedate(LocalDateTime.now());
		replyRepository.save(reply);

	}

	public void updateReply(Reply reply) {

		replyRepository.save(reply);

	}

	public List<Reply> findReply(Long pdb_id) {

		return replyRepository.findByproductBoardIdOrderByRedateDesc(pdb_id);
	}

	public List<ProductBoard> productMyList(String loginSid) {
		Seller seller = sellerRepository.findBySid(loginSid);

		List<ProductBoard> productBoard = productBoardRepository.findByMyBoard(seller.getId());
		return productBoard;
	}

	public List<ProductBoard> findBoardAll() {

		return productBoardRepository.findAll();
	}

	public void updatePbstate(ProductBoard productBoard) {
		productBoardRepository.save(productBoard);

	}

	public Product findById(Long productId) {
		return productRepository.findById(productId).orElse(null);

	}

	public List<ProductBoard> findSearch(String search) {
		System.out.println("search 서비스 호출");
		System.out.println(search);
		List<ProductBoard> pb = productBoardRepository.findByPtitle(search);
		List<Product> product = productRepository.findByProductname(search);
		List<ProductBoard> fpb = new ArrayList<>();

		for (ProductBoard pBoard : pb) {
			if (pBoard.getPbstate().equals("Y")) {
				fpb.add(pBoard);
			}
			boolean check = true;
			int index = 0;
			for (int i = 0; i < product.size(); i++) {

				if (pBoard.getProduct().getId() == product.get(i).getId()) {
					check = false;
				} else {
					index = i;
				}

			}
			if (check) {
				ProductBoard productBoard = productBoardRepository.findByProductId(product.get(index).getId());
				if (productBoard.getPbstate().equals("Y")) {
					fpb.add(productBoard);
				}
			}

		}
		System.out.println("fpb : " + fpb);
		return fpb;
	}

	public List<Reply> findReplyAll() {

		return replyRepository.findAll();
	}

	public Reply findMemberReply(Long rep_id) {

		return replyRepository.findById(rep_id).orElse(null);
	}

	/*
	 * // 정렬 방식을 선택하는 메서드 public List<ProductBoard> findAllSortedByDate1(String
	 * sortOrder) { Sort sort = sortOrder.equals("asc") ?
	 * Sort.by(Sort.Direction.ASC, "pdate") : Sort.by(Sort.Direction.DESC, "pdate");
	 * return productRepository.findAll(sort); }
	 */

	public List<ProductBoard> getAllProductBoards(String sortOrder) {
		List<ProductBoard> productBoards = productBoardRepository.findAll();
		// 판매 상태가 'Y'인 상품만 필터링
		productBoards = productBoards.stream().filter(board -> "Y".equals(board.getPbstate()))
				.collect(Collectors.toList());
		if ("latest".equals(sortOrder)) {
			productBoards.sort(Comparator.comparing(ProductBoard::getPdate).reversed());
		} else if ("oldest".equals(sortOrder)) {
			productBoards.sort(Comparator.comparing(ProductBoard::getPdate));
		}
		return productBoards;
	}

	public Product save(Product product) {
		return productRepository.save(product);

	}

	public List<ProductBoard> productListPhits() {

		return productBoardRepository.findByOrderByPhits();
	}

	public ProductBoard findByProductId(Long id) {

		return productBoardRepository.findByProductId(id);
	}

	public void updateProduct(Product product, MultipartFile mfile) throws IllegalStateException, IOException {
		// 상품 ID로 기존 상품을 조회
		Optional<Product> existingProductOpt = productRepository.findById(product.getId());
		
		if (mfile != null && !mfile.isEmpty()) {
			ProductBoard pb = productBoardRepository.findByProductId(product.getId());
			MultipartFile boardFiles1 = mfile;
			String fileNameList1 = null;
			if (!boardFiles1.isEmpty()) {
//	   				String savePath = "I:\\bootWorkspace\\finallyProject\\src\\main\\resources\\static\\productImg";
				String savePath = "C:\\Users\\user\\Desktop\\finallyProject\\src\\main\\resources\\static\\mainProduct";
//	   				String savePath = "C:\\Users\\user\\Desktop\\finallyProject\\src\\main\\resources\\static\\mainProduct";
				/* 파일명 생성 */
				String bfileName = UUID.randomUUID().toString();

				String originFilename = boardFiles1.getOriginalFilename(); // 원본 파일명
				int suffixIndex = originFilename.lastIndexOf("."); // 확장자의 시작 위치
				String suffixStr = originFilename.substring(suffixIndex); // 파일 확장자

				bfileName = bfileName + suffixStr;
				/* 파일 저장 */
				boardFiles1.transferTo(new File(savePath, bfileName));
				if (fileNameList1 == null) {
					fileNameList1 = "/mainProduct/" + bfileName;
				}
			}
			pb.setMainFile(fileNameList1);
			productBoardRepository.save(pb);
		}

		if (existingProductOpt.isPresent()) {
			Product existingProduct = existingProductOpt.get();

			// 기존 상품의 필드를 새로운 값으로 업데이트

			existingProduct.setProductname(product.getProductname());
			existingProduct.setSprice(product.getSprice());
			existingProduct.setSea(product.getSea());
			existingProduct.setPstate(product.getPstate());

			// 이미지 파일 처리 로직 추가 (필요한 경우)

			// 업데이트된 상품 저장
			productRepository.save(existingProduct);
		} else {
			throw new EntityNotFoundException("해당 상품을 찾을 수 없습니다.");
		}

	}

	public Optional<ProductBoard> findProductBoard(Long productBoardId) {
		// TODO Auto-generated method stub
		return productBoardRepository.findById(productBoardId);
	}

}
