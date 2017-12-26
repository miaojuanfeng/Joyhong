package com.joyhong.controller;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Device;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.WeatherService;

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

import java.io.PrintWriter;
import java.util.Date;
import java.io.FileWriter;
import java.io.File;

/**
 * Twitter消息控制器
 * @url https://well.bsimb.cn/twitter/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/twitter")
public class TwitterController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private static String consumerKey = "zp88Ky9CBkfPsP3wzrSAngfHG";
	private static String consumerSecret = "L5o0YYgLH4uCmRa9M1Dh9cNbAKCqnUhwfLt5yuuBeQNzivABaW";
	private static String accessToken = "2881432034-7TZQHuwUSvCKwsniZCQZEmE1RFStxN0G1krG8pl";
	private static String accessTokenSecret = "1FqyuKAPEZNcRU2AANVqwnGWwpIaba9Sj9lDaldD9agSG";
	
	private TwitterStream twitterStream = null;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
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
	 * @url https://well.bsimb.cn/twitter/listener
	 * @return json
	 */
	@RequestMapping(value="/listener", method = RequestMethod.GET)
	@ResponseBody
	public String listener(){
		JSONObject retval = new JSONObject();
		
		this.twitterStream.addListener(userStreamListener);
        this.twitterStream.user();
        
        retval.put("status", true);
        retval.put("msg", "Twitter listener start");
		return retval.toString();
	}
	
	UserStreamListener userStreamListener = new UserStreamListener() {

		private Twitter twitter = null;
		
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
		
		public String getUserProfile(Long userId){
			JSONObject retval = new JSONObject();
			try{
		        User user = this.twitter.showUser(userId);
		        retval.put("id", user.getId());
		        retval.put("name", user.getName());
		        retval.put("screen_name", user.getScreenName());
		        retval.put("mini_image", user.getMiniProfileImageURL());
		        retval.put("normal_image", user.getProfileImageURL());
		        retval.put("bigger_image", user.getBiggerProfileImageURL());
		        retval.put("origin_image", user.getOriginalProfileImageURL());
		        if( user.getURL() != null ){
		        	retval.put("url", user.getURL());
		        }else{
		        	retval.put("url", "");
		        }
		        if( user.getDescription() != null ){
		        	retval.put("description", user.getDescription());
		        }else{
		        	retval.put("description", "");
		        }
		        if( user.getLocation() != null ){
		        	retval.put("location", user.getLocation());
		        }else{
		        	retval.put("location", "");
		        }
		        retval.put("language", user.getLang());
		        retval.put("favourites_count", user.getFavouritesCount());
		        retval.put("followers_count", user.getFollowersCount());
		        retval.put("friends_count", user.getFriendsCount());
		        retval.put("listed_count", user.getListedCount());
		        retval.put("statuses_count", user.getStatusesCount());
		        retval.put("background_color", user.getProfileBackgroundColor());
		        retval.put("background_image", user.getProfileBackgroundImageURL());
		        
			} catch (TwitterException e) {
		        logger.info(e.getMessage());
		    }
			return retval.toString();
		}
		
		public Integer insertUserIfNotExist(DirectMessage message){
			com.joyhong.model.User user = userService.selectByUsername(String.valueOf(message.getSenderId()));
			if( user == null ){
				user = new com.joyhong.model.User();
				user.setUsername(String.valueOf(message.getSenderId()));
				user.setPassword(String.valueOf(message.getSenderId()));
				user.setNickname(message.getSenderScreenName());
				user.setProfile(this.getUserProfile(message.getSenderId()));
				user.setPlatform("twitter");
				user.setCreateDate(new Date());
				user.setModifyDate(new Date());
				user.setDeleted(0);
				userService.insert(user);
				return user.getId();
			}else{
				return user.getId();
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
		
        public void onDirectMessage(DirectMessage message) {
            // TODO Auto-generated method stub
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
        		String[] msg = message.getText().split(":", 2);
        		if( msg[0].equals("bindDevice") && msg[1] != null ){
        			String device_id = msg[1];
        			Device device = deviceService.selectByDeviceId(device_id);
        			init();
        			if( device != null ){
        				Integer user_id = this.insertUserIfNotExist(message);
        				insertUserDeviceAfterDelete(user_id, device.getId());
        				try{
        					this.twitter.sendDirectMessage(message.getSenderId(), "Success, the device id is bind to: \nname: " + message.getSenderScreenName() + "\nid: " + message.getSenderId());
        				}catch(Exception e){
        					logger.info(e.getMessage());
        				}
        			}else{
        				try{
        					this.twitter.sendDirectMessage(message.getSenderId(), "Sorry, the device id is not yet registered");
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
		    	CloseableHttpClient httpclient1 = HttpClients.createDefault();
			    HttpGet httpget1 = new HttpGet("http://47.89.32.89/twitter/?type=image&url="+m.getMediaURL());
			    try{
			    	CloseableHttpResponse response = httpclient1.execute(httpget1);
					if (response.getStatusLine().getStatusCode() == 200) {
						String str = "";
		                try {
		                    str = EntityUtils.toString(response.getEntity());
		                } catch (Exception e) {
		                	logger.info(e.getMessage());
		                }
		                img.add(str);
					}
			    }catch(IOException e){
			    	e.printStackTrace();
			    }
			    /**
		    	 *  同步视频
		    	 */
			    if( m.getType().equals("video") ){
			    	CloseableHttpClient httpclient2 = HttpClients.createDefault();
			    	HttpGet httpget2 = new HttpGet("http://47.89.32.89/twitter/?type=video&id="+message.getId());
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
			    }
		    }
		    retval.put("image", img);
           
			FileWriter fw = null;
			try {
				/**
				 * 如果文件存在，则追加内容；如果文件不存在，则创建文件
				 */
				File f=new File("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/twitter.txt");
				fw = new FileWriter(f, true);
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
			PrintWriter pw = new PrintWriter(fw);
			pw.println(retval.toString());
			pw.flush();
			try {
				fw.flush();
				pw.close();
				fw.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
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