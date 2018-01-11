package com.joyhong.cms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.joyhong.model.Config;
import com.joyhong.model.User;
import com.joyhong.service.ConfigService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("cms/config")
public class ConfigCtrl {
	
	@Autowired
	private ConfigService configService;
	
	
	@RequestMapping(value="/update", method=RequestMethod.GET)
	public String update(
			Model model, 
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Config> config = configService.selectAllRecord();
		if( config != null ){
			for(Config c : config){
				String value = c.getValue();
				if( value != "" ){
					JSONObject valueObj = JSONObject.fromObject(value);
					map.put(valueObj.getString("name"), valueObj.getString("value"));
				}
			}
			model.addAttribute("config", config);
					
			return "PageView";
		}
		return "redirect:/cms/device/select";
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
		
		//解析出方法名称
		String urlStr = request.getRequestURL().toString();
		String method = urlStr.substring(urlStr.lastIndexOf("/")+1);
		if( isNumeric(method) ){
//			Integer number = Integer.valueOf(method);
			urlStr = urlStr.substring(0, urlStr.lastIndexOf("/"));
			method = urlStr.substring(urlStr.lastIndexOf("/")+1);
//			if( method.equals("update") ){
//				model.addAttribute("device", deviceService.selectByPrimaryKey(number));
//			}
		}
		model.addAttribute("method", method);
		
		//当前登录用户名
		model.addAttribute("user_nickname", user.getNickname());
		
		//返回的url地址
		model.addAttribute("referer", request.getHeader("referer"));
	}
	
	private static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();
	}
}
