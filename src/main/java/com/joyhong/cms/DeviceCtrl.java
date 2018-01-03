package com.joyhong.cms;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Device;
import com.joyhong.model.User;
import com.joyhong.service.DeviceService;

@Controller
@RequestMapping("cms/device")
public class DeviceCtrl {
	
	@Autowired
	private DeviceService deviceService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession) ){
			return "redirect:/cms/user/login";
		}
		
		List<Device> device = deviceService.selectLikeDeviceToken("");
		model.addAttribute("device", device);
		
		return "DeviceView";
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(@RequestParam("device_id") Integer device_id, Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession) ){
			return "redirect:/cms/user/login";
		}
		
		Device device = deviceService.selectByPrimaryKey(device_id);
		if( device != null ){
			device.setModifyDate(new Date());
			device.setDeleted(1);
			deviceService.updateByPrimaryKey(device);
		}
		
		return "redirect:/cms/device/select";
	}
	
	private boolean permission(Model model, HttpSession httpSession){
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			return false;
		}
		model.addAttribute("user_nickname", user.getNickname());
		return true;
	}
	
}
