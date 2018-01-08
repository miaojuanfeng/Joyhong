package com.joyhong.cms;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.User;

@Controller
@RequestMapping("cms/user")
public class UserCtrl {
	
	/**
	 * 用户登录
	 * @param user_username
	 * @param user_password
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(HttpSession httpSession){
		
		if( isLogin(httpSession) ){
			return "redirect:/cms/device/select";
		}
		
		return "LoginView";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestParam(value="user_username") String user_username, @RequestParam(value="user_password") String user_password, Model model, HttpSession httpSession){
		
		if( isLogin(httpSession) ){
			return "redirect:/cms/device/select";
		}
		
		String error = null;
		
		if( user_username != null && user_username.equals("joyhong") ){
			if( user_password != null && user_password.equals("joyhong") ){
				User user = new User();
				user.setNickname(user_username);
				httpSession.setAttribute("user", user);
				
				return "redirect:/cms/device/select";
			}else{
				error = "Incorrect password";
			}
		}else if( user_username != null ){
			error = "The username does not exist";
		}
		
		if( error != null ){
			model.addAttribute("error", error);
		}
		model.addAttribute("user_username", user_username);
		model.addAttribute("user_password", user_password);
		
		return "LoginView";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession httpSession){
		httpSession.setAttribute("user", null);
		
		return "redirect:/cms/user/login";
	}
	
	private boolean isLogin(HttpSession httpSession){
		if( httpSession.getAttribute("user") != null ){
			return true;
		}
		return false;
	}
}
