package com.joyhong.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.FileService;
import com.joyhong.service.common.PushService;

import net.sf.json.JSONObject;

/**
 * Facebook消息控制器
 * @url {base_url}/facebook/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/facebook")
public class FacebookController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private PushService pushService;
	
	/**
	 * 监听facebook发来的消息
	 * @url Invoked by the Facebook server
	 */
	@RequestMapping(value="/listener", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public void listener(HttpServletRequest request, HttpServletResponse response){
		if( "GET".equalsIgnoreCase(request.getMethod()) ){
			
			String verify_token="q6pQ9fSPNspIv7CvMOaEd2nI0G2WZ3et";
			
	        String mode = request.getParameter("hub.mode");
	      	String token = request.getParameter("hub.verify_token");
	      	String challenge = request.getParameter("hub.challenge");
	      	
	      	if (mode != null && token != null && mode.length()>0 && token.length()>0) {
				// Checks the mode and token sent is correct
				if (mode.equals("subscribe") && token.equals(verify_token)) {
				  	// Responds with the challenge token from the request
					response.setHeader("Content-Type", "text/plain");
					response.setStatus(200);
					try{
						response.getOutputStream().write(challenge.getBytes());
					}catch(IOException e){
						logger.info(e.getMessage());
					}
				} else {
				  	// Responds with '403 Forbidden' if verify tokens do not match
					response.setHeader("Content-Type", "text/plain");
					response.setStatus(403);
					try{
						response.getOutputStream().write("error".getBytes());
					}catch(IOException e){
						logger.info(e.getMessage());
					}
				}
	      	}else{
	      		response.setHeader("Content-Type", "text/plain");
				response.setStatus(403);
				try{
					response.getOutputStream().write("error".getBytes());
				}catch(IOException e){
					logger.info(e.getMessage());
				}
	      	}
	      	
		}else if( "POST".equalsIgnoreCase(request.getMethod()) ){
			try{
				BufferedReader in = request.getReader();
				String line = null;
				String postdata = "";
				String msgStr = "";
				String image_url = "";
				String video_url = "";
				String type = "text";
				while((line = in.readLine()) != null){ 
					postdata = postdata + line;
				}
				
				fileService.savePostData("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/facebook2.txt", postdata);
				
				JSONObject json_obj = JSONObject.fromObject(postdata);
				JSONObject message = json_obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message");
				String sender_id = json_obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
				
				if( !sender_id.equals("867139310126070") ){
					if( message.has("attachments") ){
						
						JSONObject attachments = message.getJSONArray("attachments").getJSONObject(0);
						
						type = attachments.getString("type");
						String url = attachments.getJSONObject("payload").getString("url");
						String oldUrl = url.replace("/", "\\/");
						String fileUrl = url.replace("\\", "");
						                     
				        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
				        fileName = fileName.substring(0, fileName.lastIndexOf("?"));
	  
				        String filePath = "/home/wwwroot/default/facebook/attachments/" + type + "/";   
				        fileService.saveUrlAs(fileUrl, filePath, fileName);
				        
				        postdata = postdata.replace(oldUrl, "http://47.75.40.129/facebook/attachments/" + type + fileName);
				        if( type.equals("image") ){
				        	image_url = "http://47.75.40.129/facebook/attachments/" + type + "/" + fileName;
				        }else if( type.equals("video") ){
				        	video_url = "http://47.75.40.129/facebook/attachments/" + type + "/" + fileName;
				        }
					}
					
					String postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Sorry, I can not understand what you say.'}}";
					if( message.has("text") ){
						msgStr = message.getString("text");
						if( msgStr.equals("Hello") || msgStr.equals("hello") ){
							postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'hello world'}}";
						}else{
							String msg = msgStr;
							if( msg.startsWith("bd") ){
			        			String device_token = msg.substring(2);
			        			Device device = deviceService.selectByDeviceToken(device_token);
			        			if( device != null ){
			        				Integer user_id = this.insertUserIfNotExist(json_obj);
			        				insertUserDeviceAfterDelete(user_id, device.getId());
			        				postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Successful Binding!'}}";
			        			}else{
			        				postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Sorry, the device token is not yet registered'}}";
			        			}
			        		}
						}
					}
					String url = "https://graph.facebook.com/v2.6/me/messages?access_token=EAAC3x3FZBZBv4BAKDi2rfE3FY8oMtla3fS7ngWJ4D68PfEWY37lmK4Xb1ag6c48AZA49Ec6jMIdNuZBynTxKAzWo5qgyuacNtqBZA66cVAp7E3KC9fmDyjBZBGhsFoeAZC1KgsqDdtgGPlIfWjN0cYeijxOaexdgZBrfEi4rrgkaVVNJrFZBiZB1vs";
					URL obj = new URL(url);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
									 
					// Setting basic post request
					con.setConnectTimeout(20*1000);
					con.setReadTimeout(20*1000);
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type","application/json");
									 
					// Send post request
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(postJsonData);
					wr.flush();
					wr.close();
					/*
					 * not work comment this line
					 */
					int responseCode = con.getResponseCode();
					if( responseCode != 200 ){
						logger.info("Response Code : " + responseCode);
					}
//					System.out.println("nSending 'POST' request to URL : " + url);
//					System.out.println("Post Data : " + con.getResponseMessage());
//					System.out.println("Response Code : " + responseCode);
					
					/*
					 * 推送在下
					 */
					if( type.equals("image") || type.equals("video") ){
					    User user = userService.selectByUsername(sender_id);
						if( user != null ){
							List<UserDevice> ud = userDeviceService.selectByUserId(user.getId());
							if( ud != null ){
								UserDevice userDevice = ud.get(0);
								Device device = deviceService.selectByPrimaryKey(userDevice.getDeviceId());
								JSONObject body = new JSONObject();
								body.put("sender_id", user.getId());
								body.put("sender_name", user.getNickname());
								//
								JSONObject temp = new JSONObject();
								temp.put("username", user.getUsername());
								temp.put("number", user.getNumber());
								temp.put("nickname", user.getNickname());
								temp.put("account", user.getNumber());
								temp.put("profile_image", user.getProfileImage());
								temp.put("platform", user.getPlatform());
								temp.put("accepted", user.getAccepted());
								body.put("sender_user", temp);
								//
								body.put("receive_id", device.getId());
								body.put("receive_name", userDevice.getDeviceName());
								body.put("to_fcm_token", device.getDeviceFcmToken());
								body.put("text", msgStr);
								body.put("image_url", image_url);
								body.put("video_url", video_url);
								body.put("type", type);
								body.put("platform", "facebook");
								pushService.push(
										user.getId(),
										user.getNickname(), 
										device.getId(), 
										userDevice.getDeviceName(), 
										device.getDeviceFcmToken(), 
										msgStr, 
										image_url, 
										video_url, 
										type, 
										"facebook", 
										"Receive a message from Facebook", 
										body.toString().replace("\"", "\\\""));
							}
						}
					}
					/*
					 * 推送在上
					 */
					
					fileService.savePostData("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/facebook.txt", postdata);
				}
			}catch(IOException e){
				logger.info(e.getMessage());
			}
		}
	}
	
	/**
	 * 插入用户与设备之间的关联信息
	 * @param userId
	 * @param deviceId
	 * @return Integer
	 */
	private Integer insertUserDeviceAfterDelete(Integer userId, Integer deviceId){
		userDeviceService.deleteByUserId(userId);
		
		UserDevice userDevice = new UserDevice();
		userDevice.setUserId(userId);
		userDevice.setDeviceId(deviceId);
		userDevice.setDeviceName("");
		
		return userDeviceService.insert(userDevice);
	}
	
	/**
	 * 获取用户信息并同步图片
	 * @param userId
	 * @return json
	 */
	private String getUserProfile(String userId){
		JSONObject retval = new JSONObject();
		
		String filePath = "/home/wwwroot/default/facebook/attachments/users/" + userId + "/";
		String fileName = "";
		String fileUrl = "http://47.75.40.129/facebook/attachments/users/" + userId + "/";
		
		try{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet("https://graph.facebook.com/v2.6/"+userId+"?access_token=EAAHQ2lh6o2UBAAeLni23hen920ECISXqjY5SsJXPeUKrHRid3f3huz82pdu8ptYSmCI8yhGJmjc0E3InZAl3mgKZBGTjMyIZCGGniep8lnGRVaheHfZB7h0dh06YDvV5mAmFL7pdfKgOMJTP9aUFM9ZAGZAuldUPUwPG5oQoY2wIvNEaZCuZAN4DLyQqURJKH9IZD");

			CloseableHttpResponse response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());
                
                JSONObject json_obj = JSONObject.fromObject(str);
                
                String username = "";
                if( json_obj.has("name") ){
                	username = json_obj.getString("name");
                }else{
	                if( json_obj.has("last_name") ){
	                	username += json_obj.getString("last_name");
	                }
	                if( json_obj.has("first_name") ){
	                	username += json_obj.getString("first_name");
	                }
                }
                retval.put("username", username);
                /**
    	         * Cache profile_pic
    	         */
                if( json_obj.has("profile_pic") ){
	    	        fileName = json_obj.getString("profile_pic").substring(json_obj.getString("profile_pic").lastIndexOf("/")+1);
	    	        fileName = fileName.substring(0, fileName.lastIndexOf("?"));
	    	        fileService.saveUrlAs(json_obj.getString("profile_pic"), filePath, fileName);
	    	        retval.put("profile_pic", fileUrl + fileName);
                }
//                if( json_obj.has("locale") ){
//                	retval.put("locale", json_obj.getString("locale"));
//                }
//                if( json_obj.has("timezone") ){
//                	retval.put("timezone", json_obj.getString("timezone"));
//                }
//                if( json_obj.has("gender") ){
//                	retval.put("gender", json_obj.getString("gender"));
//                }
//                if( json_obj.has("id") ){
//                	retval.put("id", json_obj.getString("id"));
//                }
			}
		} catch (Exception e) {
	        logger.info(e.getMessage());
	    }
		return retval.toString();
	}
	
	/**
	 * 如果用户不存在就新增facebook用户
	 * @param json_obj
	 * @return Integer
	 */
	public Integer insertUserIfNotExist(JSONObject json_obj){
		String sender_id = json_obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
		com.joyhong.model.User user = userService.selectByUsername(sender_id);
		if( user == null ){
			String u = this.getUserProfile(sender_id);
			JSONObject uJson = JSONObject.fromObject(u);
			String username = "";
			if( uJson.has("username") ){
				username = uJson.getString("username");
			}
			
			user = new com.joyhong.model.User();
			int user_number = 0;
			while(true){
				user_number = (int)((Math.random()*9+1)*1000000000);
				User exist_user = userService.selectByNumber(user_number);
				if( exist_user == null ){
					break;
				}
			}
			user.setNumber(user_number);
			user.setUsername(sender_id);
			user.setNumber(0);
			user.setNickname(username);
			if( uJson.has("profile_pic") ){
				user.setProfileImage(uJson.getString("profile_pic"));
			}else{
				user.setProfileImage("");
			}
			user.setPlatform("facebook");
			user.setAccepted("1");
			userService.insert(user);
			return user.getId();
		}else{
			return user.getId();
		}
	}
	
	
}
