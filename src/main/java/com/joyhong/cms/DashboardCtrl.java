package com.joyhong.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.joyhong.service.DeviceService;
import com.joyhong.service.OrderService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.FuncService;

@Controller
public class DashboardCtrl {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FuncService funcService;

	/*
	 * 主页跳转
	 */
	@RequestMapping(value="/cms", method=RequestMethod.GET)
	public String index(){
		return "redirect:/cms/dashboard/select";
	}
	
	@RequestMapping(value="/cms/dashboard/select", method=RequestMethod.GET)
	public String select(){
		return "DashboardView";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		funcService.modelAttribute(model, httpSession, request);
		
		model.addAttribute("orderCount", orderService.selectCount());
		model.addAttribute("deviceCount", deviceService.selectCount());
		model.addAttribute("userCount", userService.selectCount());
		
		model.addAttribute("facebookCount", userService.selectPlatformCount("facebook"));
		model.addAttribute("twitterCount", userService.selectPlatformCount("twitter"));
		model.addAttribute("appCount", userService.selectPlatformCount("app"));
	}

}
