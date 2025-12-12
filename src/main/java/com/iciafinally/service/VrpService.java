package com.iciafinally.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.iciafinally.domain.JsonResult;
import com.iciafinally.domain.Node;
import com.iciafinally.domain.NodeCost;
import com.iciafinally.domain.NodeCostParam;
import com.iciafinally.domain.VrpResult;
import com.iciafinally.domain.VrpVehicleRoute;
import com.iciafinally.kakao.util.KakaoDirections;
import com.iciafinally.kakao.util.KakaoDirections.Route;
import com.iciafinally.kakao.util.KakaoDirections.Route.Section;
import com.iciafinally.kakao.util.KakaoDirections.Route.Summary;
import com.iciafinally.kakao.util.KakaoDirections.Route.Section.Road;
import com.iciafinally.kakao.util.KakaoDirections.Route.Summary.Fare;
import com.iciafinally.mapline.util.KakaoApiUtil;
import com.iciafinally.mapline.util.KakaoApiUtil.Point;

@Service
public class VrpService {
	private Map<String/* vehicleId */, VehicleImpl.Builder> vehicleBuilderMap = new HashMap<String, VehicleImpl.Builder>();
	private Map<String/* shipmentId */, Shipment.Builder> shipmentBuilderMap = new HashMap<String, Shipment.Builder>();
	private VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
			.newInstance(false);

	@Autowired
	private NodeCostService nodeCostService;

	private NodeCost getNodeCost(Node prev, Node next) throws IOException, InterruptedException {
		NodeCostParam nodeCostParam = new NodeCostParam();
		nodeCostParam.setStartNodeId(prev.getId());
		nodeCostParam.setEndNodeId(next.getId());
		NodeCost nodeCost = nodeCostService.getOneByParam(nodeCostParam);

		if (nodeCost == null) {
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
				nodeCostService.add(nodeCost);
				return null;
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
			nodeCostService.add(nodeCost);
		}
		return nodeCost;
	}

	public JsonResult postVrp(String driverName,@RequestBody List<Node> nodeList) throws IOException, InterruptedException {

		Node firstNode = nodeList.get(0);
		String firstNodeId = String.valueOf(firstNode.getId());
// 차량 등록
		addVehicle(driverName, firstNodeId);

		Map<String, Node> nodeMap = new HashMap<>();
		Map<String, Map<String, NodeCost>> nodeCostMap = new HashMap<>();

		for (Node node : nodeList) {
			String nodeId = String.valueOf(node.getId());
			// 화물 등록
			addShipement(node.getName(), firstNodeId, nodeId);
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
				addCost(startNodeId, endNodeId, durationSecond, distanceMeter);
				if (!nodeCostMap.containsKey(startNodeId)) {
					nodeCostMap.put(startNodeId, new HashMap<>());
				}
				nodeCostMap.get(startNodeId).put(endNodeId, nodeCost);
			}
		}

//

		List<Node> vrpNodeList = new ArrayList<>();

		VrpResult vrpResult = getVrpResult();

		String prevLocationId = null;
		for (VrpVehicleRoute vrpVehicleRoute : vrpResult.getVrpVehicleRouteList()) {
			System.out.println(vrpVehicleRoute);
			String locationId = vrpVehicleRoute.getLocationId();
			if (prevLocationId == null) {
				prevLocationId = locationId;
			} else if (locationId.equals(prevLocationId)) {
				continue;
			}

			prevLocationId = locationId;
			vrpNodeList.add(nodeMap.get(locationId));

		}

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

		JsonResult jsonResult = new JsonResult();
		jsonResult.addData("totalDistance", totalDistance);// 전체이동거리
		jsonResult.addData("totalDuration", totalDuration);// 전체이동시간
		jsonResult.addData("totalPathPointList", totalPathPointList);// 전체이동경로
		jsonResult.addData("nodeList", vrpNodeList);// 방문지목록
		return jsonResult;
	}
	
	/**
	 * 차량 등록
	 * 
	 * @param vehicleId       차량ID
	 * @param startLocationId 차량시작위치ID
	 * @return
	 */
	public VehicleImpl.Builder addVehicle(String vehicleId, String startLocationId) {
		VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(vehicleId);
		builder.setType(getVehicleType());

		Location startLoc = Location.Builder.newInstance().setId(startLocationId).build();
		builder.setStartLocation(startLoc);
		vehicleBuilderMap.put(vehicleId, builder);
		return builder;
	}

	/**
	 * 업무등록
	 * 
	 * @param shipmentId         업무ID
	 * @param pickupLocationId   픽업위치ID
	 * @param deliveryLocationId 배송위치ID
	 * @return
	 */
	public Shipment.Builder addShipement(String shipmentId, String pickupLocationId, String deliveryLocationId) {
		Shipment.Builder shipmentBuilder = Shipment.Builder.newInstance(shipmentId);
		shipmentBuilder.setPickupLocation(Location.Builder.newInstance().setId(pickupLocationId).build());//
		shipmentBuilder.setDeliveryLocation(Location.Builder.newInstance().setId(deliveryLocationId).build());//
		shipmentBuilderMap.put(shipmentId, shipmentBuilder);
		return shipmentBuilder;
	}

	/**
	 * 비용등록
	 * 
	 * @param fromLocationId 출발지위치ID
	 * @param toLocationId   도착지위치ID
	 * @param cost           비용
	 */
	public void addCost(String fromLocationId, String toLocationId, long cost) {
		costMatrixBuilder.addTransportTime(fromLocationId, toLocationId, cost);
	}

	/**
	 * 비용등록
	 * 
	 * @param fromLocationId 출발지위치ID
	 * @param toLocationId   도착지위치ID
	 * @param cost           비용(이동시간)
	 * @param distnace       거리
	 */
	public void addCost(String fromLocationId, String toLocationId, long cost, long distnace) {
		costMatrixBuilder.addTransportTime(fromLocationId, toLocationId, cost);
		costMatrixBuilder.addTransportDistance(fromLocationId, toLocationId, distnace);
	}

	/**
	 * vrp 결과 조회
	 * 
	 * @return VrpResult
	 */
	public VrpResult getVrpResult() {
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		addVehicles(vrpBuilder);
		addShipments(vrpBuilder);
		VehicleRoutingTransportCostsMatrix vehicleRoutingTransportCostsMatrix = costMatrixBuilder.build();
		VehicleRoutingProblem vrp = vrpBuilder.setRoutingCost(vehicleRoutingTransportCostsMatrix).build();

		VehicleRoutingProblemSolution bestSolution = getBestSolution(vrp, vehicleRoutingTransportCostsMatrix);

		VrpResult vrpResult = new VrpResult();
		vrpResult.setTotalJobCount(shipmentBuilderMap.size());
		vrpResult.setTotalVehicleCount(vehicleBuilderMap.size());
		vrpResult.setCost(Double.valueOf(bestSolution.getCost()).longValue());

		List<VehicleRoute> list = new ArrayList<VehicleRoute>(bestSolution.getRoutes());
		Collections.sort(list, new com.graphhopper.jsprit.core.util.VehicleIndexComparator());

		int routeNo = 1;
		long costs = 0l;
		long totalDistance = 0l;
		long totalTime = 0l;
		long totalVisitCount = 0l;
		for (VehicleRoute route : list) {

			totalDistance = 0l;
			totalTime = 0l;
			totalVisitCount = 0l;
			VrpVehicleRoute startVrpVehicleRoute = new VrpVehicleRoute();
			startVrpVehicleRoute.setRouteNo(routeNo);
			String vehicleId = route.getVehicle().getId();
			startVrpVehicleRoute.setVehicleId(vehicleId);
			startVrpVehicleRoute.setActivityName(route.getStart().getName());
			startVrpVehicleRoute.setLocationId(route.getStart().getLocation().getId());
			startVrpVehicleRoute.setEndTime(Double.valueOf(route.getStart().getEndTime()).longValue());
			startVrpVehicleRoute.setCosts(costs);
			startVrpVehicleRoute.setJobId("-");
			startVrpVehicleRoute.setTotalDistance(totalDistance);
			startVrpVehicleRoute.setTotalTime(totalTime);
			startVrpVehicleRoute.setTotalVisitCount(totalVisitCount);
			vrpResult.addVehilceRoute(startVrpVehicleRoute);

			TourActivity prevAct = route.getStart();
			for (TourActivity act : route.getActivities()) {
				String jobId;
				if (act instanceof TourActivity.JobActivity) {
					jobId = ((TourActivity.JobActivity) act).getJob().getId();
				} else {
					jobId = "-";
				}

				double c = vrp.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(),
						prevAct.getEndTime(), route.getDriver(), route.getVehicle());
				c += vrp.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(),
						route.getVehicle());
				costs += c;

				double distance = vrp.getTransportCosts().getDistance(prevAct.getLocation(), act.getLocation(), 0l,
						null);
				totalDistance += distance;
				totalTime += vrp.getTransportCosts().getTransportTime(prevAct.getLocation(), act.getLocation(), 0l,
						null, null);
				totalTime += vrp.getActivityCosts().getActivityDuration(act, 0d, null, null);
				if (distance > 0) {
					totalVisitCount++;
				}

				VrpVehicleRoute vrpVehicleRoute = new VrpVehicleRoute();
				vrpVehicleRoute.setRouteNo(routeNo);
				vrpVehicleRoute.setVehicleId(vehicleId);
				vrpVehicleRoute.setActivityName(act.getName());
				vrpVehicleRoute.setLocationId(act.getLocation().getId());
				vrpVehicleRoute.setArrivalTime(Double.valueOf(act.getArrTime()).longValue());
				vrpVehicleRoute.setEndTime(Double.valueOf(act.getEndTime()).longValue());
				vrpVehicleRoute.setCosts(costs);
				vrpVehicleRoute.setJobId(jobId);
				vrpVehicleRoute.setTotalDistance(totalDistance);
				vrpVehicleRoute.setTotalTime(totalTime);
				vrpVehicleRoute.setTotalVisitCount(totalVisitCount);
				vrpResult.addVehilceRoute(vrpVehicleRoute);
				prevAct = act;
			}

			double c = vrp.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(),
					prevAct.getEndTime(), route.getDriver(), route.getVehicle());
			c += vrp.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(),
					route.getVehicle());
			costs += c;

			double distance = vrp.getTransportCosts().getDistance(prevAct.getLocation(), route.getEnd().getLocation(),
					0l, null);
			totalDistance += distance;
			totalTime += vrp.getTransportCosts().getTransportTime(prevAct.getLocation(), route.getEnd().getLocation(),
					0l, null, null);
			totalTime += vrp.getActivityCosts().getActivityDuration(prevAct, 0d, null, null);
			if (distance > 0) {
				totalVisitCount++;
			}
			VrpVehicleRoute endVrpVehicleRoute = new VrpVehicleRoute();
			endVrpVehicleRoute.setRouteNo(routeNo);
			endVrpVehicleRoute.setVehicleId(vehicleId);
			endVrpVehicleRoute.setActivityName(route.getEnd().getName());
			endVrpVehicleRoute.setLocationId(route.getEnd().getLocation().getId());
			endVrpVehicleRoute.setArrivalTime(Double.valueOf(route.getEnd().getArrTime()).longValue());
			endVrpVehicleRoute.setCosts(costs);
			endVrpVehicleRoute.setTotalDistance(totalDistance);
			endVrpVehicleRoute.setTotalTime(totalTime);
			endVrpVehicleRoute.setTotalVisitCount(totalVisitCount);
			endVrpVehicleRoute.setJobId("-");
			vrpResult.addVehilceRoute(endVrpVehicleRoute);
			routeNo++;
		}
		vrpResult.setRouteCount(routeNo - 1);
		return vrpResult;
	}

	/**
	 * 화물 등록
	 * 
	 * @param vrpBuilder
	 */
	private void addShipments(VehicleRoutingProblem.Builder vrpBuilder) {
		for (Shipment.Builder shipmentBuilder : shipmentBuilderMap.values()) {
			Shipment shipment = shipmentBuilder.build();
			vrpBuilder.addJob(shipment);
		}
	}

	/**
	 * 차량 유형 생성
	 * 
	 * @return
	 */
	private static VehicleType getVehicleType() {
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("DEFAULT_VEHICLE_TYPE")//
				.setCostPerDistance(1)// 이동거리
				.setCostPerWaitingTime(1)// 대기시간
				.setCostPerTransportTime(1)// 이동시간
				.setCostPerServiceTime(1)// 서비스시간
				.addCapacityDimension(0, 15);//
		return vehicleTypeBuilder.build();
	}

	/**
	 * 경로최적화 모듈에 차량 등록
	 * 
	 * @param vrpBuilder
	 */
	private void addVehicles(VehicleRoutingProblem.Builder vrpBuilder) {
		for (VehicleImpl.Builder vehicleBuilder : vehicleBuilderMap.values()) {
			VehicleImpl vehicle = vehicleBuilder.build();
			vrpBuilder.addVehicle(vehicle);
		}
	}

	/**
	 * 가장 좋은 결과 조회
	 * 
	 * @param vrp
	 * @param vehicleRoutingTransportCostsMatrix
	 * @return
	 */
	private VehicleRoutingProblemSolution getBestSolution(VehicleRoutingProblem vrp,
			VehicleRoutingTransportCostsMatrix vehicleRoutingTransportCostsMatrix) {

		// build the problem
		com.graphhopper.jsprit.core.algorithm.box.Jsprit.Builder jspritBuilder = Jsprit.Builder.newInstance(vrp);//
		VehicleRoutingAlgorithm algorithm = jspritBuilder//
				.setProperty("iterations", "2000")//
				.buildAlgorithm();

		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		return bestSolution;
	}

}
