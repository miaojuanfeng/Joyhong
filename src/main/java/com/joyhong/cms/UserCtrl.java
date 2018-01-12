package com.joyhong.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Config;
import com.joyhong.model.Device;
import com.joyhong.model.Order;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.ConfigService;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("cms/user")
public class UserCtrl {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
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
			return "redirect:/cms/device/select";
		}
		
		return "LoginView";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(
			@RequestParam(value="user_username") String user_username, 
			@RequestParam(value="user_password") String user_password, 
			Model model, 
			HttpSession httpSession,
			@ModelAttribute("redirect") String redirect
	){
		
		if( redirect == null ){
			return "redirect:/cms/device/select";
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
				
				return "redirect:/cms/device/select";
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
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession httpSession){
		httpSession.setAttribute("user", null);
		
		return "redirect:/cms/user/login";
	}
	
	@RequestMapping(value="/all/select", method=RequestMethod.GET)
	public String selectAll(){
		return "redirect:/cms/user/all/select/1";
	}
	
	@RequestMapping(value="/facebook/select", method=RequestMethod.GET)
	public String selectFacebook(){
		return "redirect:/cms/user/facebook/select/1";
	}
	
	@RequestMapping(value="/twitter/select", method=RequestMethod.GET)
	public String selectTwitter(){
		return "redirect:/cms/user/twitter/select/1";
	}
	
	@RequestMapping(value="/app/select", method=RequestMethod.GET)
	public String selectApp(){
		return "redirect:/cms/user/app/select/1";
	}
	
	@RequestMapping(value="/all/select/{page}", method=RequestMethod.GET)
	public String selectAll(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		pager(model, page, null);
		
		return "UserView";
	}
	
	@RequestMapping(value="/facebook/select/{page}", method=RequestMethod.GET)
	public String selectFacebook(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		pager(model, page, "facebook");
		
		return "UserView";
	}
	
	@RequestMapping(value="/twitter/select/{page}", method=RequestMethod.GET)
	public String selectTwitter(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		pager(model, page, "twitter");
		
		return "UserView";
	}
	
	@RequestMapping(value="/app/select/{page}", method=RequestMethod.GET)
	public String selectApp(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		pager(model, page, "app");
		
		return "UserView";
	}
	
	private void pager(Model model, Integer page, String platform){
		int pageSize = 20;
		int totalRecord = 0;
		if( platform != null ){
			totalRecord = userService.selectPlatformCount(platform);
		}else{
			totalRecord = userService.selectCount();
		}
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<User> user = null;
		if( platform != null ){
			user = userService.selectPlatformOffsetAndLimit(platform, offset, pageSize);
		}else{
			user = userService.selectOffsetAndLimit(offset, pageSize);
		}
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("user", user);
	}
	
	@RequestMapping(value="/update/{user_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("user_id") Integer user_id,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		User user = userService.selectByPrimaryKey(user_id);
		if( user != null ){
			model.addAttribute("user", user);

			List<Map<String, Object>> device = new ArrayList<Map<String, Object>>();
			List<UserDevice> userDevice = userDeviceService.selectByUserId(user_id);
			for(UserDevice ud : userDevice){
				Device d = deviceService.selectByPrimaryKey(ud.getDeviceId());
				if( d != null ){
					Map<String, Object> dMap = new HashMap<String, Object>();
					dMap.put("id", d.getId());
					dMap.put("deviceToken", d.getDeviceToken());
					dMap.put("deviceName", ud.getDeviceName());
					dMap.put("createDate", d.getCreateDate());
					dMap.put("modifyDate",  d.getModifyDate());
					device.add(dMap);
				}
			}
			model.addAttribute("device", device);
			model.addAttribute("deviceTotal", device.size());
		
			return "UserView";
		}
		return "redirect:/cms/user/all/select";
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
			Integer number = Integer.valueOf(method);
			urlStr = urlStr.substring(0, urlStr.lastIndexOf("/"));
			method = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if( method.equals("update") ){
				model.addAttribute("user", userService.selectByPrimaryKey(number));
			}
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
