package com.joyhong.cms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("cms/user")
public class LoginController {

	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(){
		
		
		
		return "LoginView";
	}
}
