package com.joyhong.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

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
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;

import net.sf.json.JSONObject;

/**
 * Facebook消息控制器
 * @url https://well.bsimb.cn/facebook/{method}
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
						System.out.println(e.getMessage());
					}
				} else {
				  	// Responds with '403 Forbidden' if verify tokens do not match
					response.setHeader("Content-Type", "text/plain");
					response.setStatus(403);
					try{
						response.getOutputStream().write("error".getBytes());
					}catch(IOException e){
						System.out.println(e.getMessage());
					}
				}
	      	}else{
	      		response.setHeader("Content-Type", "text/plain");
				response.setStatus(403);
				try{
					response.getOutputStream().write("error".getBytes());
				}catch(IOException e){
					System.out.println(e.getMessage());
				}
	      	}
	      	
		}else if( "POST".equalsIgnoreCase(request.getMethod()) ){
			try{
				BufferedReader in = request.getReader();
				String line = null;
				String postdata = "";
				while((line = in.readLine()) != null){ 
					postdata = postdata + line;
				}
				
				JSONObject json_obj = JSONObject.fromObject(postdata);
				
				JSONObject message = json_obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message");
				
				if( message.has("attachments") ){
					
					JSONObject attachments = message.getJSONArray("attachments").getJSONObject(0);
					
					String type = attachments.getString("type");
					String url = attachments.getJSONObject("payload").getString("url");
					String oldUrl = url.replace("/", "\\/");
					String fileUrl = url.replace("\\", "");
					                     
			        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
			        fileName = fileName.substring(0, fileName.lastIndexOf("?"));
  
			        String filePath = "/home/wwwroot/default/facebook/attachments/" + type + "/";   
			        this.saveUrlAs(fileUrl, filePath, fileName);
			        
			        postdata = postdata.replace(oldUrl, "http://47.89.32.89/facebook/attachments/" + type + fileName);
				}
				
				String sender_id = json_obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
				
				String postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Sorry, I can not understand what you say.'}}";
				if( message.has("text") ){
					String msgStr = message.getString("text");
					if( msgStr.equals("Hello") ){
						postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'hello world'}}";
					}else{
						String[] msg = msgStr.split(":", 2);
						if( msg[0].equals("bindDevice") && msg[1] != null ){
		        			String device_id = msg[1];
		        			Device device = deviceService.selectByDeviceId(device_id);
		        			if( device != null ){
		        				Integer user_id = this.insertUserIfNotExist(json_obj);
		        				insertUserDeviceAfterDelete(user_id, device.getId());
		        				postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Success, the device id is bind to " + sender_id + "'}}";
		        			}else{
		        				postJsonData = "{'recipient':{'id':'" + sender_id + "'},'message':{'text':'Sorry, the device id is not yet registered'}}";
		        			}
		        		}
					}
				}
				
				String url = "https://graph.facebook.com/v2.6/me/messages?access_token=EAAHQ2lh6o2UBAAeLni23hen920ECISXqjY5SsJXPeUKrHRid3f3huz82pdu8ptYSmCI8yhGJmjc0E3InZAl3mgKZBGTjMyIZCGGniep8lnGRVaheHfZB7h0dh06YDvV5mAmFL7pdfKgOMJTP9aUFM9ZAGZAuldUPUwPG5oQoY2wIvNEaZCuZAN4DLyQqURJKH9IZD";
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
								 
				// Setting basic post request
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
//				System.out.println("nSending 'POST' request to URL : " + url);
//				System.out.println("Post Data : " + con.getResponseMessage());
//				System.out.println("Response Code : " + responseCode);
				
				FileWriter fw = null;
			    try {
			    	//如果文件存在，则追加内容；如果文件不存在，则创建文件
			    	File f=new File("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/facebook.txt");
			      	fw = new FileWriter(f, true);
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
			    PrintWriter pw = new PrintWriter(fw);
			    pw.println(postdata);
			    pw.flush();
			    try {
					fw.flush();
					pw.close();
					fw.close();
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	public Integer insertUserDeviceAfterDelete(Integer userId, Integer deviceId){
		userDeviceService.deleteByUserId(userId);
		
		UserDevice userDevice = new UserDevice();
		userDevice.setUserId(userId);
		userDevice.setDeviceId(deviceId);
		userDevice.setCreateDate(new Date());
		userDevice.setModifyDate(new Date());
		userDevice.setDeleted(0);
		
		return userDeviceService.insert(userDevice);
	}
	
	public String getUserProfile(String userId){
		JSONObject retval = new JSONObject();
		
		String filePath = "/home/wwwroot/default/facebook/attachments/users/" + userId + "/";
		String fileName = "";
		String fileUrl = "http://47.89.32.89/facebook/attachments/users/" + userId + "/";
		
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
	    	        this.saveUrlAs(json_obj.getString("profile_pic"), filePath, fileName);
	    	        retval.put("profile_pic", fileUrl + fileName);
                }
                if( json_obj.has("locale") ){
                	retval.put("locale", json_obj.getString("locale"));
                }
                if( json_obj.has("timezone") ){
                	retval.put("timezone", json_obj.getString("timezone"));
                }
                if( json_obj.has("gender") ){
                	retval.put("gender", json_obj.getString("gender"));
                }
                if( json_obj.has("id") ){
                	retval.put("id", json_obj.getString("id"));
                }
			}
		} catch (Exception e) {
	        logger.info(e.getMessage());
	    }
		return retval.toString();
	}
	
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
			user.setUsername(sender_id);
			user.setPassword(sender_id);
			user.setNickname(username);
			user.setProfile(u);
			user.setPlatform("facebook");
			user.setCreateDate(new Date());
			user.setModifyDate(new Date());
			user.setDeleted(0);
			userService.insert(user);
			return user.getId();
		}else{
			return user.getId();
		}
	}
	
	/**
	 * 同步图片视频到本地服务器
	 * @param url
	 * @param filePath
	 * @param method
	 */
	private void saveUrlAs(String url, String filePath, String fileName){   
	     FileOutputStream fileOut = null;  
	     HttpURLConnection conn = null;  
	     InputStream inputStream = null;  
	     try {
	    	 if (!filePath.endsWith("/")) {  
	             filePath += "/";
	         }
	    	 File file = new File(filePath);
	    	 if(!file.exists()){
	    		 file .mkdir();
	    	 }
	    	 
	         URL httpUrl=new URL(url);  
	         conn=(HttpURLConnection) httpUrl.openConnection();  
	         conn.setRequestMethod("GET");  
	         conn.setDoInput(true);    
	         conn.setDoOutput(true);   
	         conn.setUseCaches(false);  
	         conn.connect();  
	         inputStream=conn.getInputStream();  
	         BufferedInputStream bis = new BufferedInputStream(inputStream);
	         
	         fileOut = new FileOutputStream(filePath+fileName);  
	         BufferedOutputStream bos = new BufferedOutputStream(fileOut);  
	           
	         byte[] buf = new byte[4096];
	         int length = bis.read(buf);
	         while(length != -1)  
	         {  
	             bos.write(buf, 0, length);  
	             length = bis.read(buf);  
	         }
	         bos.close();
	         bis.close();
	         conn.disconnect();
	    } catch (Exception e)  {
	    	logger.info(e.getMessage());
	    }
	 }
}
