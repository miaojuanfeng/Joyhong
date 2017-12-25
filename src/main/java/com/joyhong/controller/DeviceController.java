package com.joyhong.controller;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.service.DeviceService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/device")
public class DeviceController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	/**
	 * 注册device id与fcm token
	 * @url https://well.bsimb.cn/device/signin
	 * @param device_id
	 * @param device_fcm_token
	 * @return json
	 */
	@RequestMapping(value="/signin", method=RequestMethod.POST)
	@ResponseBody
	public String signin(@RequestParam("device_id") String device_id, @RequestParam("device_fcm_token") String device_fcm_token){
		JSONObject retval = new JSONObject();
		
		Device device = new Device();
		device.setDeviceId(device_id);
		device.setDeviceFcmToken(device_fcm_token);
		device.setCreateDate(new Date());
		device.setModifyDate(new Date());
		device.setDeleted(0);
		
		if( this.deviceService.insert(device) == 1 ){
			retval.put("status", true);
		}else{
			retval.put("status", false);
			logger.info("Save device to database failed: " + device_id + " - " + device_fcm_token);
		}
		
		return retval.toString();
	}
	
	/**
	 * 查询device id绑定的所有用户信息
	 * @param device_id
	 * @return
	 */
	@RequestMapping(value="/device_user", method=RequestMethod.POST)
	@ResponseBody
	public String device_user(@RequestParam("device_id") String device_id){
		// facebook twitter 用户信息 头像
		return "building";
	}
}
