package com.iciafinally.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iciafinally.domain.Driver;
import com.iciafinally.domain.DriverRoute;
import com.iciafinally.domain.Node;
import com.iciafinally.domain.Orders;
import com.iciafinally.domain.Product;
import com.iciafinally.service.DriverService;
import com.iciafinally.service.OrderService;
import com.iciafinally.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/drivers")
public class DriverController {
	@Autowired
	private HttpSession session;
	@Autowired
	private DriverService driverService;
	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

///drivers/driverJoin
	@GetMapping("/main")
	public String getDriverMain(Model model) {
		model.addAttribute("driverForm", new DriverForm());
		if (session.getAttribute("loginDid") != null) {

			List<DriverRoute> driverRoute = driverService.RoutefindById((String) session.getAttribute("loginDid"));

			model.addAttribute("driverRoute", driverRoute);
		}
		return "driver/driverMain";
	}

	@GetMapping("/driverJoin")
	public String driverJoinForm(Model model) {
		System.out.println("회원 등록 페이지 이동요청");
		DriverForm driverForm = new DriverForm();
		model.addAttribute("driverForm", driverForm);
		System.out.println("!!!@@#"); // 여기까지 안넘어감
		return "driver/driverJoinForm";
	}

	@PostMapping("/join")
	public String registMember(@Valid DriverForm driverForm, BindingResult result, Model model) {
		System.out.println("기사 회원 등록 요청");
		System.out.println(driverForm);
		model.addAttribute("driverForm", driverForm);
		/* 회원 아이디 중복 확인 */
		if (!result.hasFieldErrors("did")) { // mid에 Error가 없는 경우
			// 아이디로 회원 정보 조회
			Driver checkDriver = driverService.findByDid(driverForm.getDid());
			if (checkDriver != null) { // 회원 정보가 조회 되는 경우
				// 중복 확인 Error 추가
				result.rejectValue("did", "duplicateDid", "이미 사용중인 아이디 입니다.");
			}
		}
		/* 유효성검사 */
		if (result.hasErrors()) { // Error가 확인 된 경우
			return "driver/driverJoinForm";
		}

		/* 회원 등록 기능 호출 */
		try {
			Driver driver = Driver.createDriver(driverForm);
			driverService.registDriver(driver);
		} catch (Exception e) {
			e.printStackTrace();
			// model.addAttribute("memberForm", memberForm);
			return "driver/driverJoinForm";
		}
		return "redirect:/drivers/login";


	} 

	@GetMapping("/login")
	public String driverLogin(DriverForm driverForm, Model model) {
		System.out.println("로그인 페이지 이동 요청");

		return "driver/login";

	}

	@PostMapping("/login")
	public String loginDriver(DriverForm driverForm, Model model) {
		System.out.println("로그인");

		if (driverForm != null) {
			Driver checkDriver = driverService.findByDidAndDpw(driverForm.getDid(), driverForm.getDpw());
			if (checkDriver == null) {
				System.out.println("아이디 또는 비밀번호가 맞지 않습니다(드라이버)");

				model.addAttribute("driverForm", driverForm);

				return "redirect:/drivers/login";

			} else {
				session.setAttribute("loginDid", checkDriver.getDid());
				session.setAttribute("loginDname", checkDriver.getDname());
				session.setAttribute("loginFid", checkDriver.getDid());
				
				LocalDate today = LocalDate.now();
				List<DriverRoute> driverRoute = driverService.findByDridtoday((String) session.getAttribute("loginDid"),today);

				model.addAttribute("driverRoute", driverRoute);
				System.out.println("======" + driverRoute.size());

				return "driver/driverMain";
			}
		}
		return null;
	}

	@GetMapping("/driverdate")
	@ResponseBody
	public DriverRoute driverdate(@RequestParam("id") String id) {
		System.out.println("해당 날짜 루트 요청");
		System.out.println("id" + id);
		DriverRoute driverRoute = driverService.DateRoutefindById(id);
		JsonArray nodeList_Array = JsonParser.parseString(driverRoute.getNodelist()).getAsJsonArray();

		List<Node> nodeList = new ArrayList<>();
		for (JsonElement node_json : nodeList_Array) {
			Node node = new Gson().fromJson(node_json, Node.class);
			System.out.println(node.getOrderid());
			String ostate = null;
			String pickup = null;
			if (node.getOrderid() != null) {
				ostate = orderService.findById(node.getOrderid()).getOstate();
			}
			node.setOstate(ostate);

			if (node.getPname() != null) {

				pickup = productService.findById(node.getPid()).getPickup();
			}
			node.setPickup(pickup);
			nodeList.add(node);

		}

		String nodeList_Json = new Gson().toJson(nodeList);
		driverRoute.setNodelist(nodeList_Json);

		return driverRoute;

	}

	@GetMapping("/deliverystate")
	@ResponseBody
	public String deliverystate(@RequestParam("orderid") String orderid) {
		System.out.println("배달 상태 변경 요청");

		Orders orders = orderService.findById(Long.parseLong(orderid));

		orders.setOstate("F");
		orderService.save(orders);
		System.out.println(orders);
		return "성공";

	}

	@GetMapping("/pickupstate")
	@ResponseBody
	public String pickupstate(@RequestParam("pid") Long pid) {
		System.out.println("픽업 상태 변경 요청");

		Product product = productService.findById(pid);

		product.setPickup("F");
		productService.save(product);

		return "성공";

	}

	@GetMapping("/dmypage")
	public String dmypage(Model model) {
		System.out.println("기사 마이페이지 이동");
		Driver driver = driverService.findByDid((String) session.getAttribute("loginDid"));
		LocalDate today = LocalDate.now();
		List<DriverRoute> driverRoute = driverService.RoutefindByIdDate((String) session.getAttribute("loginDid"),
				today);
		model.addAttribute("driverRoute", driverRoute);
		model.addAttribute("driver", driver);
		return "driver/dmypage";

	}

	@GetMapping("/dchange")
	public String sellerChange(Model model) {
		String id = (String) session.getAttribute("loginDid");
		if (id != null) {
			Driver driver = driverService.findByDid(id);
			model.addAttribute("driver", driver);
			return "driver/dchange";
		}
		return "driver/login";
	}

	@PostMapping("/dchange")
	public String selleraddr(Driver driver,RedirectAttributes ra) {
		 // 세션에서 드라이버 ID 가져오기
	    String driverId = (String) session.getAttribute("loginDid");
	    if (driverId == null) {
	        return "redirect:/drivers/login"; // 로그인 페이지로 리다이렉트
	    }
	    Driver existingDriver = driverService.findByDid(driverId); // 세션 ID 사용
	    // 현재 아이디와 변경하려는 아이디가 같은 경우
	    if (driver.getDid().equals(existingDriver.getDid())) {
	        // 그냥 넘어가도록 설정
	        return "redirect:/drivers/dmypage"; // 또는 다른 적절한 경로로 리다이렉트
	    }

	 // 아이디 중복 체크 (변경할 경우에만 체크)
	    if (!driver.getDid().equals(existingDriver.getDid())) {
	        Driver checkDriver = driverService.findByDid(driver.getDid());
	        if (checkDriver != null) {
	            ra.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
	            return "redirect:/drivers/dchange"; // 오류 메시지를 포함하여 리다이렉트
	        }
	    }
	    // 비밀번호를 변경하지 않는 경우 기존 비밀번호를 유지
	    if (driver.getDpw() == null || driver.getDpw().isEmpty()) {
	        driver.setDpw(existingDriver.getDpw()); // 기존 비밀번호로 설정
	    }
	    try {
	        driverService.registDriver(driver);
	        // 세션에 수정된 정보 저장
	        session.setAttribute("loginDid", driver.getDid());
	        session.setAttribute("loginDname", driver.getDname());
	        
	        return "redirect:/drivers/dmypage";
	
	}catch (Exception e) {
        ra.addFlashAttribute("error", "정보 수정 중 오류가 발생했습니다.");
        return "redirect:/drivers/dchange"; // 오류 발생 시 수정 페이지로 리다이렉트
    }
	}
	@GetMapping("/checkdateroute")
	@ResponseBody
	public DriverRoute checkdateroute(@RequestParam("id") String id) {
		System.out.println("이전 날짜 루트 조회");
		
		DriverRoute driverRoute = driverService.DateRoutefindById(id);
		
		return driverRoute ;

	}

}