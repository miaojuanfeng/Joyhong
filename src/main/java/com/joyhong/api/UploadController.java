package com.joyhong.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
import com.joyhong.service.common.PushService;
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.UploadService;
import com.joyhong.service.UserDeviceService;
import com.joyhong.service.UserService;
import com.joyhong.service.common.FileService;

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
	
	private String tempPath = "/home/wwwroot/default/upload/temp/";
	private String filePath = "/home/wwwroot/default/upload/";
//	private String tempPath = "/Users/user/Desktop/temp/";
//	private String filePath = "/Users/user/Desktop/test/";
	private String fileUrl = ConstantService.baseUrl + "/upload/";
	
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
					body.put("sender_account", user.getNumber());
					body.put("receive_id", device.getId());
					body.put("receive_name", userDevice.getDeviceName());
					body.put("to_fcm_token", device.getDeviceFcmToken());
					body.put("text", desc_temp.toString());
					body.put("image_url", temp.toString());
					body.put("video_url", "");
					body.put("type", "image");
					body.put("platform", "app");
					pushService.push(
							user.getId(),
							user.getNickname(), 
							device.getId(), 
							userDevice.getDeviceName(), 
							device.getDeviceFcmToken(), 
							desc_temp.toString(), 
							temp.toString(), 
							"", 
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
							for(Integer id : device_id){
								Device device = deviceService.selectByPrimaryKey(id);
								UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId());
								if( device != null && userDevice != null ){
									JSONObject body = new JSONObject();
									body.put("sender_id", user.getId());
									body.put("sender_name", user.getNickname());
									body.put("sender_account", user.getNumber());
									body.put("receive_id", device.getId());
									body.put("receive_name", userDevice.getDeviceName());
									body.put("to_fcm_token", device.getDeviceFcmToken());
									body.put("text", file_desc);
									body.put("image_url", "");
									body.put("video_url", webUrl + fileName);
									body.put("type", "video");
									body.put("platform", "app");
									pushService.push(
											user.getId(),
											user.getNickname(), 
											device.getId(), 
											userDevice.getDeviceName(), 
											device.getDeviceFcmToken(), 
											file_desc, 
											"", 
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
							for(Integer id : device_id){
								Device device = deviceService.selectByPrimaryKey(id);
								UserDevice userDevice = userDeviceService.selectByUserIdAndDeviceId(user_id, device.getId());
								if( device != null && userDevice != null ){
									JSONObject body = new JSONObject();
									body.put("sender_id", user.getId());
									body.put("sender_name", user.getNickname());
									body.put("sender_account", user.getNumber());
									body.put("receive_id", device.getId());
									body.put("receive_name", userDevice.getDeviceName());
									body.put("to_fcm_token", device.getDeviceFcmToken());
									body.put("text", file_desc);
									body.put("image_url", "");
									body.put("video_url", webUrl + fileName);
									body.put("type", "video");
									body.put("platform", "app");
									pushService.push(
											user.getId(),
											user.getNickname(), 
											device.getId(), 
											userDevice.getDeviceName(), 
											device.getDeviceFcmToken(), 
											file_desc, 
											"", 
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
	
//	@RequestMapping(value="/tencent", method = RequestMethod.POST)
//	@ResponseBody
//	public String tencent() throws Exception{
//		JSONObject retval = new JSONObject();
//		
//		JSONObject body = new JSONObject();
//		body.put("sender_id", 22);
//		body.put("sender_name", "http://file.bsimb.cn:8080/upload/UID_39/1523524437941209.jpg");
//		
//		JSONObject t = new JSONObject();
//		t.put("t", body.toString());
//		
//		System.out.println(URLDecoder.decode(URLEncoder.encode(t.toString(), "utf-8"), "utf-8"));
//		
//		
//		pushService.push(
//				2,
//				"中文名称", 
//				3, 
//				"英文名称", 
//				"ff4928702ee545430bd87ff7e09671d51c2fe85a", 
//				"文件描述", 
//				"", 
//				"webUrl + fileName", 
//				"video", 
//				"app", 
//				"测试推送消息", 
//				t.toString());
//		
//		return retval.toString();
//	}
}