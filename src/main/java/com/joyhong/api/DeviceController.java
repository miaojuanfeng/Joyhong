package com.joyhong.api;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.Order;
import com.joyhong.model.Ota;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.OrderService;
import com.joyhong.service.OtaService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.common.PushService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 设备管理控制器
 * @url {base_url}/device/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping(value="/device", produces="application/json;charset=UTF-8")
public class DeviceController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private PushService pushService;
	
	@Autowired
	private OtaService otaService;
	
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
			User user = userService.selectByPrimaryKey(user_id);
			Device device = deviceService.selectByPrimaryKey(device_id);
			UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device_id);
			if( user != null && device != null && userDevice != null ){
				/*
				 * 推送解绑消息
				 */
				JSONObject body = new JSONObject();
				JSONArray desc_temp = new JSONArray();
				desc_temp.add("unbind user");
				JSONArray url_temp = new JSONArray();
				body.put("sender_id", user.getId());
				body.put("sender_name", user.getNickname());
				//
				JSONObject ut = new JSONObject();
				ut.put("username", user.getUsername());
				ut.put("account", user.getNumber());
				ut.put("nickname", user.getNickname());
				ut.put("avatar", user.getProfileImage());
				ut.put("platform", user.getPlatform());
				ut.put("accepted", user.getAccepted());
				body.put("sender_user", ut);
				//
				body.put("receive_id", device.getId());
				body.put("receive_name", userDevice.getDeviceName());
				body.put("to_fcm_token", device.getDeviceFcmToken());
				body.put("text", desc_temp);
				body.put("url", url_temp);
				body.put("type", "text");
				body.put("platform", "app");
				body.put("time", (new Date()).getTime()/1000);
				pushService.push(
						user.getId(),
						user.getNickname(), 
						device.getId(), 
						userDevice.getDeviceName(), 
						device.getDeviceFcmToken(), 
						"unbind user", 
						"", 
						"text", 
						"app", 
						"Receive a message from App", 
						body.toString());
				userDeviceService.deleteByUserIdAndDeviceId(user_id, device_id);
			}
			retval.put("status", ConstantService.statusCode_200);
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
				 * 检查绑定的设备数量，不能超过order限制
				 */
				Order order = orderService.selectByPrimaryKey(device.getOrderId());
				if( order != null ){
					List<UserDevice> userDevice = userDeviceService.selectByDeviceId(device.getId());
					if( userDevice.size() < order.getMaxBind() ){
						/*
						 * 绑定设备
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
								/*
								 * 推送绑定消息
								 */
								JSONObject body = new JSONObject();
								JSONArray desc_temp = new JSONArray();
								desc_temp.add("new user");
								JSONArray url_temp = new JSONArray();
								body.put("sender_id", user.getId());
								body.put("sender_name", user.getNickname());
								//
								JSONObject ut = new JSONObject();
								ut.put("username", user.getUsername());
								ut.put("account", user.getNumber());
								ut.put("nickname", user.getNickname());
								ut.put("avatar", user.getProfileImage());
								ut.put("platform", user.getPlatform());
								ut.put("accepted", user.getAccepted());
								body.put("sender_user", ut);
								//
								body.put("receive_id", device.getId());
								body.put("receive_name", device_name);
								body.put("to_fcm_token", device.getDeviceFcmToken());
								body.put("text", desc_temp);
								body.put("url", url_temp);
								body.put("type", "text");
								body.put("platform", "app");
								body.put("time", (new Date()).getTime()/1000);
								pushService.push(
										user.getId(),
										user.getNickname(), 
										device.getId(), 
										device_name, 
										device.getDeviceFcmToken(), 
										"new user", 
										"", 
										"text", 
										"app", 
										"Receive a message from App", 
										body.toString());
							}else{
								retval.put("status", ConstantService.statusCode_107);
							}
						}else{
							retval.put("status", ConstantService.statusCode_108);
						}
					}else{
						retval.put("status", ConstantService.statusCode_117);
					}
				}else{
					retval.put("status", ConstantService.statusCode_112);
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
	 * @param version 设备当前版本编号
	 * @return json
	 */
	@RequestMapping(value="/version", method=RequestMethod.POST)
	@ResponseBody
	public String version(@RequestParam("device_id") Integer device_id, @RequestParam("version") Integer version){
		JSONObject retval = new JSONObject();
		JSONObject temp = new JSONObject();
		
		Device device = deviceService.selectByPrimaryKey(device_id);
		if( device != null ){
			retval.put("status", ConstantService.statusCode_103);
			
			Integer order_id = device.getOrderId();
			Order order = orderService.selectByPrimaryKey(order_id);
			if( order != null ){
				Ota ota = otaService.selectByOrderIdAndVersion(order_id, version);
				if( ota != null ){
					retval.put("status", ConstantService.statusCode_200);
					temp.put("last_version", ota.getLastVersion());
					temp.put("download_link", ConstantService.ossUrl + ota.getDownloadLink());
					temp.put("version_desc", ota.getVersionDesc());
					retval.put("data", temp);
				}else{
					retval.put("status", ConstantService.statusCode_118);
				}
			}else{
				retval.put("status", ConstantService.statusCode_112);
			}
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
	
	/**
	 * 根据城市名搜索得到经纬度等信息
	 * @param city_name
	 * @return
	 */
	@RequestMapping(value="/place", method=RequestMethod.POST)
	@ResponseBody
	public String place(@RequestParam("city_name") String city_name){
		JSONObject retval = new JSONObject();
		
		String[] keyArr = {
				"AIzaSyBGpcn9Q31LBNaJqRLALz4XS523NgDcKkA", 
				"AIzaSyDpxi5Oozr1yFW0xj96hUEzsN_veRwTOiA", 
				"AIzaSyDZqUytGU8R-fmrr6zZidCvGSv7Kz6EBzQ", 
				"AIzaSyA42VWexikFUtarRUwCxOrhiAE5rdt3MP0", 
				"AIzaSyD_RpZNX0DigiGe8fArePk0Zz-n8HMqFvI"};
		
		for(int k=0;k<keyArr.length;k++){
			try{
				String key = keyArr[k];
				String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+URLEncoder.encode(city_name, "utf-8")+"&key="+key+"&language=cn";
				
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet(url);
	
				CloseableHttpResponse response = httpclient.execute(httpget);
				if (response.getStatusLine().getStatusCode() == 200) {
	                String result = EntityUtils.toString(response.getEntity());
	                JSONObject resultJson = JSONObject.fromObject(result);
					
//	                System.out.println(resultJson.toString());
	                
	                if( resultJson.getString("status").equals("OK") ){
	                	retval.put("status", ConstantService.statusCode_200);
	                	//
	                	JSONArray results = resultJson.getJSONArray("results");
	                	JSONArray temp = new JSONArray();
	                	for(int i=0;i<results.size();i++){
	                		JSONObject city = results.getJSONObject(i);
		                	JSONObject location = city.getJSONObject("geometry").getJSONObject("location");
		                	JSONObject t = new JSONObject();
		                	t.put("name", city.getString("name"));
		                	t.put("formatted_address", city.getString("formatted_address"));
		                	t.put("lat", location.getString("lat"));
		                	t.put("lng", location.getString("lng"));
		                	temp.add(t);
	                	}
	                	retval.put("data", temp);
	                	return retval.toString();
	                }else if( resultJson.getString("status").equals("ZERO_RESULTS") ){
	                	retval.put("status", ConstantService.statusCode_113);
	                	return retval.toString(); 
	                }else if( resultJson.getString("status").equals("OVER_QUERY_LIMIT") ){
	                	if( k == keyArr.length-1 ){
	                		logger.info("Google place exhausted the key: " + city_name);
	                	}
	                	continue;
	                }else if( resultJson.getString("status").equals("REQUEST_DENIED") ){
	                	retval.put("status", ConstantService.statusCode_114);
	                	return retval.toString(); 
	                }else if( resultJson.getString("status").equals("INVALID_REQUEST") ){
	                	retval.put("status", ConstantService.statusCode_115);
	                	return retval.toString(); 
	                }else{
	                	logger.info("Google place unkonw status: " + resultJson.toString());
	                	break;
	                }
				}else{
					logger.info("Google place network error: " + city_name);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
			}
		}
		
		retval.put("status", ConstantService.statusCode_116);
		return retval.toString();
	}
}
