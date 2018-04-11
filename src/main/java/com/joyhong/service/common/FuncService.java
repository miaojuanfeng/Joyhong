package com.joyhong.service.common;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.joyhong.model.User;

@Service
public class FuncService {
	
	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();
	}
	
	/**
	 * 注入model参数
	 * @param model
	 * @param httpSession
	 * @param request
	 */
	public void modelAttribute(Model model, HttpSession httpSession, HttpServletRequest request){
		User user = (User)httpSession.getAttribute("user");
		
		//解析出方法名称
		String urlStr = request.getRequestURL().toString();
		String method = urlStr.substring(urlStr.lastIndexOf("/")+1);
		if( this.isNumeric(method) ){
			urlStr = urlStr.substring(0, urlStr.lastIndexOf("/"));
			method = urlStr.substring(urlStr.lastIndexOf("/")+1);
		}
		model.addAttribute("method", method);
		
		//当前登录用户名
		model.addAttribute("user_nickname", user.getNickname());
		
		//返回的url地址
		model.addAttribute("referer", request.getHeader("referer"));
	}
}
