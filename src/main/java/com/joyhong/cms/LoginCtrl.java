package com.joyhong.cms;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Config;
import com.joyhong.model.User;
import com.joyhong.service.ConfigService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("cms")
public class LoginCtrl {
	
	@Autowired
	private ConfigService configService;
	
	/**
	 * 用户登录
	 * @param user_username
	 * @param user_password
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(
			HttpSession httpSession,
			@ModelAttribute("redirect") String redirect
	){
		
		if( redirect == null ){
			return "redirect:/cms/dashboard/select";
		}
		
		return "LoginView";
	}
	
	/**
	 * 用户登录
	 * @param user_username
	 * @param user_password
	 * @param model
	 * @param httpSession
	 * @param redirect
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(
			@RequestParam(value="user_username") String user_username, 
			@RequestParam(value="user_password") String user_password, 
			Model model, 
			HttpSession httpSession,
			@ModelAttribute("redirect") String redirect
	){
		
		if( redirect == null ){
			return "redirect:/cms/dashboard/select";
		}
		
		String error = null;
		
		Config administrator = configService.selectByTitle("Administrator");
		JSONObject administratorObj = JSONObject.fromObject(administrator.getValue());
		String configUsername = administratorObj.getString("username");
		String configPassword = administratorObj.getString("password");
		
		if( user_username != null && configUsername.equals(user_username) ){
			if( user_password != null && configPassword.equals(DigestUtils.md5Hex(user_password)) ){
				User user = new User();
				user.setNickname(user_username);
				httpSession.setAttribute("user", user);
				
				return "redirect:/cms/dashboard/select";
			}else{
				error = "Incorrect Password";
			}
		}else if( user_username != null ){
			error = "The Username does not exist";
		}
		
		if( error != null ){
			model.addAttribute("error", error);
		}
		model.addAttribute("user_username", user_username);
		model.addAttribute("user_password", user_password);
		
		return "LoginView";
	}
	
	/**
	 * 用户登出
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession httpSession){
		httpSession.setAttribute("user", null);
		
		return "redirect:/cms/login";
	}
}
