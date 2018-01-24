package com.joyhong.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Config;
import com.joyhong.model.Device;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.ConfigService;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.FuncService;
import com.joyhong.service.common.ConstantService;

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
	
//	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FuncService funcService;
	
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
		
		if( !funcService.isNumeric(user_imei) ){
			retval.put("status", ConstantService.statusCode_401);
			return retval.toString();
		}
		
		if( user_imei.length() != 15 ){
			retval.put("status", ConstantService.statusCode_402);
			return retval.toString();
		}
		
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
				
				retval.put("status", ConstantService.statusCode_200);
				retval.put("data", uJson);
			}else{
				retval.put("status", ConstantService.statusCode_403);
			}
		}else{
			uJson.put("user_id", user.getId());
			uJson.put("user_number", user.getNumber());
			uJson.put("user_nickname", user.getNickname());
			
			retval.put("status", ConstantService.statusCode_200);
			retval.put("data", uJson);
		}
		
		return retval.toString();
	}
	
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
				retval.put("status", ConstantService.statusCode_200);
			}else{
				retval.put("status", ConstantService.statusCode_404);
			}
		}else{
			retval.put("status", ConstantService.statusCode_405);
		}
		
		return retval.toString();
	}
	
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
				temp.add(uTemp);
			}
		}
		retval.put("status", ConstantService.statusCode_200);
		retval.put("data", temp);
		
		return retval.toString();
	}
	
	/**
	 * 获取app版本更新信息
	 * @url {base_url}/user/version
	 * @method POST
	 * @return json
	 */
	@RequestMapping(value="/version", method=RequestMethod.POST)
	@ResponseBody
	public String version(){
		JSONObject retval = new JSONObject();
		
		Config version = configService.selectByTitle("Version");
		retval.put("status", ConstantService.statusCode_200);
		retval.put("data", version.getValue());
		
		return retval.toString();
	}
}
