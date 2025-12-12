package com.iciafinally.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iciafinally.domain.Member;
import com.iciafinally.domain.ProductBoard;
import com.iciafinally.service.ProductBoardService;
import com.iciafinally.service.ProductService;

@Controller
public class ProductBoardController {
	@Autowired
    private ProductBoardService productBoardService; // 로직 처리를 위한 서비스
	  @Autowired
	    private ProductService productService;

    @GetMapping("/productboard/{id}")
    @ResponseBody
    public String deleteProductBoard(@PathVariable("id") Long id) {
    	System.out.println("deleteProductBoard");
        ProductBoard productBoard = productBoardService.findById(id); // ID로 게시판 조회
        productBoard.deleteBoard(); // 상태를 'N'으로 변경
        productBoardService.save(productBoard); // 변경된 엔티티 저장
     // 구매자 목록 가져오기
        List<Member> members = productBoardService.getBuyersByProductBoardId(id);
        StringBuilder responseMessage = new StringBuilder("게시물이 삭제되었습니다.");
        if (!members.isEmpty()) {
            responseMessage.append(" 구매자 목록: ");
            System.out.println("1");
            for (Member member : members) {
                responseMessage.append(member.getMname()).append(", "); // 구매자 이름 추가
                System.out.println("2");
            }
            // 마지막 쉼표와 공백 제거
            responseMessage.setLength(responseMessage.length() - 2);
        }else{
        	responseMessage.append("구매자가 없습니다.");
        }
        System.out.println(members);

        return responseMessage.toString(); // 성공 메시지 반환
    }
  
    @GetMapping("/productboard")
    @ResponseBody
    public String getProductBoards(@RequestParam(name = "sortOrder", required = false) String sortOrder, Model model) {
        List<ProductBoard> productBoard = productService.getAllProductBoards(sortOrder);
        // HTML 조각 생성
        StringBuilder html = new StringBuilder();
        
        if (productBoard.isEmpty()) {
        	
        	
            html.append("<li>등록된 상품이 없습니다.</li>");
   
        } else {
            for (ProductBoard item : productBoard) {
                // 이미지 URL 설정
                String imageUrl;
                if (item.getMainFile() == null || item.getMainFile().isEmpty()) {
                    imageUrl = "https://search.pstatic.net/common/?src=http%3A%2F%2Fcafefiles.naver.net%2FMjAxOTAzMTlfNTEg%2FMDAxNTUyOTYwMTI1OTcx.6TFsqurSPyGEO66jIQy43fwvV49XAau2JsEqM_SwHkgg.49rUM2ZDiDVraEFOrA8WvO4Wqn6wP-pDGfMQKWQOWnQg.JPEG.smdyym1985%2F2.jpg&type=a340";
                } else {
                	  imageUrl =  item.getMainFile();// /productImg/025b4396-f88f-4574-b7ae-a4524f45f0be.jpeg
                }
                // 이미지 URL 확인
                System.out.println("Image URL: " + imageUrl);
                
             // HTML 조각 생성
                html.append("<li class='list-group-item d-flex justify-content-between align-items-center'>")
                //<a th:href="@{/product/detail/{id}(id=${item.id})}" style="text-decoration: none; color: inherit; width: 100%;">
                    .append("<a href='/product/detail/"+item.getId()+"'").append(item.getId()).append(" style='text-decoration: none; color: inherit; width: 100%;' >")
                	.append("<div class='row'>")
                    .append("<div class='col-md-4'>")
                    .append("<img src='").append(imageUrl).append("' class='img-fluid rounded product-image' alt='상품 이미지'> ")
                    .append("</div>")
                    .append("<div class='col-md-1 d-flex align-items-center justify-content-center'>")
                    .append("<div class='vr' style='height: 100%; width: 1px; background-color: black;'></div>")
                    .append("</div>")
                    .append("<div class='col-md-6' style='margin-top: 20px; margin-left: 20px;'>")
                    .append("<p>").append(item.getPcontents()).append("</p>")
                    .append("<ul>")
                    .append("<li>상품명: ").append(item.getPtitle()).append("</li>")
                    .append("<li>가격: ").append(item.getPhits()).append("원</li>")
                    .append("<li>조회수: ").append(item.getPhits()).append("회</li>")
                    .append("<li>작성일: ").append(item.getPdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</li>")
                    .append("</ul>")
                    .append("</div>")
                    .append("</div>")
                    //
                    
                    .append("</a>")
                    .append("<button class='delete-btn' data-id='").append(item.getId()).append("' style='background-color: black; color: white; padding: 5px 10px; border: none; cursor: pointer; position: absolute; right: 20px; bottom: 10px;'>삭제</button>")
                    .append("</li>");
            }
        }
        return html.toString(); // 생성된 HTML 문자열 반환
    }

//    @GetMapping("/productboard/{id}")
//    @ResponseBody
//    public String deleteProductBoard(@PathVariable("id") Long id) {
//    	System.out.println("deleteProductBoard");
//        ProductBoard productBoard = productBoardService.findById(id); // ID로 게시판 조회
//        productBoard.deleteBoard(); // 상태를 'N'으로 변경
//        productBoardService.save(productBoard); // 변경된 엔티티 저장
//        return "게시물이 삭제되었습니다."; // 성공 메시지 반환
//    }
  
}