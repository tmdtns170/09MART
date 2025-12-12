package com.iciafinally.domain;
import java.util.Date;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 노드  Class
 */

@Getter @Setter @ToString
public class Node {
    @Id
    private Long id;//노드id

    private  Long orderid;
    private String ostate;
    private String pickup;
   
    private String pname; //픽업 물품 이름
    private Long pid;//픽업 물품 아이디
    
    
    private String name;
    private String address;
    private String phone;
    private Double x;//경도
    private Double y;//위도
    private Date regDt;//등록일시
    private Date modDt;//수정일시
   /**
    * 노드id 조회
    * @return id
    */

}