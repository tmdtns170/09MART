package com.iciafinally.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iciafinally.controller.SelletProductDto;
import com.iciafinally.domain.Delivery;
import com.iciafinally.domain.Driver;
import com.iciafinally.domain.DriverRoute;
import com.iciafinally.domain.JsonResult;
import com.iciafinally.domain.Member;
import com.iciafinally.domain.Node;
import com.iciafinally.domain.NodeCost;
import com.iciafinally.domain.NodeCostParam;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.domain.Seller;
import com.iciafinally.domain.VrpResult;
import com.iciafinally.domain.VrpVehicleRoute;
import com.iciafinally.kakao.util.KakaoDirections;
import com.iciafinally.kakao.util.KakaoDirections.Route;
import com.iciafinally.kakao.util.KakaoDirections.Route.Section;
import com.iciafinally.kakao.util.KakaoDirections.Route.Section.Road;
import com.iciafinally.kakao.util.KakaoDirections.Route.Summary;
import com.iciafinally.kakao.util.KakaoDirections.Route.Summary.Fare;
import com.iciafinally.mapline.util.KakaoApiUtil;
import com.iciafinally.mapline.util.KakaoApiUtil.Point;
import com.iciafinally.repository.DeliveryRepository;
import com.iciafinally.repository.DriverRepository;
import com.iciafinally.repository.DriverRouteRepository;
import com.iciafinally.repository.OrderRepository;
import com.iciafinally.repository.ProductRepository;

@Service
public class DriverService {

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private DriverRouteRepository driverRouteRepository;

	@Autowired
	private ProductRepository productRepository;

	public void registDriver(Driver driver) {
		driverRepository.save(driver);
	}

	public Driver findByDid(String did) {
		Driver driver = driverRepository.findByDid(did);
		return driver;
	}

	public Driver findBympw(String dpw) {
		Driver driver = driverRepository.findByDpw(dpw);
		return driver;
	}

	public List<Driver> findAll() {
		/* SELECT * FROM Driver */
		return driverRepository.findAll();
	}

	public Driver findByDidAndDpw(String did, String dpw) {
		Driver driver = driverRepository.findByDidAndDpw(did, dpw);

		return driver;
	}

	public void registDelivery(List<Product> productList) {
		System.out.println("메인 서비스 시작");
		Map<String, List<Delivery>> deMap = new HashMap<>(); // {1 : [0,1,2,3,]}, {2 : [0,1,2,3,]}, {3 : [0,1,2,3,]}
		List<Driver>nowaitDriverOkList = new ArrayList<>();
		for (int i = 0; i < productList.size(); i++) {
			List<Orders> orderList = orderRepository.findByProductId(productList.get(i).getId());// 해당 상품을 주문한 주문내역
			List<Driver> nowaitDriverList = driverRepository.findByNoWaitDriverOrderById(productList.get(i).getSdate()); // 상품배달일에 배송하는 기사 목록
			List<Driver> waitDriverList = new ArrayList<>(); // 배송가능한 기사를 저장할 목록
			
			for (int n = 0; n < nowaitDriverList.size(); n++) {
				System.out.println("nowaitDriverList.get(n).getId() : " + nowaitDriverList.get(n).getId());
				List<Delivery> deliveryListBySdate 
							= deliveryRepository.findByDriverAndDdate(nowaitDriverList.get(n),productList.get(i).getSdate() );// 상품배달일에 일하는 기사의 배송목록
																										
				Double totalWeight = 0.0; // // 상품배달일에 배송하는 무게 총합
				for (int rd = 0; rd < deliveryListBySdate.size(); rd++) {
					totalWeight += deliveryListBySdate.get(rd).getOrders().getBea() * deliveryListBySdate.get(rd).getOrders().getProduct().getWeight();// 상품배달일에
				}
				
				Double driverWeight = nowaitDriverList.get(n).getDweight();// 상품배달일에 일하는 기사가 배달할 수 있는 총 무게
				driverWeight -= totalWeight;
				if (driverWeight > 0) { // 배달을 더 할 공간이 남아있으면 쉬는 기사 리스트에 넣기
					waitDriverList.add(nowaitDriverList.get(n));
					nowaitDriverOkList.add(nowaitDriverList.get(n));
				}

			}
			waitDriverList.addAll(driverRepository.findByWaitDriverOrderById(productList.get(i).getSdate())); // 쉬는 기사들 추가
	
			for (int d = 0; d < waitDriverList.size(); d++) { // [ 0 ~ 3:여유공간 있는 기사, 해당일에 일이 없는 기사 4~10]
				List<Delivery> deElement = new ArrayList<>();
				double driverWeight = waitDriverList.get(d).getDweight(); // 기사가 적재 가능한 총 무게
				
				// 배송일에 배송하는 상품의 무게 총합
				List<Delivery> deliveryListBySdate = deliveryRepository.findByDriverAndDdate(waitDriverList.get(d), productList.get(i).getSdate());
				Double totalWeight = 0.0; // // 상품배달일에 배송하는 무게 총합
				for (int rd = 0; rd < deliveryListBySdate.size(); rd++) {
					totalWeight += deliveryListBySdate.get(rd).getOrders().getBea() * deliveryListBySdate.get(rd).getOrders().getProduct().getWeight();// 상품배달일에
				}
				driverWeight -= totalWeight; // 
				System.out.println("driverWeight : " + driverWeight );
				for (int o = 0; o < orderList.size(); o++) {
					if (orderList.get(o).getOstate().equals("N")) {
						Delivery delivery = new Delivery();
						System.out.println("productTotalWeight : "+orderList.get(o).getBea() * productList.get(i).getWeight());
						driverWeight -= orderList.get(o).getBea() * productList.get(i).getWeight();
						if (driverWeight < 0) {
							System.out.println("if");
							driverWeight += orderList.get(o).getBea() * productList.get(i).getWeight();
						} else {
							System.out.println("else");
							orderList.get(o).setOstate("Y");
							orderRepository.save(orderList.get(o));
							delivery.setDdate(productList.get(i).getSdate());
							delivery.setDriver(waitDriverList.get(d));
							delivery.setOrders(orderList.get(o));
							deliveryRepository.save(delivery);
							deElement.add(delivery);
							deMap.put(waitDriverList.get(d).getId().toString(), deElement);
						}
					}
				}
			}
		} //
		
		createNode(deMap);
	}

	private void createNode(Map<String, List<Delivery>> deMap) { // {1 : [0,1,2,3,]}, {2 : [0,1,2,3,]}, {3 : [0,1,2,3,]}
		System.out.println("createNode실행");

		List<Product> productList = productRepository.findAll();

		System.out.println(deMap);
		System.out.println(deMap.keySet());
		for (String key : deMap.keySet()) { // key 드라이버 아이디
			List<Member> memberList = new ArrayList<>();
			List<Seller> sellerList = new ArrayList<>();


			Driver driver = driverRepository.findById(Long.parseLong(key)).orElse(null);
			String driverName = driver.getDname();
			List<Delivery> deliveryList = deliveryRepository.findByDriverIdOrderByDdate(Long.parseLong(key));
			
			Map<LocalDate, List<Delivery> > deliveryMapByDate = new HashMap<>();
			
			for(Delivery delivery : deliveryList) {
				LocalDate ddate = delivery.getDdate();
				System.out.println("delivery_driverid : " + delivery.getDriver().getId());
				System.out.println("delivery_ddate : " + ddate);
				System.out.println("delivery_order_id : " + delivery.getOrders().getId());
				if(!deliveryMapByDate.containsKey(ddate)) {
					deliveryMapByDate.put(ddate, new ArrayList<>());
				};
				List<Delivery> ddateDeList = deliveryMapByDate.get(ddate);
				System.out.println("추가 전 size : " + ddateDeList.size());
				ddateDeList.add(delivery);
				System.out.println("추가 후 size : " + ddateDeList.size());
			}
			
			
			
			for( LocalDate localDate  : deliveryMapByDate.keySet()  ) {
				List<Node> pknodeList = new ArrayList<>(); // 픽업 장소
				List<Node> ptnodeList = new ArrayList<>(); // 배송 장소
				System.out.println("key_date : " + localDate);
				List<Delivery> deList = deliveryMapByDate.get(localDate);
				
				System.out.println("======de=========");
				System.out.println(deList.size());
				System.out.println("=======de========");
				Map<String, List<SelletProductDto>> productBySeller = new HashMap<>();
				Map<String, List<SelletProductDto>> productByMember = new HashMap<>();
	
				for (Delivery delivery : deList) {
	
					Node member_Node = new Node();
					member_Node.setId(delivery.getOrders().getMember().getId() + 1111L);
					member_Node.setOrderid(delivery.getOrders().getId());
					member_Node.setOstate(delivery.getOrders().getOstate());
					member_Node.setName(delivery.getOrders().getMember().getMname());
					member_Node.setAddress(delivery.getOrders().getMember().getRoadAddress());
					member_Node.setPhone(delivery.getOrders().getMember().getMphone());
					member_Node.setX(delivery.getOrders().getMember().getX());
					member_Node.setY(delivery.getOrders().getMember().getY());
					ptnodeList.add(member_Node);
					System.out.println("dddddd"+delivery.getOrders().getId());
					
					if (productByMember.get(member_Node.getId().toString()) == null) {
						List<SelletProductDto> prList = new ArrayList<>();
						productByMember.put(member_Node.getId().toString(), prList);
					}
					
					SelletProductDto testmemberProduct = new SelletProductDto();
					testmemberProduct.setId(delivery.getOrders().getId().toString());
					List<SelletProductDto> membercheckList = productByMember.get(member_Node.getId().toString());
					boolean memberisConstains = false;
					for (SelletProductDto se : membercheckList) {
						if (se.getId().equals(testmemberProduct.getId())) {
							memberisConstains = true;
							break;
						}
	
					}
					if (!memberisConstains) {
						membercheckList.add(testmemberProduct);
					}
	
					// memberList.add( delivery.getOrders().getMember());
					Product seller_product = delivery.getOrders().getProduct();
					Seller seller = seller_product.getSeller();
					Node seller_Node = new Node();
					seller_Node.setPid(seller_product.getId());
					seller_Node.setPname(seller_product.getProductname());
					seller_Node.setPickup(seller_product.getPickup());
	
					seller_Node.setName(seller.getSname());
					seller_Node.setAddress(seller.getSaddress());
					seller_Node.setPhone(seller.getSphone());
					seller_Node.setX(seller.getX());
					seller_Node.setY(seller.getY());
					seller_Node.setId(seller.getId() + 2222L);
					pknodeList.add(seller_Node);
	
					
					if (productBySeller.get(seller_Node.getId().toString()) == null) {
						List<SelletProductDto> prList = new ArrayList<>();
						productBySeller.put(seller_Node.getId().toString(), prList);
					}
					SelletProductDto testProduct = new SelletProductDto();
					testProduct.setId(seller_product.getId().toString());
					testProduct.setProductName(seller_product.getProductname());
					List<SelletProductDto> checkList = productBySeller.get(seller_Node.getId().toString());
					boolean isConstains = false;
					for (SelletProductDto se : checkList) {
						if (se.getId().equals(testProduct.getId())) {
							isConstains = true;
							break;
						}
	
					}
					if (!isConstains) {
						checkList.add(testProduct);
					}
					
					
				}
	
				System.out.println(productBySeller);
	
				System.out.println("===============");
				System.out.println("pknodeList.size() : "+pknodeList.size());
				for(Node node : pknodeList) {
					System.out.print(node.getOrderid()+", ");
				}
				System.out.println();				
				System.out.println("ptnodeList.size() :  "+ptnodeList.size());
				
				for(Node node : ptnodeList) {
					System.out.print(node.getOrderid()+", ");
				}
				System.out.println();
				System.out.println("===============");
				// 픽업 장소 목록
	
				Node drnode = new Node();
				drnode.setName(driver.getDname());
	//			drnode.setAddress();
				drnode.setPhone(driver.getDphone());
				drnode.setX(driver.getX());
				drnode.setY(driver.getY());
				drnode.setId(driver.getId() + 3333L);
				pknodeList.addFirst(drnode);
				System.out.println("try catch 문 실행전");
	
				// lastIndex 객체 x,y,name, address >> Member
	
				try {
	
					System.out.println("pknodeList : " + pknodeList.size());
					JsonResult pkvreResult = deliveryByVrp(driverName, pknodeList);
					List<Node> pkVrpNodeList = (List<Node>) pkvreResult.getData().get("nodeList");
					System.out.println("pkVrpNodeList : " + pkVrpNodeList.size());
					/* int lastIndex=pkVrpNodeList.size()-1; */
	
					ptnodeList.addFirst(pkVrpNodeList.getLast());
	
					JsonResult ptvreResult = deliveryByVrp(driverName, ptnodeList);
					List<Node> ptVrpNodeList = (List<Node>) ptvreResult.getData().get("nodeList");
	
					Map<String, Object> pkdata = pkvreResult.getData();
					int totalDistance = (int) pkdata.get("totalDistance");
	
					int duration = (int) pkdata.get("totalDuration");
	
					List<Point> totalPathPointList = (List<Point>) pkdata.get("totalPathPointList");
	
					List<Node> vrpNodeList = (List<Node>) pkdata.get("nodeList");
					List<Node> vrpDuplicatenodelist = (List<Node>) pkdata.get("duplicatenodelist");
					System.out.println(vrpNodeList.size());
					System.out.println("============픽업 장소 목록===========");
					for (Node node : vrpNodeList) {
						System.out.println(node.getName());
					}
					System.out.println("============픽업 장소 목록===========");
					vrpNodeList.removeLast();
					System.out.println("after : " + vrpNodeList.size());
					Map<String, Object> ptdata = ptvreResult.getData();
	
					totalDistance += (int) ptdata.get("totalDistance");
	
					duration += (int) ptdata.get("totalDuration");
	
					totalPathPointList.addAll((List<Point>) ptdata.get("totalPathPointList"));
	
					vrpNodeList.addAll((List<Node>) ptdata.get("nodeList"));
	
					vrpDuplicatenodelist.addAll((List<Node>) ptdata.get("duplicatenodelist"));
					Gson gson = new Gson();
	
					String totalPathPointList_json = gson.toJson(totalPathPointList);
					System.out.println(totalPathPointList_json.length());
					String nodeList_json = gson.toJson(vrpNodeList);
					String vrpDuplicatenodelist_json = gson.toJson(vrpDuplicatenodelist);
					System.out.println(nodeList_json);
					
					DriverRoute driverRoute = driverRouteRepository.findByDridAndRtdate(driver.getDid(),deList.getFirst().getDdate()); // driverRoute 테이블에 담기
					if(driverRoute == null) {
						driverRoute = new DriverRoute();
					}
					
					driverRoute.setDrid(driver.getDid());
					driverRoute.setRtdate(deList.getFirst().getDdate());
					driverRoute.setDistance(totalDistance);
					driverRoute.setDuration(duration);
					driverRoute.setPathlist(totalPathPointList_json);
					driverRoute.setNodelist(nodeList_json);
					driverRoute.setProductBySellernodelist(gson.toJson(productBySeller));
					driverRoute.setProductByMembernodelist(gson.toJson(productByMember));
	
					System.out.println("driverRoute = ");
					System.out.println(driverRoute);
					driverRouteRepository.save(driverRoute);
	
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
	
			}
		}
	}

	private JsonResult deliveryByVrp(String driverName, List<Node> nodeList) throws IOException, InterruptedException {

		VrpService pkVrpService = new VrpService();
		Node firstNode = nodeList.get(0);
		String firstNodeId = String.valueOf(firstNode.getId());
		pkVrpService.addVehicle(driverName, firstNodeId);

		Map<String, Node> nodeMap = new HashMap<>();
		Map<String, Map<String, NodeCost>> nodeCostMap = new HashMap<>();

		for (Node node : nodeList) {
			String nodeId = String.valueOf(node.getId());
			// 화물 등록
			pkVrpService.addShipement(node.getName(), firstNodeId, nodeId);
			nodeMap.put(nodeId, node);
		}

		for (int i = 0; i < nodeList.size(); i++) {
			Node startNode = nodeList.get(i);
			for (int j = 0; j < nodeList.size(); j++) {
				Node endNode = nodeList.get(j);
				NodeCost nodeCost = getNodeCost(startNode, endNode);
				if (i == j) {
					continue;
				}
				if (nodeCost == null) {
					nodeCost = new NodeCost();
					nodeCost.setDistanceMeter(0l);
					nodeCost.setDurationSecond(0l);
				}
				Long distanceMeter = nodeCost.getDistanceMeter();
				Long durationSecond = nodeCost.getDurationSecond();
				String startNodeId = String.valueOf(startNode.getId());
				String endNodeId = String.valueOf(endNode.getId());

				// 비용 등록
				pkVrpService.addCost(startNodeId, endNodeId, durationSecond, distanceMeter);
				if (!nodeCostMap.containsKey(startNodeId)) {
					nodeCostMap.put(startNodeId, new HashMap<>());
				}
				nodeCostMap.get(startNodeId).put(endNodeId, nodeCost);
			}
		}
		List<Node> vrpNodeList = new ArrayList<>();

		VrpResult vrpResult = pkVrpService.getVrpResult();

		String prevLocationId = null;
		int vrpVehicleRouteCount = 0;
		int addCount = 0;
		for (VrpVehicleRoute vrpVehicleRoute : vrpResult.getVrpVehicleRouteList()) {
			vrpVehicleRouteCount++;

			String locationId = vrpVehicleRoute.getLocationId();
			if (prevLocationId == null) {
				prevLocationId = locationId;
			} else if (locationId.equals(prevLocationId)) {
				continue;
			}

			prevLocationId = locationId;
			vrpNodeList.add(nodeMap.get(locationId));
			addCount++;
		}

		System.out.println("===========vrpVehicleRouteCount=========");
		System.out.println(vrpVehicleRouteCount);
		System.out.println(addCount);
		System.out.println("===========vrpVehicleRouteCount=========");
		vrpNodeList.removeLast();

		int totalDistance = 0;
		int totalDuration = 0;
		List<Point> totalPathPointList = new ArrayList<>();
		for (int i = 1; i < vrpNodeList.size(); i++) {
			Node prev = vrpNodeList.get(i - 1);
			Node next = vrpNodeList.get(i);

			NodeCost nodeCost = nodeCostMap.get(String.valueOf(prev.getId())).get(String.valueOf(next.getId()));
			if (nodeCost == null) {
				continue;
			}

			totalDistance += nodeCost.getDistanceMeter();
			totalDuration += nodeCost.getDurationSecond();
			String pathJson = nodeCost.getPathJson();
			if (pathJson != null) {
				totalPathPointList.addAll(new ObjectMapper().readValue(pathJson, new TypeReference<List<Point>>() {
				}));
			}
		}
		List<Node> duplicatenodelist = new ArrayList<>();
		for (int a = 0; a < nodeList.size(); a++) {
			for (int b = 0; b < vrpNodeList.size(); b++) {
				if (!(nodeList.get(a).getId().equals(vrpNodeList.get(b).getId()))) {
					duplicatenodelist.add(nodeList.get(a));
					System.out.println("duplicatenodelistduplicatenodelistduplicatenodelist");
					System.out.println(duplicatenodelist.size());
				}
			}

		}

		JsonResult jsonResult = new JsonResult();
		jsonResult.addData("totalDistance", totalDistance);// 전체이동거리
		jsonResult.addData("totalDuration", totalDuration);// 전체이동시간
		jsonResult.addData("totalPathPointList", totalPathPointList);// 전체이동경로
		jsonResult.addData("nodeList", vrpNodeList);// 방문지목록
		jsonResult.addData("duplicatenodelist", duplicatenodelist);// 중복된 방문지목록
		return jsonResult;
	}

	private NodeCost getNodeCost(Node prev, Node next) throws IOException, InterruptedException {
		NodeCostParam nodeCostParam = new NodeCostParam();
		nodeCostParam.setStartNodeId(prev.getId());
		nodeCostParam.setEndNodeId(next.getId());
		NodeCost nodeCost = null;

		KakaoDirections kakaoDirections = KakaoApiUtil.getKakaoDirections(new Point(prev.getX(), prev.getY()),
				new Point(next.getX(), next.getY()));
		List<Route> routes = kakaoDirections.getRoutes();
		Route route = routes.get(0);
		List<Point> pathPointList = new ArrayList<Point>();
		List<Section> sections = route.getSections();

		if (sections == null) {
			// {"trans_id":"018e3d7f7526771d9332cb717909be8f","routes":[{"result_code":104,"result_msg":"출발지와
			// 도착지가 5 m 이내로 설정된 경우 경로를 탐색할 수 없음"}]}
			pathPointList.add(new Point(prev.getX(), prev.getY()));
			pathPointList.add(new Point(next.getX(), next.getY()));
			nodeCost = new NodeCost();
			nodeCost.setStartNodeId(prev.getId());// 시작노드id
			nodeCost.setEndNodeId(next.getId());// 종료노드id
			nodeCost.setDistanceMeter(0l);// 이동거리(미터)
			nodeCost.setDurationSecond(0l);// 이동시간(초)
			nodeCost.setTollFare(0);// 통행 요금(톨게이트)
			nodeCost.setTaxiFare(0);// 택시 요금(지자체별, 심야, 시경계, 복합, 콜비 감안)
			nodeCost.setPathJson(new ObjectMapper().writeValueAsString(pathPointList));// 이동경로json [[x,y],[x,y]]
			nodeCost.setRegDt(new Date());// 등록일시
			nodeCost.setModDt(new Date());// 수정일시
			return nodeCost;
		}
		List<Road> roads = sections.get(0).getRoads();
		for (Road road : roads) {
			List<Double> vertexes = road.getVertexes();
			for (int q = 0; q < vertexes.size(); q++) {
				pathPointList.add(new Point(vertexes.get(q), vertexes.get(++q)));
			}
		}
		Summary summary = route.getSummary();
		Integer distance = summary.getDistance();
		Integer duration = summary.getDuration();
		Fare fare = summary.getFare();
		Integer taxi = fare.getTaxi();
		Integer toll = fare.getToll();

		nodeCost = new NodeCost();
		nodeCost.setStartNodeId(prev.getId());// 시작노드id
		nodeCost.setEndNodeId(next.getId());// 종료노드id
		nodeCost.setDistanceMeter(distance.longValue());// 이동거리(미터)
		nodeCost.setDurationSecond(duration.longValue());// 이동시간(초)
		nodeCost.setTollFare(toll);// 통행 요금(톨게이트)
		nodeCost.setTaxiFare(taxi);// 택시 요금(지자체별, 심야, 시경계, 복합, 콜비 감안)
		nodeCost.setPathJson(new ObjectMapper().writeValueAsString(pathPointList));// 이동경로json [[x,y],[x,y]]
		nodeCost.setRegDt(new Date());// 등록일시
		nodeCost.setModDt(new Date());// 수정일시

		return nodeCost;
	}

	public List<Delivery> findById(Long Id) {

		return deliveryRepository.findByDriverIdOrderByDdate(Id);
	}

	public List<DriverRoute> findByDridtoday(String Id,LocalDate today) {

		return driverRouteRepository.findByDridtoday(Id,today);
	}

	public DriverRoute DateRoutefindById(String id) {

		return driverRouteRepository.findById(Long.parseLong(id)).orElse(null);
	}

	public List<DriverRoute> RoutefindByIdDate(String Did, LocalDate today) {

		return driverRouteRepository.RoutefindByIdDate(Did, today);
	}

	public List<DriverRoute> RoutefindById(String drid) {
		
		return driverRouteRepository.findByDrid(drid);
	}

}