package com.joyhong.api;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping(value="/upload")
public class UploadController {
	
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
	
	private String tempPath = "/home/wwwroot/default/upload/";
	private String filePath = "/home/wwwroot/default/upload/";
//	private String tempPath = "/Users/user/Desktop/temp/";
//	private String filePath = "/Users/user/Desktop/test/";
	private String fileUrl = "http://47.89.32.89/upload/";
	
	/**
	 * 上传视频
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/video", method = RequestMethod.POST)
	@ResponseBody
	public String video(HttpServletRequest request) throws Exception {
		JSONObject retval = new JSONObject();
		JSONObject temp = new JSONObject();
		
		/*
         * 获取MD5
         */
		String fileMD5 = request.getHeader("File-MD5");
		if( null != fileMD5 ){
			fileMD5 = URLDecoder.decode(fileMD5, "UTF-8");
		}else{
			retval.put("status", ConstantService.statusCode_301);
			return retval.toString();
		}
		/*
		 * 获取文件大小
		 */
		String fileSize = request.getHeader("File-Size");
		int endPoint = 0;
		if( null != fileSize ){
			fileSize = URLDecoder.decode(fileSize, "UTF-8");
			try{
				endPoint = Integer.parseInt(fileSize);
			}catch(Exception e){
				retval.put("status", ConstantService.statusCode_302);
				return retval.toString();
			}
		}else{
			retval.put("status", ConstantService.statusCode_303);
			return retval.toString();
		}
		/*
		 * 获取字节范围
		 */
		String range = request.getHeader("File-Range");
        int start = 0;
        int end = 0;
        if( null != range && range.startsWith("bytes=") ){
        	range = URLDecoder.decode(range, "UTF-8");
            String[] values = range.split("=")[1].split("-");
        	try{
        		start = Integer.parseInt(values[0]);
        	}catch(Exception e){
        		retval.put("status", ConstantService.statusCode_304);
    			return retval.toString();
        	}
        	try{
            	end = Integer.parseInt(values[1]);
        	}catch(Exception e){
            	retval.put("status", ConstantService.statusCode_305);
    			return retval.toString();
            }
        }else{
        	retval.put("status", ConstantService.statusCode_306);
			return retval.toString();
        }
        /*
         * 计算字节大小
         */
        if( start < 1 ){
        	retval.put("status", ConstantService.statusCode_307);
			return retval.toString();
        }
        if( end < start ){
        	retval.put("status", ConstantService.statusCode_308);
			return retval.toString();
        }
        if( end > endPoint ){
        	retval.put("status", ConstantService.statusCode_309);
			return retval.toString();
        }
        int requestSize = end - start + 1;
        /*
		 * 获取分块范围
		 */
		String block = request.getHeader("File-Block");
        int currentBlock = 0;
        int totalBlock = 0;
        if( null != block ){
        	range = URLDecoder.decode(range, "UTF-8");
            String[] values = block.split("/");
        	try{
        		currentBlock = Integer.parseInt(values[0]);
        	}catch(Exception e){
        		retval.put("status", ConstantService.statusCode_320);
    			return retval.toString();
        	}
        	try{
        		totalBlock = Integer.parseInt(values[1]);
        	}catch(Exception e){
            	retval.put("status", ConstantService.statusCode_321);
    			return retval.toString();
            }
        }else{
        	retval.put("status", ConstantService.statusCode_322);
			return retval.toString();
        }
        /*
         * 判断分块范围是否合法
         */
        if( currentBlock < 1 ){
        	retval.put("status", ConstantService.statusCode_323);
			return retval.toString();
        }
        if( totalBlock < 1 ){
        	retval.put("status", ConstantService.statusCode_324);
			return retval.toString();
        }
        if( currentBlock > totalBlock ){
        	retval.put("status", ConstantService.statusCode_325);
			return retval.toString();
        }
        /*
         * 获取文件名
         */
        String fileName = request.getHeader("Content-Disposition");
        if( null != fileName ){
        	fileName = URLDecoder.decode(fileName, "UTF-8");
	        fileName = new String(fileName.getBytes("ISO8859-1"), "UTF-8");
	        int index = fileName.lastIndexOf("filename=");
	        if( index > -1 ){
	        	fileName = fileName.substring(index+9);
	        }else{
	        	retval.put("status", ConstantService.statusCode_310);
				return retval.toString();
	        }
        }else{
        	retval.put("status", ConstantService.statusCode_311);
			return retval.toString();
        }
        /*
         * 检查Body二进制数据长度是否等于File-Range长度
         */
        if(request.getContentLength()!=requestSize){
        	retval.put("status", ConstantService.statusCode_312);
			return retval.toString();
        }
        /*
		 * 获取用户id
		 */
		String userId = request.getHeader("User-Id");
		int user_id = 0;
		if( null != userId ){
			userId = URLDecoder.decode(userId, "UTF-8");
			try{
				user_id = Integer.parseInt(userId);
			}catch(Exception e){
				retval.put("status", ConstantService.statusCode_313);
				return retval.toString();
			}
		}else{
			retval.put("status", ConstantService.statusCode_314);
			return retval.toString();
		}
		/*
         * 获取device id
         */
		String deviceStr = request.getHeader("Device-Id");
		Integer[] device_id = null;
		if( null != deviceStr ){
			deviceStr = URLDecoder.decode(deviceStr, "UTF-8");
			String[] deviceArr = deviceStr.split(",");
			device_id = new Integer[deviceArr.length];
			for(int i=0;i<deviceArr.length;i++){
    			try{
    				device_id[i] = Integer.valueOf(deviceArr[i]);
    			}catch(Exception e){
    				retval.put("status", ConstantService.statusCode_315);
    				return retval.toString();
    			}
			}
		}else{
			retval.put("status", ConstantService.statusCode_316);
			return retval.toString();
		}
		/*
		 * 获取文件描述
		 */
		String fileDesc = request.getHeader("File-Desc");
		String file_desc = "";
		if( null != fileDesc ){
			fileDesc = URLDecoder.decode(fileDesc, "UTF-8");
			file_desc = fileDesc;
		}else{
			retval.put("status", ConstantService.statusCode_317);
			return retval.toString();
		}
        /*
         * 文件从开头处上传时
         */
		String tempDir = tempPath + fileName + ".temp/";
        if( start == 1 ){
        	/*
             * 检查文件是否存在
             * 根据文件MD5值检查，相同MD5值则不写文件直接推送
             */
        	Upload existsFile = uploadService.selectByNameAndMD5(fileName, fileMD5);
	        if( existsFile != null ){
	        	existsFile.setDescription(file_desc);
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
								body.put("receive_id", device.getId());
								body.put("receive_name", userDevice.getDeviceName());
								body.put("to_fcm_token", device.getDeviceFcmToken());
								body.put("text", "");
								body.put("image_url", "");
								body.put("video_url", fileUrl + fileName);
								body.put("type", "video");
								body.put("platform", "app");
								pushService.push(
										user.getId(),
										user.getNickname(), 
										device.getId(), 
										userDevice.getDeviceName(), 
										device.getDeviceFcmToken(), 
										"", 
										temp.toString(), 
										"", 
										"image", 
										"app", 
										"Receive a message from App", 
										body.toString().replace("\"", "\\\""));
							}
						}
					}
	        		/*
	        		 * 推送在上
	        		 */
	        		retval.put("status", ConstantService.statusCode_200);
					temp.put("complete", true);
					temp.put("file", fileUrl + fileName);
					retval.put("data", temp);
					return retval.toString();
	        	}
	        }
	        /*
	         * 检查临时文件是否存在
	         * 如果start为1表示需要重写文件，将已上传的数据块删除
	         */
        	fileService.deleteDir(tempDir);
        }
        /*
         * 追加文件
         */
        fileService.makeDir(tempDir);
        fileService.deleteFile(tempDir + fileName + "." + currentBlock);
        fileService.deleteFile(tempDir + fileName + "." + currentBlock + ".temp");
        InputStream is = request.getInputStream();
        RandomAccessFile oSavedFile = new RandomAccessFile(tempDir + fileName + "." + currentBlock + ".temp", "rw"); 
        long nPos = start-1;
        
	    oSavedFile.seek(nPos);
	    
	    byte[] dataOrigin = new byte[requestSize];
	    DataInputStream in = new DataInputStream(is);
	    in.readFully(dataOrigin);
	    oSavedFile.write(dataOrigin, 0, dataOrigin.length);
	    
	    in.close();
		oSavedFile.close();
		
		fileService.renameFile(tempDir, tempDir, fileName + "." + currentBlock + ".temp", fileName + "." + currentBlock);
       
		if( fileService.getFileCount(tempDir, null, ".temp") == totalBlock ){
			/*
			 *  将分块文件写入到一个文件中
			 */
			RandomAccessFile mergeFile = new RandomAccessFile(tempDir + fileName + ".done", "rw");
			long fileLength = mergeFile.length();
			for(int i=1;i<=totalBlock;i++){
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
			int error = fileService.renameFile(tempDir, filePath, fileName+".done", fileName);
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
				upload.setUrl(fileUrl+fileName);
				upload.setMd5(fileMD5);
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
								body.put("receive_id", device.getId());
								body.put("receive_name", userDevice.getDeviceName());
								body.put("to_fcm_token", device.getDeviceFcmToken());
								body.put("text", "");
								body.put("image_url", "");
								body.put("video_url", fileUrl + fileName);
								body.put("type", "video");
								body.put("platform", "app");
								pushService.push(
										user.getId(),
										user.getNickname(), 
										device.getId(), 
										userDevice.getDeviceName(), 
										device.getDeviceFcmToken(), 
										"", 
										temp.toString(), 
										"", 
										"image", 
										"app", 
										"Receive a message from App", 
										body.toString().replace("\"", "\\\""));
							}
						}
					}
					/*
					 * 推送在上
					 */
					retval.put("status", ConstantService.statusCode_200);
					temp.put("complete", true);
					temp.put("file", fileUrl + fileName);
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
			temp.put("start", start);
			temp.put("end", end);
			temp.put("next", end+1);
			temp.put("current_block", currentBlock);
			temp.put("total_block", totalBlock);
			retval.put("data", temp);
		}
		
        return retval.toString();
	}
	
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
		
		int fileLength = files.length;
		if( file_desc.length != fileLength ){
			retval.put("status", ConstantService.statusCode_319);
			return retval.toString();
		}
		
		for(int i = 0 ; i < fileLength; i++){
			if( !files[i].isEmpty() ){
				String fileName = files[i].getOriginalFilename();
				files[i].transferTo(new File(filePath + fileName));
				Runtime.getRuntime().exec("chmod 644 " + filePath + fileName);
				Upload upload = new Upload();
				upload.setUserId(user_id);
				upload.setName(fileName);
				upload.setDescription(file_desc[i]);
				upload.setUrl(fileUrl+fileName);
				upload.setMd5("");
				if( uploadService.insert(upload) == 1 ){
					temp.add(fileUrl + fileName);
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
					body.put("receive_id", device.getId());
					body.put("receive_name", userDevice.getDeviceName());
					body.put("to_fcm_token", device.getDeviceFcmToken());
					body.put("text", "");
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
							"", 
							temp.toString(), 
							"", 
							"image", 
							"app", 
							"Receive a message from App", 
							body.toString().replace("\"", "\\\""));
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
}