package com.joyhong.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Device;
import com.joyhong.model.Order;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;

@Controller
@RequestMapping("cms/device")
public class DeviceCtrl {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
//	@RequestMapping(value="/select", method=RequestMethod.GET)
//	public String select(Model model, HttpSession httpSession){
//		
//		if( !permission(model, httpSession, "select") ){
//			return "redirect:/cms/user/login";
//		}
//		
//		List<Device> device = deviceService.selectLikeDeviceToken("");
//		model.addAttribute("device", device);
//		
//		return "DeviceView";
//	}
//	
//	@RequestMapping(value="/insert", method={RequestMethod.GET,RequestMethod.POST})
//	public String insert(Model model, HttpSession httpSession){
//		
//		if( !permission(model, httpSession, "insert") ){
//			return "redirect:/cms/user/login";
//		}
//		
//		List<Device> device = deviceService.selectLikeDeviceToken("");
//		model.addAttribute("device", device);
//		
//		return "DeviceView";
//	}
//	
//	@RequestMapping(value="/update/{device_id}", method={RequestMethod.GET,RequestMethod.POST})
//	public String update(@PathParam("device_id") Integer device_id, Model model, HttpSession httpSession){
//		
//		if( !permission(model, httpSession, "update") ){
//			return "redirect:/cms/user/login";
//		}
//		
//		List<Device> device = deviceService.selectLikeDeviceToken("");
//		model.addAttribute("device", device);
//		
//		return "DeviceView";
//	}
//	
//	@RequestMapping(value="/delete", method=RequestMethod.POST)
//	public String delete(@RequestParam("device_id") Integer device_id, Model model, HttpSession httpSession){
//		
//		if( !permission(model, httpSession, "delete") ){
//			return "redirect:/cms/user/login";
//		}
//		
//		Device device = deviceService.selectByPrimaryKey(device_id);
//		if( device != null ){
//			device.setModifyDate(new Date());
//			device.setDeleted(1);
//			deviceService.updateByPrimaryKey(device);
//		}
//		
//		return "redirect:/cms/device/select";
//	}
//	
//	private boolean permission(Model model, HttpSession httpSession, String router){
//		User user = (User)httpSession.getAttribute("user");
//		if( user == null ){
//			return false;
//		}
//		model.addAttribute("router", router);
//		model.addAttribute("user_nickname", user.getNickname());
//		return true;
//	}
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(){
		return "redirect:/cms/device/select/1";
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(
			Model model,  
			@PathVariable(value="page") Integer page,
			@ModelAttribute("redirect") String redirect,
			@ModelAttribute("platform") String platform
	){
		if( redirect != null ){
			return redirect;
		}
		
		int pageSize = 20;
		int totalRecord = deviceService.selectCount();
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Device> device = deviceService.selectOffsetAndLimit(offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("device", device);
		
		return "DeviceView";
	}
	
	@RequestMapping(value="/update/{device_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("device_id") Integer device_id,
			@ModelAttribute("redirect") String redirect
	){
		if( redirect != null ){
			return redirect;
		}
		
		Device device = deviceService.selectByPrimaryKey(device_id);
		if( device != null ){
			model.addAttribute("device", device);
			
			List<User> user = new ArrayList<User>();
			List<UserDevice> userDevice = userDeviceService.selectByDeviceId(device_id);
			for(UserDevice ud : userDevice){
				User u = userService.selectByPrimaryKey(ud.getUserId());
				if( u != null ){
					user.add(u);
				}
			}
			model.addAttribute("user", user);
			model.addAttribute("userTotal", user.size());
		
			return "DeviceView";
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
			Integer number = Integer.valueOf(method);
			urlStr = urlStr.substring(0, urlStr.lastIndexOf("/"));
			method = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if( method.equals("update") ){
				model.addAttribute("device", deviceService.selectByPrimaryKey(number));
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
