package com.joyhong.api;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
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
import com.joyhong.service.common.FileService;
import com.joyhong.service.common.PushService;
import com.joyhong.service.common.ConstantService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.User;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.DirectMessage;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;

/**
 * Twitter消息控制器
 * @url {base_url}/twitter/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/twitter")
public class TwitterController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private static String consumerKey = "pLekKldeX32b2g9PWSo8RK0N7";
	private static String consumerSecret = "NSlNjDKudsc58MCTgVNOl3BNzVwH4Uhk7OOAVuMrOgbFJOAuCT";
	private static String accessToken = "935413608145719296-urfIsaIgDpEbJrwzz5zgIWjVvXS9uXV";
	private static String accessTokenSecret = "KYRHnzRutnJ25MobjBxsCgTPVq6GUjzk0IDf1Cro1S1C4";
	
	private static String twitterImagePath = "/home/wwwroot/default/twitter/attachments/image/";
	private static String twitterImageUrl = "http://47.75.40.129/twitter/attachments/image/";
	
	private TwitterStream twitterStream = null;
	
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
	
	public TwitterController(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	      .setOAuthConsumerKey(TwitterController.consumerKey)
	      .setOAuthConsumerSecret(TwitterController.consumerSecret)
	      .setOAuthAccessToken(TwitterController.accessToken)
	      .setOAuthAccessTokenSecret(TwitterController.accessTokenSecret);

	    TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
	    this.twitterStream = twitterStreamFactory.getInstance();
	}
	
	/**
	 * 监听twitter发来的消息
	 * @url {base_url}/twitter/listener
	 * @return json
	 */
	@RequestMapping(value="/listener", method = RequestMethod.GET)
	@ResponseBody
	public String listener(){
		JSONObject retval = new JSONObject();
		
		this.twitterStream.addListener(userStreamListener);
        this.twitterStream.user();
        
        retval.put("status", ConstantService.statusCode_200);
		return retval.toString();
	}
	
	private byte[] url(String url){
		byte[] fileByte = null;
		
		try{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			String oauth_timestamp = String.valueOf(new Date().getTime()/1000);
			String hmacSHA1Text = "GET&" + URLEncoder.encode(url, "UTF-8") + "&" + 
								  URLEncoder.encode("oauth_consumer_key="+consumerKey+"&"+
								  "oauth_nonce="+oauth_timestamp+"&"+
								  "oauth_signature_method=HMAC-SHA1&"+
								  "oauth_timestamp="+oauth_timestamp+"&"+
								  "oauth_token="+accessToken+"&"+
								  "oauth_version=1.0", "UTF-8");
	
			String hmacSHA1Key = consumerSecret + "&" + accessTokenSecret;
			
			String oauth_signature = URLEncoder.encode(new String(Base64.encodeBase64(HmacSHA1Encrypt(hmacSHA1Text, hmacSHA1Key))), "UTF-8");
			
			HttpGet httpget = new HttpGet(url);
	
			httpget.setHeader("Authorization", "OAuth oauth_consumer_key=\""+consumerKey+"\",oauth_token=\""+accessToken+"\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\""+oauth_timestamp+"\",oauth_nonce=\""+oauth_timestamp+"\",oauth_version=\"1.0\",oauth_signature=\""+oauth_signature+"\"");
			
			CloseableHttpResponse response = httpclient.execute(httpget);
			//if (response.getStatusLine().getStatusCode() == 200) {
				fileByte = EntityUtils.toByteArray(response.getEntity());
			//}
			System.out.println("asd");
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println(EntityUtils.toString(response.getEntity()));
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		
		return fileByte;
	}
	
	private String imageUrl(String url){
		byte[] fileByte = url(url);
		String fileName = url.substring(url.lastIndexOf("/"));
		
		try{
			FileOutputStream fileOut = new FileOutputStream(twitterImagePath+fileName);  
	        BufferedOutputStream bos = new BufferedOutputStream(fileOut); 
	        bos.write(fileByte, 0, fileByte.length);
	        bos.close();
	        Runtime.getRuntime().exec("chmod 644 " + twitterImagePath + fileName);
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		
		return twitterImageUrl+fileName;
	}
	
//	private String videoUrl(long id){
//		System.out.println("https://api.twitter.com/1.1/direct_messages/show.json?id="+id);
//		byte[] s = url("https://api.twitter.com/1.1/direct_messages/show.json?id="+id);
//		System.out.println(s.toString());
//		return s.toString();
//	}
	
	/**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     * @param encryptText 被签名的字符串
     * @param encryptKey 密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
	public static byte[] HmacSHA1Encrypt( String encryptText, String encryptKey ) throws Exception{
	    byte[] data = encryptKey.getBytes( "UTF-8" );
	    // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
	    SecretKey secretKey = new SecretKeySpec( data, "HmacSHA1" );
	    // 生成一个指定 Mac 算法 的 Mac 对象
	    Mac mac = Mac.getInstance( "HmacSHA1" );
	    // 用给定密钥初始化 Mac 对象
	    mac.init( secretKey );
	    byte[] text = encryptText.getBytes( "UTF-8" );
	    // 完成 Mac 操作
	    return mac.doFinal( text );
	}
	
	UserStreamListener userStreamListener = new UserStreamListener() {

		private Twitter twitter = null;
		
		/**
		 * 初始化twitter工厂
		 */
		public void init(){
			ConfigurationBuilder cb2 = new ConfigurationBuilder();
		    cb2.setDebugEnabled(true)
		       .setOAuthConsumerKey(TwitterController.consumerKey)
		       .setOAuthConsumerSecret(TwitterController.consumerSecret)
		       .setOAuthAccessToken(TwitterController.accessToken)
		       .setOAuthAccessTokenSecret(TwitterController.accessTokenSecret);
		    
		    TwitterFactory twitterFactory = new TwitterFactory(cb2.build());
		    this.twitter = twitterFactory.getInstance();
		}
		
		/**
		 * 获取用户信息并同步图片
		 * @param userId
		 * @return json
		 */
		public String getUserProfile(Long userId){
			String retval = "";
			// 用户头像文件夹
			String filePath = "/home/wwwroot/default/twitter/attachments/users/" + String.valueOf(userId) + "/";
			String fileName = "";
			// 用户头像url
			String fileUrl = "http://47.75.40.129/twitter/attachments/users/" + String.valueOf(userId) + "/";
			
			try{
		        User user = this.twitter.showUser(userId);
		        /**
		         * Cache origin image
		         */
		        fileName = user.getOriginalProfileImageURL().substring(user.getOriginalProfileImageURL().lastIndexOf("/")+1);
		        fileService.saveUrlAs(user.getOriginalProfileImageURL(), filePath, fileName);
		        retval = fileUrl + fileName;
			} catch (TwitterException e) {
		        logger.info(e.getMessage());
		    }
			return retval;
		}
		
		/**
		 * 如果用户不存在就新增twitter用户
		 * @param message
		 * @return Integer
		 */
		public Integer insertUserIfNotExist(DirectMessage message){
			com.joyhong.model.User user = userService.selectByUsername(String.valueOf(message.getSenderId()));
			if( user == null ){
				user = new com.joyhong.model.User();
				user.setUsername(String.valueOf(message.getSenderId()));
				user.setNumber(0);
				user.setNickname(message.getSenderScreenName());
				user.setProfileImage(this.getUserProfile(message.getSenderId()));
				user.setPlatform("twitter");
				user.setAccepted("1");
				userService.insert(user);
				return user.getId();
			}else{
				return user.getId();
			}
		}
		
		/**
		 * 插入用户与设备之间的关联信息
		 * @param userId
		 * @param deviceId
		 * @return Integer
		 */
		public Integer insertUserDeviceAfterDelete(Integer userId, Integer deviceId){
			userDeviceService.deleteByUserId(userId);
			
			UserDevice userDevice = new UserDevice();
			userDevice.setUserId(userId);
			userDevice.setDeviceId(deviceId);
			userDevice.setDeviceName("");
			
			return userDeviceService.insert(userDevice);
		}
		
		/**
		 * 监听到消息事件
		 */
        public void onDirectMessage(DirectMessage message) {
            // TODO Auto-generated method stub
        	if( message.getSenderId() != 935413608145719296L ){
	        	JSONObject retval = new JSONObject();
	        	retval.put("id", message.getId());
	        	retval.put("sender_id", message.getSenderId());
	        	retval.put("sender_name", message.getSenderScreenName());
	        	retval.put("recipient_id", message.getRecipientId());
	        	retval.put("recipient_name", message.getRecipientScreenName());
	        	retval.put("message", message.getText());
	        	retval.put("time", message.getCreatedAt().toString());
	   	        /**
	   	         * 绑定设备
	   	         */
	        	if( message.getText() != "" ){
	        		String msg = message.getText();
	        		if( msg.startsWith("bd") ){
	        			String device_token = msg.substring(2);
	        			Device device = deviceService.selectByDeviceToken(device_token);
	        			init();
	        			if( device != null ){
	        				Integer user_id = this.insertUserIfNotExist(message);
	        				insertUserDeviceAfterDelete(user_id, device.getId());
	        				try{
	        					this.twitter.sendDirectMessage(message.getSenderId(), "Successful Binding!");
	        				}catch(Exception e){
	        					logger.info(e.getMessage());
	        				}
	        			}else{
	        				try{
	        					this.twitter.sendDirectMessage(message.getSenderId(), "Sorry, the device token is not yet registered");
	        				}catch(Exception e){
	        					logger.info(e.getMessage());
	        				}
	        			}
	        		}
	        	}
	        	/**
	        	 * 遍历所有附件
	        	 */
	   	        MediaEntity[] media = message.getMediaEntities();
	   	        JSONArray img = new JSONArray();
			    for(MediaEntity m : media){
			    	/**
			    	 *  同步图片
			    	 */
	//		    	CloseableHttpClient httpclient1 = HttpClients.createDefault();
	//			    HttpGet httpget1 = new HttpGet("http://47.75.40.129/twitter/?type=image&url="+m.getMediaURL());
	//			    try{
	//			    	CloseableHttpResponse response = httpclient1.execute(httpget1);
	//					if (response.getStatusLine().getStatusCode() == 200) {
	//						String str = "";
	//		                try {
	//		                    str = EntityUtils.toString(response.getEntity());
	//		                } catch (Exception e) {
	//		                	logger.info(e.getMessage());
	//		                }
	//		                img.add(str);
	//					}
	//			    }catch(IOException e){
	//			    	e.printStackTrace();
	//			    }
			    	img.add(imageUrl(m.getMediaURL()));
				    /**
			    	 *  同步视频
			    	 */
				    if( m.getType().equals("video") ){
				    	CloseableHttpClient httpclient2 = HttpClients.createDefault();
				    	HttpGet httpget2 = new HttpGet("http://47.75.40.129/twitter/?type=video&id="+message.getId());
						try{
							CloseableHttpResponse response = httpclient2.execute(httpget2);
							if (response.getStatusLine().getStatusCode() == 200) {
								String str = "";
				                try {
				                	str = EntityUtils.toString(response.getEntity());
				                } catch (Exception e) {
				                	logger.info(e.getMessage());
				                }
				                retval.put("video", str);
							}
						}catch(IOException e){
							e.printStackTrace();
						}
	//			    	retval.put("video", url(m.getMediaURL()));
				    }
			    }
			    retval.put("image", img);
			    
			    if( img.size() > 0 || retval.has("video") ){
				    /*
					 * 推送在下
					 */
				    com.joyhong.model.User user = userService.selectByUsername(String.valueOf(message.getSenderId()));
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
							temp.put("profile_image", user.getProfileImage());
							temp.put("platform", user.getPlatform());
							temp.put("accepted", user.getAccepted());
							body.put("sender_user", temp);
							//
							body.put("receive_id", device.getId());
							body.put("receive_name", userDevice.getDeviceName());
							body.put("to_fcm_token", device.getDeviceFcmToken());
							body.put("text", message.getText());
							String image_url = "";
							String video_url = "";
							String type = "";
							if( retval.has("video") ){
								image_url = "";
								video_url = retval.get("video").toString();
								type = "video";
							}else if( img.size() > 0 ){
								image_url = img.toString();
								video_url = "";
								type = "image";
							}else{
								image_url = "";
								video_url = "";
								type = "text";
							}
							body.put("image_url", image_url);
							body.put("video_url", video_url);
							body.put("type", type);
							body.put("platform", "twitter");
							pushService.push(
									user.getId(),
									user.getNickname(), 
									device.getId(), 
									userDevice.getDeviceName(), 
									device.getDeviceFcmToken(), 
									message.getText(), 
									image_url, 
									video_url, 
									type, 
									"twitter", 
									"Receive a message from Twitter", 
									body.toString().replace("\"", "\\\""));
						}
					}
					/*
					 * 推送在上
					 */
			    }
	           
				fileService.savePostData("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/twitter.txt", retval.toString());
        	}
        }

        public void onDeletionNotice(long arg0, long arg1) {
            // TODO Auto-generated method stub

        }

        public void onBlock(User arg0, User arg1) {
            // TODO Auto-generated method stub

        }
        
        public void onQuotedTweet(User arg0, User arg1, Status arg2){
        	 // TODO Auto-generated method stub
        	
        }
        
        public void onFavoritedRetweet(User arg0, User arg1, Status arg2){
        	// TODO Auto-generated method stub
        	
        }
        
        public void onStallWarning(StallWarning arg0){
        	// TODO Auto-generated method stub
        	
        }
        
        public void onRetweetedRetweet(User arg0, User arg1, Status arg2){
        	// TODO Auto-generated method stub
        	
        }
        
        public void onUserDeletion(long arg0){
        	// TODO Auto-generated method stub
        	
        }
        
        public void onUnfollow(User arg0, User arg1){
        	// TODO Auto-generated method stub
        	
        }
        
        public void onUserSuspension(long arg0){
        	// TODO Auto-generated method stub
        	
        }

		public void onDeletionNotice(StatusDeletionNotice arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onStatus(Status arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onTrackLimitationNotice(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onException(Exception arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onFavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onFollow(User arg0, User arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onFriendList(long[] arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onUnblock(User arg0, User arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onUnfavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListCreation(User arg0, UserList arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListDeletion(User arg0, UserList arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onUserListUpdate(User arg0, UserList arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onUserProfileUpdate(User arg0) {
			// TODO Auto-generated method stub
			
		}
    };
}