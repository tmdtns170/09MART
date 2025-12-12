package com.iciafinally.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iciafinally.domain.JsonResult;
import com.iciafinally.domain.Node;
import com.iciafinally.domain.NodeCost;
import com.iciafinally.domain.NodeCostParam;

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
import com.iciafinally.service.NodeCostService;
import com.iciafinally.service.VrpService;




@Controller
public class VrpController {
	 	  

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

	  @PostMapping("/vrp")
	  @ResponseBody
	  public JsonResult postVrp(@RequestBody List<Node> nodeList) throws IOException, InterruptedException {
	    VrpService vrpService = new VrpService();
	    Node firstNode = nodeList.get(0);
	    String firstNodeId = String.valueOf(firstNode.getId());
	    // 차량 등록
	    vrpService.addVehicle("차량01", firstNodeId);

	    Map<String, Node> nodeMap = new HashMap<>();
	    Map<String, Map<String, NodeCost>> nodeCostMap = new HashMap<>();

	    for (Node node : nodeList) {
	      String nodeId = String.valueOf(node.getId());
	      // 화물 등록
	      vrpService.addShipement(node.getName(), firstNodeId, nodeId);
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
	        vrpService.addCost(startNodeId, endNodeId, durationSecond, distanceMeter);
	        if (!nodeCostMap.containsKey(startNodeId)) {
	          nodeCostMap.put(startNodeId, new HashMap<>());
	        }
	        nodeCostMap.get(startNodeId).put(endNodeId, nodeCost);
	      }
	    }

	    //
	    
	    List<Node> vrpNodeList = new ArrayList<>();

	    VrpResult vrpResult = vrpService.getVrpResult();
	    
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
}