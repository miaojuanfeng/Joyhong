package com.joyhong.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.joyhong.model.User;
import com.joyhong.service.DeviceService;
import com.joyhong.service.OrderService;
import com.joyhong.service.UserService;

@Controller
public class DashboardCtrl {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;

	/*
	 * 主页跳转
	 */
	@RequestMapping(value="/cms", method=RequestMethod.GET)
	public String index(@ModelAttribute("redirect") String redirect){
		
		if( redirect != null ){
			return redirect;
		}
		
		return "redirect:/cms/dashboard/select";
	}
	
	@RequestMapping(value="/cms/dashboard/select", method=RequestMethod.GET)
	public String select(@ModelAttribute("redirect") String redirect){
		
		if( redirect != null ){
			return redirect;
		}
		
		return "DashboardView";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		//判断是否登录
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			model.addAttribute("redirect", "redirect:/cms/user/login");
			return;
		}else{
			model.addAttribute("redirect", null);
		}
		
		model.addAttribute("orderCount", orderService.selectCount());
		model.addAttribute("deviceCount", deviceService.selectCount());
		model.addAttribute("userCount", userService.selectCount());
		
		model.addAttribute("facebookCount", userService.selectPlatformCount("facebook"));
		model.addAttribute("twitterCount", userService.selectPlatformCount("twitter"));
		model.addAttribute("appCount", userService.selectPlatformCount("app"));
		
		//当前登录用户名
		model.addAttribute("user_nickname", user.getNickname());
	}

}
