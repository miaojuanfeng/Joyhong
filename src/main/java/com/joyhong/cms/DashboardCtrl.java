package com.joyhong.cms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DashboardCtrl {

	/*
	 * 主页跳转
	 */
	@RequestMapping(value="/cms", method=RequestMethod.GET)
	public String index(){
		return "redirect:/cms/user/login";
	}
}
