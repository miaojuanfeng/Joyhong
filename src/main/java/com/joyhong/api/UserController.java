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
 * 用户管理控制器
 * @url {base_url}/user/{method}
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
	
	@Autowired
	private UserService userService;
	
	/**
	 * App用户注册
	 * @url {base_url}/user/signup
	 * @method POST
	 * @param username
	 * @param password
	 * @return json
	 */
//	@RequestMapping(value="/signup", method=RequestMethod.POST)
//	@ResponseBody
//	public String signup(@RequestParam("username") String username, @RequestParam("password") String password){
//		JSONObject retval = new JSONObject();
//		
//		User user = userService.selectByUsername(username);
//		if( user == null ){
//			password = DigestUtils.md5Hex(password);
//	        
//			user = new User();
//			user.setUsername(username);
//			user.setPassword(password);
//			user.setNickname(username);
//			user.setProfileImage("");
//			user.setPlatform("app");
//			user.setAccepted("1");
//			user.setCreateDate(new Date());
//			user.setModifyDate(new Date());
//			user.setDeleted(0);
//			if( userService.insert(user) == 1 ){
//				JSONObject uJson = new JSONObject();
//				uJson.put("user_id", user.getId());
//				uJson.put("user_token", get_user_token(user));
//				
//				retval.put("status", true);
//				retval.put("data", uJson);
//			}else{
//				retval.put("status", false);
//				retval.put("msg", "User registration failed, please try again later");
//			}
//		}else{
//			retval.put("status", false);
//			retval.put("msg", "The username has been registered");
//		}
//		
//		return retval.toString();
//	}
	
	/**
	 * App用户登录
	 * @url {base_url}/user/signin
	 * @method POST
	 * @param user_imei
	 * @return json
	 */
	@RequestMapping(value="/signin", method=RequestMethod.POST)
	@ResponseBody
	public String signin(@RequestParam("user_imei") String user_imei){
		JSONObject retval = new JSONObject();
		JSONObject uJson = new JSONObject();
		
		User user = userService.selectByUsername(user_imei);
		if( user == null ){
			user = new User();
			user.setUsername(user_imei);
			int user_number = 0;
			while(true){
				user_number = (int)((Math.random()*9+1)*1000000000);
				User exist_user = userService.selectByNumber(user_number);
				if( exist_user == null ){
					break;
				}
			}
			user.setNumber(user_number);
			user.setNickname("");
			user.setProfileImage("");
			user.setPlatform("app");
			user.setAccepted("1");
			user.setCreateDate(new Date());
			user.setModifyDate(new Date());
			user.setDeleted(0);
			if( userService.insert(user) == 1 ){
				uJson.put("user_id", user.getId());
				uJson.put("user_number", user.getNumber());
				uJson.put("user_nickname", user.getNickname());
				
				retval.put("status", true);
				retval.put("data", uJson);
			}else{
				retval.put("status", false);
				retval.put("msg", "User registration failed, please try again later");
			}
		}else{
			uJson.put("user_id", user.getId());
			uJson.put("user_number", user.getNumber());
			uJson.put("user_nickname", user.getNickname());
			
			retval.put("status", true);
			retval.put("data", uJson);
		}
		
		return retval.toString();
	}
	
	/**
	 * App自动登录
	 * @url {base_url}/user/auto_signin
	 * @method POST
	 * @param user_id
	 * @param user_token
	 * @return json
	 */
//	@RequestMapping(value="/auto_signin", method=RequestMethod.POST)
//	@ResponseBody
//	public String auto_signin(@RequestParam("user_id") Integer user_id, @RequestParam("user_token") String user_token){
//		JSONObject retval = new JSONObject();
//		
//		User user = userService.selectByPrimaryKey(user_id);
//		if( user != null ){
//			if( get_user_token(user).equals(user_token) ){
//				JSONObject uJson = new JSONObject();
//				uJson.put("user_id", user.getId());
//				uJson.put("user_token", get_user_token(user));
//				
//				retval.put("status", true);
//				retval.put("data", uJson);
//			}else{
//				retval.put("status", false);
//				retval.put("msg", "Incorrect user token");
//			}
//		}else{
//			retval.put("status", false);
//			retval.put("msg", "Unable to find the user");
//		}
//		
//		return retval.toString();
//	}
	
	/**
	 * 更新账号信息
	 * @url {base_url}/user/update_profile
	 * @method POST
	 * @param user_id
	 * @param user_nickname
	 * @return json
	 */
	@RequestMapping(value="/update_profile", method=RequestMethod.POST)
	@ResponseBody
	public String update_profile(@RequestParam("user_id") Integer user_id, @RequestParam("user_nickname") String user_nickname){
		JSONObject retval = new JSONObject();
		
		User user = userService.selectByPrimaryKey(user_id);
		if( user != null ){
			user.setNickname(user_nickname);
			user.setModifyDate(new Date());
			if( userService.updateByPrimaryKey(user) == 1 ){
				retval.put("status", true);
				retval.put("msg", "Success");
			}else{
				retval.put("status", false);
				retval.put("msg", "Update user profile failed, please try again later");
			}
		}else{
			retval.put("status", false);
			retval.put("msg", "Unable to find the user");
		}
		
		return retval.toString();
	}
	
	/**
	 * 计算user token
	 * @param user
	 * @return String
	 */
//	private String get_user_token(User user){
//		String user_id = String.valueOf(user.getId());
//		String user_name = user.getUsername();
//		String user_platform = user.getPlatform();
//		String random_key = "#N5$8cA&a*X";
//		
//		String user_token = user_platform + user_id + user_name + random_key;
//		
//		return DigestUtils.md5Hex(user_token);
//	}
	
	/**
	 * 获取用户绑定的设备列表
	 * @url {base_url}/user/user_device
	 * @method POST
	 * @param user_id 数据库id
	 * @return json
	 */
	@RequestMapping(value="/user_device", method=RequestMethod.POST)
	@ResponseBody
	public String user_device(@RequestParam("user_id") Integer user_id){
		JSONObject retval = new JSONObject();
		
		List<UserDevice> userDevice = userDeviceService.selectByUserId(user_id);
		JSONArray temp = new JSONArray();
		for(UserDevice ud : userDevice){
			Device device = deviceService.selectByPrimaryKey(ud.getDeviceId());
			if( device != null){
				JSONObject uTemp = new JSONObject();
				uTemp.put("device_id", device.getId());
				uTemp.put("device_token", device.getDeviceToken());
				uTemp.put("device_fcm_token", device.getDeviceFcmToken());
				uTemp.put("device_name", ud.getDeviceName());
				uTemp.put("create_date", device.getCreateDate().getTime());
				uTemp.put("modify_date", device.getModifyDate().getTime());
				temp.add(uTemp);
			}
		}
		retval.put("status", true);
		retval.put("data", temp);
		
		return retval.toString();
	}
	
	
	
//	@RequestMapping(value="/push", method=RequestMethod.GET)
//	@ResponseBody
//	public void push(){
//		System.out.println(notificationService.push(
//				12345, 
//				"sender_name", 
//				67890, 
//				"receive_name", 
//				"fmUSEMs6Bzc:APA91bHvuuezPri_rGkFYiR8TJK8jrBszsKAMltUT4PAWYeyxN3tWhxMEC-_gkz1b4EgOhkESi7s_OCDWgNiOR2kMfo_KBA7gdCQUR-JCZXWD0zn4Qf6JwNDFouB52uROxeWdLbQ_G0H", 
//				"text", 
//				"image_url", 
//				"video_url", 
//				"text", 
//				"facebook", 
//				"title", 
//				"body"));
//	}
}
