package com.joyhong.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.Order;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.OrderService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.ConstantService;

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
	
//	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	/**
	 * 注册device id与fcm token
	 * @url {base_url}/device/signin
	 * @param device_token
	 * @param device_fcm_token
	 * @return json
	 */
	@RequestMapping(value="/signin", method=RequestMethod.POST)
	@ResponseBody
	public String signin(@RequestParam("device_token") String device_token, @RequestParam("device_fcm_token") String device_fcm_token){
		JSONObject retval = new JSONObject();
		
		Device device = new Device();
		device.setDeviceToken(device_token);
		device.setDeviceFcmToken(device_fcm_token);
		
		Device exist_device = this.deviceService.selectByDeviceToken(device_token);
		if( exist_device != null ){
			Date now = new Date();
			device.setId(exist_device.getId());
			device.setLoginTime(now);
			device.setHeartbeatTime(now);
			if( this.deviceService.updateByPrimaryKeySelective(device) == 1 ){
				JSONObject temp = new JSONObject();
				temp.put("device_id", exist_device.getId());
				retval.put("status", ConstantService.statusCode_200);
				retval.put("data", temp);
			}else{
				retval.put("status", ConstantService.statusCode_102);
			}
		}else{
			retval.put("status", ConstantService.statusCode_101);
		}
		
		return retval.toString();
	}
	
	/**
	 * 查询device token绑定的所有用户信息
	 * @url {base_url}/device/device_user
	 * @param device_id
	 * @return json
	 */
	@RequestMapping(value="/device_user", method=RequestMethod.POST)
	@ResponseBody
	public String device_user(@RequestParam("device_id") Integer device_id){
		JSONObject retval = new JSONObject();
		
		Device device = deviceService.selectByPrimaryKey(device_id);
		if( device != null ){
			List<UserDevice> userDevice = userDeviceService.selectByDeviceId(device.getId());
			JSONArray temp = new JSONArray();
			for(UserDevice ud : userDevice){
				User user = userService.selectByPrimaryKey(ud.getUserId());
				if( user != null){
					JSONObject uTemp = new JSONObject();
					uTemp.put("id", user.getId());
					uTemp.put("username", user.getUsername());
					uTemp.put("account", user.getNumber());
					uTemp.put("nickname", user.getNickname());
					uTemp.put("avatar", user.getProfileImage());
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
			
			retval.put("status", ConstantService.statusCode_200);
			retval.put("data", dTemp);
		}else{
			retval.put("status", ConstantService.statusCode_101);
		}
		
		return retval.toString();
	}
	
	/**
	 * 改变用户和设备的绑定状态
	 * @url {base_url}/device/status
	 * @method POST
	 * @param user_id
	 * @param device_id
	 * @param status，4种状态，unbind：app端解除绑定，lock：设备端锁定，unlock：设备端解锁，delete：设备端删除
	 * @return json
	 */
	@RequestMapping(value="/status", method=RequestMethod.POST)
	@ResponseBody
	public String status(@RequestParam("user_id") Integer user_id, @RequestParam("device_id") Integer device_id, @RequestParam("status") String status){
		JSONObject retval = new JSONObject();
		
		if( status.equals("unbind") ){
			if( userDeviceService.deleteByUserIdAndDeviceId(user_id, device_id) == 1 ){
				retval.put("status", ConstantService.statusCode_200);
			}else{
				retval.put("status", ConstantService.statusCode_103);
			}
		}else if( status.equals("lock") ){
			// 检查设备与用户是否已关联
			UserDevice user_device = userDeviceService.selectByUserIdAndDeviceId(user_id, device_id);
			if( user_device != null ){
				User user = userService.selectByPrimaryKey(user_device.getUserId());
				if( user != null ){
					user.setAccepted("0");
					user.setModifyDate(new Date());
					if( userService.updateByPrimaryKey(user) == 1 ){
						retval.put("status", ConstantService.statusCode_200);
					}else{
						retval.put("status", ConstantService.statusCode_104);
					}
				}else{
					retval.put("status", ConstantService.statusCode_105);
				}
			}else{
				retval.put("status", false);
				retval.put("msg", ConstantService.statusCode_103);
			}
		}else if( status.equals("unlock") ){
			// 检查设备与用户是否已关联
			UserDevice user_device = userDeviceService.selectByUserIdAndDeviceId(user_id, device_id);
			if( user_device != null ){
				User user = userService.selectByPrimaryKey(user_device.getUserId());
				if( user != null ){
					user.setAccepted("1");
					user.setModifyDate(new Date());
					if( userService.updateByPrimaryKey(user) == 1 ){
						retval.put("status", ConstantService.statusCode_200);
					}else{
						retval.put("status", ConstantService.statusCode_104);
					}
				}else{
					retval.put("status", ConstantService.statusCode_105);
				}
			}else{
				retval.put("status", ConstantService.statusCode_103);
			}
		}else if( status.equals("delete") ){
			if( userDeviceService.deleteByUserIdAndDeviceId(user_id, device_id) == 1 ){
				retval.put("status", ConstantService.statusCode_200);
			}else{
				retval.put("status", ConstantService.statusCode_103);
			}
		}else{
			retval.put("status", ConstantService.statusCode_106);
		}
		
		return retval.toString();
	}
	
	/**
	 * 绑定单个设备
	 * @url {base_url}/device/bind
	 * @method POST
	 * @param Integer user_id
	 * @param String device_token
	 * @param String device_name
	 * @return
	 */
	@RequestMapping(value="/bind", method=RequestMethod.POST)
	@ResponseBody
	public String bind(@RequestParam("user_id") Integer user_id, @RequestParam("device_token") String device_token, @RequestParam("device_name") String device_name){
		JSONObject retval = new JSONObject();
		
		User user = userService.selectByPrimaryKey(user_id);
		if( user != null ){
			Device device = deviceService.selectByDeviceToken(device_token);
			if( device != null ){
				/*
				 * 检查绑定的设备数量，不能超过20个
				 */
				
				/*
				 * 检查绑定的设备数量，不能超过20个
				 */
				if( userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId()) == null ){
					UserDevice ud = new UserDevice();
					ud.setUserId(user_id);
					ud.setDeviceId(device.getId());
					ud.setDeviceName(device_name);
					if( userDeviceService.insert(ud) == 1 ){
						retval.put("status", ConstantService.statusCode_200);
						JSONObject temp = new JSONObject();
						temp.put("device_id", device.getId());
						temp.put("device_token", device.getDeviceToken());
						temp.put("device_fcm_token", device.getDeviceFcmToken());
						temp.put("device_name", device_name);
						temp.put("create_date", device.getCreateDate().getTime());
						temp.put("modify_date", device.getModifyDate().getTime());
						retval.put("data", temp);
					}else{
						retval.put("status", ConstantService.statusCode_107);
					}
				}else{
					retval.put("status", ConstantService.statusCode_108);
				}
			}else{
				retval.put("status", ConstantService.statusCode_101);
			}
		}else{
			retval.put("status", ConstantService.statusCode_109);
		}
		
		return retval.toString();
	}
	
	/**
	 * 重命名单个设备
	 * @url {base_url}/device/rename
	 * @method POST
	 * @param Integer user_id
	 * @param Integer device_id
	 * @param String device_name
	 * @return
	 */
	@RequestMapping(value="/rename", method=RequestMethod.POST)
	@ResponseBody
	public String rename(@RequestParam("user_id") Integer user_id, @RequestParam("device_id") Integer device_id, @RequestParam("device_name") String device_name){
		JSONObject retval = new JSONObject();
			
		UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device_id);
		if( userDevice != null ){
			userDevice.setDeviceName(device_name);
			if( userDeviceService.updateByPrimaryKey(userDevice) == 1 ){
				retval.put("status", ConstantService.statusCode_200);
			}else{
				retval.put("status", ConstantService.statusCode_110);
			}
		}else{
			retval.put("status", ConstantService.statusCode_103);
		}

		return retval.toString();
	}
	
	/**
	 * 获取设备版本更新信息
	 * @url {base_url}/device/version
	 * @method POST
	 * @param device_id 设备数据库主键
	 * @return json
	 */
	@RequestMapping(value="/version", method=RequestMethod.POST)
	@ResponseBody
	public String version(@RequestParam("device_id") Integer device_id){
		JSONObject retval = new JSONObject();
		JSONObject temp = new JSONObject();
		
		Device device = deviceService.selectByPrimaryKey(device_id);
		if( device != null ){
			retval.put("status", ConstantService.statusCode_103);
			
			Integer order_id = device.getOrderId();
			Order order = orderService.selectByPrimaryKey(order_id);
			if( order != null ){
				temp.put("last_version", order.getLastVersion());
				temp.put("download_link", order.getDownloadLink());
			}else{
				temp.put("last_version", "");
				temp.put("download_link", "");
			}
			retval.put("data", temp);
		}else{
			retval.put("status", ConstantService.statusCode_101);
		}
		
		return retval.toString();
	}
	
	/**
	 * 心跳
	 * @url {base_url}/device/beat
	 * @method POST
	 * @param device_id
	 * @return json
	 */
	@RequestMapping(value="/beat", method=RequestMethod.POST)
	@ResponseBody
	public String beat(@RequestParam("device_id") Integer device_id){
		JSONObject retval = new JSONObject();
		
		Device exist_device = this.deviceService.selectByPrimaryKey(device_id);
		if( exist_device != null ){
			
			Device device = new Device();
			device.setId(exist_device.getId());
			device.setHeartbeatTime(new Date());
			if( deviceService.updateByPrimaryKeySelective(device) == 1 ){
				retval.put("status", ConstantService.statusCode_200);
			}else{
				retval.put("status", false);
				retval.put("msg", ConstantService.statusCode_111);
			}
		}else{
			retval.put("status", ConstantService.statusCode_101);
		}
		
		return retval.toString();
	}
}
