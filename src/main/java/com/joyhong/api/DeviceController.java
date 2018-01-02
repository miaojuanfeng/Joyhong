package com.joyhong.api;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 设备管理控制器
 * @url {base_url}/device/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/device")
public class DeviceController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	/**
	 * 注册device id与fcm token
	 * @url {base_url}/device/signin
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
		
		Device exist_device = this.deviceService.selectByDeviceId(device_id);
		if( exist_device == null ){
			if( this.deviceService.insert(device) == 1 ){
				retval.put("status", true);
			}else{
				retval.put("status", false);
				logger.info("Save device to database failed: " + device_id + " - " + device_fcm_token);
			}
		}else{
			device.setId(exist_device.getId());
			device.setCreateDate(exist_device.getCreateDate());
			if( this.deviceService.updateByPrimaryKey(device) == 1 ){
				retval.put("status", true);
			}else{
				retval.put("status", false);
				logger.info("Update device to database failed: " + device_id + " - " + device_fcm_token);
			}
		}
		
		return retval.toString();
	}
	
	/**
	 * 查询device id绑定的所有用户信息
	 * @url {base_url}/device/device_user
	 * @param device_id
	 * @return json
	 */
	@RequestMapping(value="/device_user", method=RequestMethod.POST)
	@ResponseBody
	public String device_user(@RequestParam("device_id") String device_id){
		JSONObject retval = new JSONObject();
		
		Device device = deviceService.selectByDeviceId(device_id);
		if( device != null ){
			List<UserDevice> userDevice = userDeviceService.selectByDeviceId(device.getId());
			JSONArray temp = new JSONArray();
			for(UserDevice ud : userDevice){
				User user = userService.selectByPrimaryKey(ud.getUserId());
				if( user != null){
					JSONObject uTemp = new JSONObject();
					uTemp.put("id", user.getId());
					uTemp.put("username", user.getUsername());
					uTemp.put("nickname", user.getNickname());
					uTemp.put("profile_image", user.getProfileImage());
					uTemp.put("platform", user.getPlatform());
					uTemp.put("accepted", user.getAccepted());
					uTemp.put("create_date", user.getCreateDate().getTime());
					uTemp.put("modify_date", user.getModifyDate().getTime());
					temp.add(uTemp);
				}
			}
			
			JSONObject dTemp = new JSONObject();
			dTemp.put("device_id", device.getId());
			dTemp.put("users", temp);
			
			retval.put("status", true);
			retval.put("data", dTemp);
		}else{
			retval.put("status", false);
			retval.put("msg", "The device id is not yet registered");
		}
		
		return retval.toString();
	}
	
	/**
	 * 根据设备号搜索设备
	 * @url {base_url}/device/search
	 * @param device_id
	 * @return json
	 */
	@RequestMapping(value="/search", method=RequestMethod.POST)
	@ResponseBody
	public String search(@RequestParam("device_id") String device_id){
		JSONObject retval = new JSONObject();
		
		List<Device> device = deviceService.selectLikeDeviceId(device_id);
		
		JSONArray array = new JSONArray();
		for( Device d : device ){
			JSONObject temp = new JSONObject();
			temp.put("id", d.getId());
			temp.put("device_id", d.getDeviceId());
			temp.put("device_fcm_token", d.getDeviceFcmToken());
			temp.put("create_date", d.getCreateDate().getTime());
			temp.put("modify_date", d.getModifyDate().getTime());
			array.add(temp);
		}
		
		retval.put("status", true);
		retval.put("data", array);
		
		return retval.toString();
	}
	
	/**
	 * 改变用户和设备的绑定状态
	 * @url {base_url}/device/status
	 * @param user_id
	 * @param device_id
	 * @param status
	 * @return json
	 */
	@RequestMapping(value="/status", method=RequestMethod.POST)
	@ResponseBody
	public String status(@RequestParam("user_id") String user_id, @RequestParam("device_id") String device_id, @RequestParam("status") String status){
		JSONObject retval = new JSONObject();
		
		retval.put("status", true);
		retval.put("data", "test");
		
		return retval.toString();
	}
}
