package com.joyhong.service.common;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

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
		try{
		  String url = "https://fcm.googleapis.com/fcm/send";
		  URL obj = new URL(url);
		  HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		 
		  // Setting basic post request
		  con.setConnectTimeout(20*1000);
		  con.setReadTimeout(20*1000);
		  con.setRequestMethod("POST");
		  con.setRequestProperty("Content-Type","application/json");
		  con.setRequestProperty("Authorization","key=AAAAbSeZOds:APA91bFNh-3EEXCQj6RZUh4hIkvYg51PCtB_g_vPmQ2gFWLy_eBWyYCWImSge2D9xaB6YsfR_V_EEudrN6ue55J0MZriOzwcoC4_yI8_v6rvgxjdpIdLcBcEDZWl0gdUBHHY0IYViFv8");
		  
		  String postJsonData = "{\"notification\": {\"title\": \""+title+"\",\"body\": \""+body+"\",},\"to\": \""+to_fcm_token+"\"}";
		  
		  // Send post request
		  con.setDoOutput(true);
		  DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		  wr.writeBytes(postJsonData);
		  wr.flush();
		  wr.close();
		  
		  int responseCode = con.getResponseCode();
		  if( responseCode == 200 ){
			  // 请求返回的数据
	          InputStream in = con.getInputStream();
	          String result = null;
	          
	          byte[] data = new byte[in.available()];
	          in.read(data);
	          // 转成字符串
	          result = new String(data);
	          JSONObject resultJson = JSONObject.fromObject(result);
	          //
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
	          //
	          if( resultJson.getString("success").equals("1") ){
	        	  notification.setReceived("1");
	        	  notification.setFailedReason("");
	          }else{
	        	  String reason = resultJson.getJSONArray("results").getJSONObject(0).getString("error");
	        	  notification.setReceived("0");
	        	  notification.setFailedReason(reason);
	          }
	          notification.setCreateDate(new Date());
	          notification.setModifyDate(new Date());
	          notification.setDeleted(0);
	          
	          if( notificationService.insert(notification) == 1 ){
	        	  return Integer.valueOf(resultJson.getString("success"));
	          }
		  }else{
			  logger.info("fcm network error");
		  }
          
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		
		return 0;
	}
}
