package com.joyhong.cms;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
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
		
		Config administrator = configService.selectByTitle("Administrator");
		JSONObject administratorObj = JSONObject.fromObject(administrator.getValue());
		model.addAttribute("username", administratorObj.getString("username"));
		Config version = configService.selectByTitle("Version");
		JSONObject versionObj = JSONObject.fromObject(version.getValue());
		model.addAttribute("last_version", versionObj.getString("last_version"));
		model.addAttribute("download_link", versionObj.getString("download_link"));
		
		return "ConfigView";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(
			@ModelAttribute("redirect") String redirect,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("last_version") String last_version,
			@RequestParam("download_link") String download_link
	){
		if( redirect != null ){
			return redirect;
		}
		
		Config config = new Config();
		/*
		 * update administrator
		 */
		JSONObject administratorObj = new JSONObject();
		administratorObj.put("username", username);
		if( password == "" ){
			Config administrator = configService.selectByTitle("Administrator");
			JSONObject temp = JSONObject.fromObject(administrator.getValue());
			administratorObj.put("password", temp.getString("password"));
		}else{
			administratorObj.put("password", DigestUtils.md5Hex(password));
		}
		config.setTitle("Administrator");
		config.setValue(administratorObj.toString());
		configService.updateByTitleWithBLOBs(config);
		/*
		 * update version
		 */
		JSONObject versionObj = new JSONObject();
		versionObj.put("last_version", last_version);
		versionObj.put("download_link", download_link);
		config.setTitle("Version");
		config.setValue(versionObj.toString());
		configService.updateByTitleWithBLOBs(config);
		
		return "redirect:/cms/config/update";
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
