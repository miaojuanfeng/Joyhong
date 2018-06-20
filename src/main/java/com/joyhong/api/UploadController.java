package com.joyhong.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.joyhong.model.Device;
import com.joyhong.model.Upload;
import com.joyhong.model.User;
import com.joyhong.model.UserDevice;
import com.joyhong.service.DeviceService;
import com.joyhong.service.UploadService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.common.FileService;
import com.joyhong.service.common.OssService;
import com.joyhong.service.common.PushService;
import com.qiniu.util.Auth;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 文件上传控制器
 * @author user
 */
@Controller
@RequestMapping(value="/upload", produces="application/json;charset=UTF-8")
public class UploadController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDeviceService userDeviceService;
	
	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private PushService pushService;
	
	@Autowired
	private OssService ossService;
	
	private String tempPath = "/home/wwwroot/default/upload/temp/";
	private String filePath = "/home/wwwroot/default/upload/";
//	private String tempPath = "/Users/user/Desktop/temp/";
//	private String filePath = "/Users/user/Desktop/test/";
	private String fileUrl = ConstantService.fileUrl + "/upload/";
	
	/**
	 * 上传图片
	 * @param user_id
	 * @param device_id
	 * @param file_desc
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/image", method = RequestMethod.POST)
	@ResponseBody
	public String image(
			@RequestParam("user_id") Integer user_id,
			@RequestParam("device_id") Integer[] device_id,
			@RequestParam("file_desc") String[] file_desc,
			@RequestParam("file") MultipartFile[] files
	) throws Exception {
		JSONObject retval = new JSONObject();
		JSONArray temp = new JSONArray();
		JSONArray desc_temp = new JSONArray();
		
		String fileDir = filePath + "UID_"+user_id+"/";
		fileService.makeDir(fileDir);
		Runtime.getRuntime().exec("chmod 777 " + fileDir);
		String webUrl = fileUrl + "UID_"+user_id+"/";
		
		int fileLength = files.length;
		if( file_desc.length != fileLength ){
			retval.put("status", ConstantService.statusCode_319);
			return retval.toString();
		}
		
		for(int i = 0 ; i < fileLength; i++){
			if( !files[i].isEmpty() ){
				String fileName = files[i].getOriginalFilename();
				files[i].transferTo(new File(fileDir + fileName));
				Runtime.getRuntime().exec("chmod 644 " + fileDir + fileName);
				Upload upload = new Upload();
				upload.setUserId(user_id);
				upload.setName(fileName);
				upload.setDescription(file_desc[i]);
				upload.setUrl(webUrl+fileName);
				upload.setMd5("");
				if( uploadService.insert(upload) == 1 ){
					temp.add(webUrl + fileName);
					desc_temp.add(file_desc[i]);
				}else{
					retval.put("status", ConstantService.statusCode_318);
					return retval.toString();
				}
            }
		}
		
		/*
		 * 推送在下
		 */
		User user = userService.selectByPrimaryKey(user_id);
		if( user != null ){
			for(Integer id : device_id){
				Device device = deviceService.selectByPrimaryKey(id);
				UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId());
				if( device != null && userDevice != null ){
					JSONObject body = new JSONObject();
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
					body.put("text", desc_temp.toString());
					body.put("url", temp.toString());
					body.put("type", "image");
					body.put("platform", "app");
					body.put("time", (new Date()).getTime()/1000);
					pushService.push(
							user.getId(),
							user.getNickname(), 
							device.getId(), 
							userDevice.getDeviceName(), 
							device.getDeviceFcmToken(), 
							desc_temp.toString(), 
							temp.toString(), 
							"image", 
							"app", 
							"Receive a message from App", 
							body.toString());
				}
			}
		}
		/*
		 * 推送在上
		 */
		retval.put("status", ConstantService.statusCode_200);
		retval.put("data", temp);
		
		return retval.toString();
	}
	
	/**
	 * 上传视频
	 * @param user_id
	 * @param device_id
	 * @param file_block
	 * @param total_block
	 * @param file_desc
	 * @param file_MD5
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/video", method = RequestMethod.POST)
	@ResponseBody
	public String video(
			@RequestParam("user_id") Integer user_id,
			@RequestParam("device_id") Integer[] device_id,
			@RequestParam("file_block") Integer file_block,
			@RequestParam("total_block") Integer total_block,
			@RequestParam("file_desc") String file_desc,
			@RequestParam("file_MD5") String file_MD5,
			@RequestParam("file") MultipartFile files
	) throws Exception {
		JSONObject retval = new JSONObject();
		JSONObject temp = new JSONObject();
		JSONArray desc_temp = new JSONArray();
		JSONArray url_temp = new JSONArray();
		
		if( !files.isEmpty() ){
			String fileName = files.getOriginalFilename();
			String tempDir = tempPath + fileName + ".temp/";
			String fileDir = filePath + "UID_"+user_id+"/";
			fileService.makeDir(fileDir);
			Runtime.getRuntime().exec("chmod 777 " + fileDir);
			String webUrl = fileUrl + "UID_"+user_id+"/";
			/*
	         * 文件从开头处上传时
	         */
	        if( file_block == 1 ){
	        	/*
	             * 检查文件是否存在
	             * 根据文件MD5值检查，相同MD5值则不写文件直接推送
	             */
	        	Upload existsFile = uploadService.selectByNameAndMD5(user_id, file_MD5);
		        if( existsFile != null ){
		        	existsFile.setDescription(file_desc);
		        	fileName = existsFile.getName();
		        	if( uploadService.updateByPrimaryKey(existsFile) == 1 ){
		        		/*
		        		 * 推送在下
		        		 */
		        		User user = userService.selectByPrimaryKey(user_id);
						if( user != null ){
							desc_temp.add(file_desc);
							url_temp.add(webUrl + fileName);
							
							for(Integer id : device_id){
								Device device = deviceService.selectByPrimaryKey(id);
								UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId());
								if( device != null && userDevice != null ){
									JSONObject body = new JSONObject();
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
									body.put("type", "video");
									body.put("platform", "app");
									body.put("time", (new Date()).getTime()/1000);
									pushService.push(
											user.getId(),
											user.getNickname(), 
											device.getId(), 
											userDevice.getDeviceName(), 
											device.getDeviceFcmToken(), 
											file_desc, 
											webUrl + fileName, 
											"video", 
											"app", 
											"Receive a message from App", 
											body.toString());
								}
							}
						}
		        		/*
		        		 * 推送在上
		        		 */
						fileService.deleteDir(tempDir);
						
		        		retval.put("status", ConstantService.statusCode_200);
						temp.put("complete", true);
						temp.put("file", webUrl + fileName);
						retval.put("data", temp);
						return retval.toString();
		        	}
		        }
		        /*
		         * 检查临时文件是否存在
		         * 如果start为1表示需要重写文件，将已上传的数据块删除
		         */
//	        	fileService.deleteDir(tempDir);
	        }
			
	        fileService.makeDir(tempDir);
			Runtime.getRuntime().exec("chmod 777 " + tempDir);
			
			files.transferTo(new File(tempDir + fileName + "." + file_block + ".temp"));
			// 重命名分块
			fileService.renameFile(tempDir, tempDir, fileName + "." + file_block + ".temp", fileName + "." + file_block);
			// 所有分块上传完成，合并文件
			if( fileService.getFileCount(tempDir, null, ".temp") == total_block ){
				/*
				 *  将分块文件写入到一个文件中
				 */
				RandomAccessFile mergeFile = new RandomAccessFile(tempDir + fileName + ".done", "rw");
				long fileLength = mergeFile.length();
				for(int i=1;i<=total_block;i++){
					File file = new File(tempDir + fileName + "." + i);
			        if (!file.exists()) {
			        	mergeFile.close();
			        	fileService.deleteDir(tempDir);
			        	retval.put("status", ConstantService.statusCode_326);
						return retval.toString();
			        }
			        FileInputStream fileInput = new FileInputStream(file);
			        byte[] buffer = new byte[1024];
			        int byteread = 0; 
			        while ((byteread = fileInput.read(buffer)) != -1) {
			        	mergeFile.seek(fileLength);
			        	mergeFile.write(buffer, 0, byteread);
			        	fileLength += byteread;
			        }
			        fileInput.close();
				}
				mergeFile.close();
	    		/*
	    		 * 写入完成，重命名文件
	    		 */
				int error = fileService.renameFile(tempDir, fileDir, fileName+".done", fileName);
				Runtime.getRuntime().exec("chmod 644 " + fileDir + fileName);
				/*
		         * 将已上传的临时文件夹及里面的所有文件删除
		         */
	        	fileService.deleteDir(tempDir);
	        	/*
	        	 * 文件合并重命名成功，推送
	        	 */
	        	if( error == 0 ){
					Upload upload = new Upload();
					upload.setUserId(user_id);
					upload.setName(fileName);
					upload.setDescription(file_desc);
					upload.setUrl(webUrl+fileName);
					upload.setMd5(file_MD5);
					if( uploadService.insert(upload) == 1 ){
						/*
						 * 推送在下
						 */
						User user = userService.selectByPrimaryKey(user_id);
						if( user != null ){
							desc_temp.add(file_desc);
							url_temp.add(webUrl + fileName);
							
							for(Integer id : device_id){
								Device device = deviceService.selectByPrimaryKey(id);
								UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId());
								if( device != null && userDevice != null ){
									JSONObject body = new JSONObject();
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
									body.put("type", "video");
									body.put("platform", "app");
									body.put("time", (new Date()).getTime()/1000);
									pushService.push(
											user.getId(),
											user.getNickname(), 
											device.getId(), 
											userDevice.getDeviceName(), 
											device.getDeviceFcmToken(), 
											file_desc, 
											webUrl + fileName, 
											"video", 
											"app", 
											"Receive a message from App", 
											body.toString());
								}
							}
						}
						/*
						 * 推送在上
						 */
						retval.put("status", ConstantService.statusCode_200);
						temp.put("complete", true);
						temp.put("file", webUrl + fileName);
						retval.put("data", temp);
					}else{
						retval.put("status", ConstantService.statusCode_318);
					}
				}else{
					retval.put("status", error);
				}
			}else{
				retval.put("status", ConstantService.statusCode_200);
				temp.put("complete", false);
				temp.put("file_block", file_block);
				temp.put("total_block", total_block);
				retval.put("data", temp);
			}	
        }
		
		return retval.toString();
	}
	
	/**
	 * 获取上传token
	 * @url {base_url}/upload/token
	 * @method POST
	 * @param user_id
	 * @param user_imei
	 * @return
	 */
	@RequestMapping(value="/token", method = RequestMethod.POST)
	@ResponseBody
	public String token(@RequestParam("user_id") Integer user_id, @RequestParam("user_imei") String user_imei){
		JSONObject retval = new JSONObject();
		
		User user = userService.selectByUsername(user_imei);
		if( user != null && user_id.equals(user.getId()) ){
			JSONObject temp = new JSONObject();
			temp.put("upToken", ossService.upToken());
			
			retval.put("status", ConstantService.statusCode_200);
			retval.put("data", temp);
		}else{
			retval.put("status", ConstantService.statusCode_109);
		}
		
		return retval.toString();
	}
	
	@RequestMapping(value="/callback", method = RequestMethod.POST)
	@ResponseBody
	public String callback(HttpServletRequest request){
		JSONObject retval = new JSONObject();
//		JSONArray temp = new JSONArray();
//		JSONArray desc_temp = new JSONArray();
		
		//回调地址
        String callbackUrl = ossService.callbackUrl;
        //定义回调内容的组织格式
        String callbackBodyType = ossService.callbackBodyType;
        /**
         * 这两个参数根据实际所使用的HTTP框架进行获取
         */
        //通过获取请求的HTTP头部Authorization字段获得
        String callbackAuthHeader = request.getHeader("Authorization");
        //通过读取回调POST请求体获得，不要设置为null
        byte[] callbackBody = new byte[10240];
        try {
            //这里是最重要的，接收byte[]
            request.getInputStream().read(callbackBody);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //将byte[]转化为字符串
        String callbackBodyStr = new String(callbackBody);
        Auth auth = ossService.auth();
        //检查是否为七牛合法的回调请求
        boolean validCallback = auth.isValidCallback(callbackAuthHeader, callbackUrl, callbackBody, callbackBodyType);
        if (validCallback) {
            //继续处理其他业务逻辑
        	logger.info("callback str: " + callbackBodyStr);
        	//将字符串转化为json对象
            JSONObject obj = JSONObject.fromObject(callbackBodyStr);
            String type = obj.getString("type");
            Integer userId = Integer.valueOf(obj.getString("user_id"));
            String fileName = obj.getString("name");
            String desc = obj.getString("description");
            String url = obj.getString("url");
            JSONArray deviceId = JSONArray.fromObject(obj.getString("device_id"));
            //保存到表
            Upload upload = new Upload();
			upload.setUserId(userId);
			upload.setName(fileName);
			upload.setDescription(desc);
			upload.setUrl(ConstantService.ossUrl + url);
			upload.setMd5("");
			if( uploadService.insert(upload) == 1 ){
//				temp.add(ConstantService.ossUrl + url);
//				desc_temp.add(file_desc[i]);
				/*
				 * 推送在下
				 */
				User user = userService.selectByPrimaryKey(userId);
				if( user != null ){
					for(int i = 0; i< deviceId.size(); i++){
						Integer id = deviceId.getInt(i);
						Device device = deviceService.selectByPrimaryKey(id);
						UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(userId, device.getId());
						if( device != null && userDevice != null ){
							JSONObject body = new JSONObject();
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
							body.put("text", desc);
							body.put("url", ConstantService.ossUrl + url);
							body.put("type", type);
							body.put("platform", "app");
							body.put("time", (new Date()).getTime()/1000);
							pushService.push(
									user.getId(),
									user.getNickname(), 
									device.getId(), 
									userDevice.getDeviceName(), 
									device.getDeviceFcmToken(), 
									desc, 
									ConstantService.ossUrl + url, 
									type, 
									"app", 
									"Receive a message from App", 
									body.toString());
						}
					}
				}
				/*
				 * 推送在上
				 */
				retval.put("status", ConstantService.statusCode_200);
				retval.put("data", ConstantService.ossUrl + url);
			}else{
				retval.put("status", ConstantService.statusCode_318);
			}
        } else {
            //这是哪里的请求，被劫持，篡改了吧？
        }
        
		return retval.toString();
	}
}