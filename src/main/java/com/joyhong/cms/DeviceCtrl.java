package com.joyhong.cms;

import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("cms/device")
public class DeviceCtrl {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private FuncService funcService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(HttpServletRequest request){
		return "redirect:/cms/device/select/1"+funcService.requestParameters(request);
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(
			Model model,
			HttpServletRequest request,
			@PathVariable(value="page") Integer page
	){
		int pageSize = 20;
		int totalRecord = deviceService.selectOrderCount(request);
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Device> device = deviceService.selectOrderOffsetAndLimit(request, offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("device", device);
		model.addAttribute("parameters", funcService.requestParameters(request));
		
		return "DeviceView";
	}
	
	@RequestMapping(value="/update/{device_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("device_id") Integer device_id
	){
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
		funcService.modelAttribute(model, httpSession, request);
	}
}
