package com.joyhong.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用户管理控制器
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private DeviceService deviceService;
	
	/**
	 * 获取用户绑定的设备列表
	 * @param user_id 数据库id
	 * @return json
	 */
	@RequestMapping(value="/user_device", method=RequestMethod.GET)
	@ResponseBody
	public String user_device(@RequestParam("user_id") Integer user_id){
		JSONObject retval = new JSONObject();
		
		List<UserDevice> userDevice = userDeviceService.selectByUserId(user_id);
		JSONArray temp = new JSONArray();
		for(UserDevice ud : userDevice){
			Device device = deviceService.selectByPrimaryKey(ud.getDeviceId());
			if( device != null){
				JSONObject uTemp = new JSONObject();
				uTemp.put("id", device.getId());
				uTemp.put("device_id", device.getDeviceId());
				uTemp.put("device_fcm_token", device.getDeviceFcmToken());
				uTemp.put("create_date", device.getCreateDate().getTime());
				uTemp.put("modify_date", device.getModifyDate().getTime());
				temp.add(uTemp);
			}
		}
		retval.put("status", true);
		retval.put("data", temp);
		
		return retval.toString();
	}
}
