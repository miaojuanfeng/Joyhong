package com.joyhong.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.joyhong.model.Device;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.FuncService;

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
	private FuncService funcService;
	
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
			@PathVariable(value="page") Integer page
	){
		pager(model, page, null);
		
		return "UserView";
	}
	
	@RequestMapping(value="/facebook/select/{page}", method=RequestMethod.GET)
	public String selectFacebook(
			Model model,  
			@PathVariable(value="page") Integer page
	){
		pager(model, page, "facebook");
		
		return "UserView";
	}
	
	@RequestMapping(value="/twitter/select/{page}", method=RequestMethod.GET)
	public String selectTwitter(
			Model model,  
			@PathVariable(value="page") Integer page
	){
		pager(model, page, "twitter");
		
		return "UserView";
	}
	
	@RequestMapping(value="/app/select/{page}", method=RequestMethod.GET)
	public String selectApp(
			Model model,  
			@PathVariable(value="page") Integer page
	){
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
			@PathVariable("user_id") Integer user_id
	){
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
		funcService.modelAttribute(model, httpSession, request);
	}
}
