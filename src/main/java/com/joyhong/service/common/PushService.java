package com.joyhong.service.common;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.model.Notification;
import com.joyhong.service.NotificationService;

import net.sf.json.JSONObject;

@Service
public class PushService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MD5Service md5Service;
	
//	/**
//	 *  推送消息
//	 *  @param sender_id, 
//	 *	@param sender_name, 
//	 *	@param receive_id, 
//	 *	@param receive_name, 
//	 *	@param to_fcm_token,
//	 *	@param text,
//	 *	@param image_url,
//	 *	@param video_url,
//	 *	@param type,
//	 *	@param platform,
//	 *	@param title,
//	 *	@param body
//	 *  @return 1 success
//	 *  		0 failed
//	 */
//	public int push(Integer sender_id, 
//					String sender_name, 
//					Integer receive_id, 
//					String receive_name, 
//					String to_fcm_token,
//					String text,
//					String image_url,
//					String video_url,
//					String type,
//					String platform,
//					String title,
//					String body) {
//		try{
//		  String url = "https://fcm.googleapis.com/fcm/send";
//		  URL obj = new URL(url);
//		  HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		 
//		  // Setting basic post request
//		  con.setConnectTimeout(20*1000);
//		  con.setReadTimeout(20*1000);
//		  con.setRequestMethod("POST");
//		  con.setRequestProperty("Content-Type","application/json");
//		  con.setRequestProperty("Authorization","key=AAAAbSeZOds:APA91bFNh-3EEXCQj6RZUh4hIkvYg51PCtB_g_vPmQ2gFWLy_eBWyYCWImSge2D9xaB6YsfR_V_EEudrN6ue55J0MZriOzwcoC4_yI8_v6rvgxjdpIdLcBcEDZWl0gdUBHHY0IYViFv8");
//		  
//		  String postJsonData = "{\"notification\": {\"title\": \""+title+"\",\"body\": \""+body+"\",},\"to\": \""+to_fcm_token+"\"}";
//		  
//		  // Send post request
//		  con.setDoOutput(true);
//		  DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//		  wr.writeBytes(postJsonData);
//		  wr.flush();
//		  wr.close();
//		  
//		  int responseCode = con.getResponseCode();
//		  if( responseCode == 200 ){
//			  // 请求返回的数据
//	          InputStream in = con.getInputStream();
//	          String result = null;
//	          
//	          byte[] data = new byte[in.available()];
//	          in.read(data);
//	          // 转成字符串
//	          result = new String(data);
//	          JSONObject resultJson = JSONObject.fromObject(result);
//	          //
//	          Notification notification = new Notification();
//	          notification.setSenderId(sender_id);
//	          notification.setSenderName(sender_name);
//	          notification.setReceiveId(receive_id);
//	          notification.setReceiveName(receive_name);
//	          notification.setToFcmToken(to_fcm_token);
//	          notification.setType(type);
//        	  notification.setText(text);
//        	  notification.setImageUrl(image_url);
//        	  notification.setVideoUrl(video_url);
//        	  notification.setPlatform(platform);
//	          //
//	          if( resultJson.getString("success").equals("1") ){
//	        	  notification.setReceived("1");
//	        	  notification.setFailedReason("");
//	          }else{
//	        	  String reason = resultJson.getJSONArray("results").getJSONObject(0).getString("error");
//	        	  notification.setReceived("0");
//	        	  notification.setFailedReason(reason);
//	          }
//	          
//	          if( notificationService.insert(notification) == 1 ){
//	        	  return Integer.valueOf(resultJson.getString("success"));
//	          }
//		  }else{
//			  logger.info("fcm network error");
//		  }
//          
//		}catch(Exception e){
//			logger.info(e.getMessage());
//		}
//		
//		return 0;
//	}
	
	/**
	 *  推送消息
	 *  @param sender_id, 
	 *	@param sender_name, 
	 *	@param receive_id, 
	 *	@param receive_name, 
	 *	@param to_fcm_token,
	 *	@param text,
	 *	@param image_url,
	 *	@param video_url,
	 *	@param type,
	 *	@param platform,
	 *	@param title,
	 *	@param body
	 *  @return 1 success
	 *  		0 failed
	 */
	public int push(Integer sender_id, 
					String sender_name, 
					Integer receive_id, 
					String receive_name, 
					String to_fcm_token,
					String text,
					String image_url,
					String video_url,
					String type,
					String platform,
					String title,
					String body) {
		
		String protocol = "http://";
		
		String access_id = "2100281324";
		String device_token = to_fcm_token;
		
		String host = null;
		String url = null;
		String sign = null;
		String postJsonData = null;
		
		String message_type = "2";
		
		JSONObject messageObj = new JSONObject();
		messageObj.put("content", body);
		messageObj.put("title", title);
		messageObj.put("vibrate", 1);
		String message = messageObj.toString();
		
		Long timestamp = new Date().getTime()/1000;
		
		String secret_key = "4a7df2bc9e53627c764eec7ae9b46716";
		
//		System.out.println("GET"+host+"access_id="+access_id+"device_token="+device_token+"message="+message+"message_type="+message_type+"timestamp="+timestamp+secret_key);
//		System.out.println(sign);
		
		CloseableHttpClient httpclient = null;
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		try{
			/*
			 * 推送实体对象
			 */
			Notification notification = new Notification();
			notification.setSenderId(sender_id);
			notification.setSenderName(sender_name);
			notification.setReceiveId(receive_id);
			notification.setReceiveName(receive_name);
			notification.setToFcmToken(to_fcm_token);
			notification.setType(type);
			notification.setText(text);
			notification.setImageUrl(image_url);
			notification.setVideoUrl(video_url);
			notification.setPlatform(platform);
			/*
			 * 查询该Token是否已注册
			 * 经过测试发现这个方法是无效的，腾讯垃圾
			 */
//			host = "openapi.xg.qq.com/v2/application/get_app_token_info";
//			url = protocol + host;
//			String signCode = md5Service.encryptMD5("GET"+host+"access_id="+access_id+"device_token="+device_token+"timestamp="+timestamp+secret_key);
//			postJsonData = "access_id="+access_id+"&device_token="+device_token+"&timestamp="+timestamp+"&sign="+signCode;
//			
//			httpclient = HttpClients.createDefault();
//			httpget = new HttpGet(url+"?"+postJsonData);
//			
//			System.out.println(url+"?"+postJsonData);
//
//			response = httpclient.execute(httpget);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				String result = EntityUtils.toString(response.getEntity());
//                JSONObject resultJson = JSONObject.fromObject(result).getJSONObject("result");
//                /*
//                 * 如果未注册，写入数据库后返回
//                 */
//                if( resultJson.getString("isReg").equals("0") ){
//					notification.setReceived("0");
//					notification.setFailedReason("MissingRegistration");
//					notificationService.insert(notification);
//					return 0;
//				}
//			}
			/*
			 * 推送消息
			 */
			host = "openapi.xg.qq.com/v2/push/single_device";
			url = protocol + host;
			sign = md5Service.encryptMD5("GET"+host+"access_id="+access_id+"device_token="+device_token+"message="+message+"message_type="+message_type+"timestamp="+timestamp+secret_key);
			postJsonData = "access_id="+access_id+"&device_token="+device_token+"&message="+URLEncoder.encode(message, "utf-8")+"&message_type="+message_type+"&timestamp="+timestamp+"&sign="+sign;
			
			httpclient = HttpClients.createDefault();
			httpget = new HttpGet(url+"?"+postJsonData);
			
			System.out.println(url+"?"+postJsonData);

			response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject resultJson = JSONObject.fromObject(result);
                
				//
				if( resultJson.getString("ret_code").equals("0") ){
					notification.setReceived("1");
					notification.setFailedReason("");
				}else{
					String reason = resultJson.getString("err_msg");
					notification.setReceived("0");
					notification.setFailedReason(reason);
				}
  
				if( notificationService.insert(notification) == 1 ){
					return 1;
				}
			}else{
				logger.info("fcm network error");
			}
		} catch (Exception e) {
	        logger.info(e.getMessage());
	    }
		
		return 0;
	}
}
