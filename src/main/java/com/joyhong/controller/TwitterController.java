package com.joyhong.controller;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.ui.ModelMap;

import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.User;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.StatusDeletionNotice;
import twitter4j.UserList;
import twitter4j.DirectMessage;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;

@Controller
@RequestMapping("/twitter")
public class TwitterController {
	private String consumerKey = "zp88Ky9CBkfPsP3wzrSAngfHG";
	private String consumerSecret = "L5o0YYgLH4uCmRa9M1Dh9cNbAKCqnUhwfLt5yuuBeQNzivABaW";
	private String accessToken = "2881432034-7TZQHuwUSvCKwsniZCQZEmE1RFStxN0G1krG8pl";
	private String accessTokenSecret = "1FqyuKAPEZNcRU2AANVqwnGWwpIaba9Sj9lDaldD9agSG";
	
	private TwitterStream twitterStream = null;
	
	public TwitterController(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	      .setOAuthConsumerKey(this.consumerKey)
	      .setOAuthConsumerSecret(this.consumerSecret)
	      .setOAuthAccessToken(this.accessToken)
	      .setOAuthAccessTokenSecret(this.accessTokenSecret);

	    TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
	    this.twitterStream = twitterStreamFactory.getInstance();
	}
	
	@RequestMapping(value="/listener", method = RequestMethod.GET)
	public String listener(ModelMap model){
		
		this.twitterStream.addListener(userStreamListener);
        this.twitterStream.user();
        
		model.addAttribute("response", "Listener");
		return "twitter";
	}
	
	UserStreamListener userStreamListener = new UserStreamListener() {

        public void onDirectMessage(DirectMessage message) {
            // TODO Auto-generated method stub
        	JSONObject obj = new JSONObject();
            obj.put("id", message.getId());
            obj.put("sender_id", message.getSenderId());
   	        obj.put("sender_name", message.getSenderScreenName());
   	        obj.put("recipient_id", message.getRecipientId());
   	        obj.put("recipient_name", message.getRecipientScreenName());
   	        obj.put("message", message.getText());
   	        obj.put("time", message.getCreatedAt().toString());
   	       
   	       MediaEntity[] media = message.getMediaEntities(); //get the media entities from the status
   	       JSONArray img = new JSONArray();
		   for(MediaEntity m : media){ //search trough your entities
			   
			   CloseableHttpClient httpclient1 = HttpClients.createDefault();
			   HttpGet httpget1 = new HttpGet("http://47.89.32.89/twitter/?type=image&url="+m.getMediaURL());
			   try{
					CloseableHttpResponse response = httpclient1.execute(httpget1);
					if (response.getStatusLine().getStatusCode() == 200) {
						String str = "";
		                try {
		                    str = EntityUtils.toString(response.getEntity());
		                } catch (Exception e) {
		                	e.printStackTrace();
		                }
		                img.add(str);
					}
			   }catch(IOException e){
					e.printStackTrace();
			   }
			   
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
			                	e.printStackTrace();
			                }
			                obj.put("video", str);
						}
					}catch(IOException e){
						e.printStackTrace();
					}
			   }
		   }
		   obj.put("image", img);
	       
           System.out.println(obj.toString());
           
           FileWriter fw = null;
	   		try {
	   			//如果文件存在，则追加内容；如果文件不存在，则创建文件
	   			File f=new File("/usr/local/tomcat/apache-tomcat-8.5.23/webapps/files/twitter.txt");
	   			fw = new FileWriter(f, true);
	   		} catch (IOException e) {
	   			e.printStackTrace();
	   		}
	   		PrintWriter pw = new PrintWriter(fw);
	   		pw.println(obj.toString());
	   		pw.flush();
	   		try {
	   			fw.flush();
	   			pw.close();
	   			fw.close();
	   		} catch (IOException e) {
	   			e.printStackTrace();
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