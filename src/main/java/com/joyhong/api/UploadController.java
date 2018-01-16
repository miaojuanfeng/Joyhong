package com.joyhong.api;

import java.io.DataInputStream;
import java.io.File;
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

import com.joyhong.model.Upload;
import com.joyhong.service.UploadService;
import com.joyhong.service.common.FileService;
import com.joyhong.service.common.MD5Service;

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
	private UploadService uploadService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private MD5Service md5Service;
	
	private String tempPath = "/home/wwwroot/default/upload/";
	private String filePath = "/home/wwwroot/default/upload/";
//	private String tempPath = "/Users/user/Desktop/";
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
			retval.put("status", false);
			retval.put("msg", "File-MD5 property is not set or format error");
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
				retval.put("status", false);
				retval.put("msg", "File-Size property format error");
				return retval.toString();
			}
		}else{
			retval.put("status", false);
			retval.put("msg", "File-Size property is not set");
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
        		retval.put("status", false);
    			retval.put("msg", "Start bytes is not set or format error");
    			return retval.toString();
        	}
        	try{
            	end = Integer.parseInt(values[1]);
        	}catch(Exception e){
            	retval.put("status", false);
    			retval.put("msg", "End bytes is not set or format error");
    			return retval.toString();
            }
        }else{
        	retval.put("status", false);
			retval.put("msg", "File-Range property is not set or format error");
			return retval.toString();
        }
        /*
         * 计算字节大小
         */
        if( start < 1 ){
        	retval.put("status", false);
			retval.put("msg", "The start bytes must be more than 0");
			return retval.toString();
        }
        if( end < start ){
        	retval.put("status", false);
			retval.put("msg", "The end bytes must be more or equal than start bytes");
			return retval.toString();
        }
        if( end > endPoint ){
        	retval.put("status", false);
			retval.put("msg", "The end bytes must be less or equal than file size");
			return retval.toString();
        }
        int requestSize = end - start + 1;
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
	        	retval.put("status", false);
				retval.put("msg", "Content-Disposition property format error");
				return retval.toString();
	        }
        }else{
        	retval.put("status", false);
			retval.put("msg", "Content-Disposition property is not set");
			return retval.toString();
        }
        /*
         * 检查Body二进制数据长度是否等于File-Range长度
         */
        if(request.getContentLength()!=requestSize){
        	retval.put("status", false);
			retval.put("msg", "The size of the Body is not equal to the size of the File-Range");
			return retval.toString();
        }
        /*
         * 文件从开头处上传时
         */
        if( start == 1 ){
        	/*
             * 检查文件是否存在
             * 根据文件MD5值检查，相同MD5值则不写文件直接推送
             */
	        File existsFile = new File(filePath + fileName);
	        if( existsFile.exists() ){
	        	if( fileMD5.equals(md5Service.getMD5OfFile(filePath + fileName)) ){
	        		/*
	        		 * 推送在下
	        		 */
	        		
	        		/*
	        		 * 推送在上
	        		 */
	        		retval.put("status", true);
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
        	File existsTempFile = new File(tempPath + fileName + ".temp");
        	if( existsTempFile.exists() ){
        		existsTempFile.delete();
        	}
        }
        /*
         * 追加文件
         */
        InputStream is = request.getInputStream();
        RandomAccessFile oSavedFile = new RandomAccessFile(tempPath + fileName + ".temp", "rw"); 
        long nPos = start-1;
        
	    oSavedFile.seek(nPos);
	    
	    byte[] dataOrigin = new byte[requestSize];
	    DataInputStream in = new DataInputStream(is);
	    in.readFully(dataOrigin);
	    oSavedFile.write(dataOrigin, 0, dataOrigin.length);
	    in.close();
		
		oSavedFile.close();
       
		if( end == endPoint ){
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
    				retval.put("status", false);
    				retval.put("msg", "User-Id property format error");
    				return retval.toString();
    			}
    		}else{
    			retval.put("status", false);
    			retval.put("msg", "User-Id property is not set");
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
	    				retval.put("status", false);
	    				retval.put("msg", "Device-Id property format error");
	    				return retval.toString();
	    			}
    			}
    		}else{
    			retval.put("status", false);
    			retval.put("msg", "Device-Id property is not set");
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
    		}
    		/*
    		 * 写入完成，重命名文件
    		 */
			String error = fileService.renameFile(tempPath, filePath, fileName+".temp", fileName);
			if( error == null ){
				Upload upload = new Upload();
				upload.setUserId(user_id);
				upload.setDescription(file_desc);
				upload.setUrl(fileUrl+fileName);
				if( uploadService.insert(upload) == 1 ){
					/*
					 * 推送在下
					 */
					
					/*
					 * 推送在上
					 */
					retval.put("status", true);
					temp.put("complete", true);
					temp.put("file", fileUrl + fileName);
					retval.put("data", temp);
				}else{
					retval.put("status", false);
					retval.put("msg", "Save file url to database failed, please try again later");
				}
			}else{
				retval.put("status", false);
				retval.put("msg", error);
			}
		}else{
			retval.put("status", true);
			temp.put("complete", false);
			temp.put("start", start);
			temp.put("end", end);
			temp.put("next", end+1);
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
			retval.put("status", false);
			retval.put("msg", "The length of the file_desc is not equal to the length of the file");
			return retval.toString();
		}
		
		for(int i = 0 ; i < fileLength; i++){
			if( !files[i].isEmpty() ){
				String fileName = files[i].getOriginalFilename();
				files[i].transferTo(new File(filePath + fileName));
				Upload upload = new Upload();
				upload.setUserId(user_id);
				upload.setDescription(file_desc[i]);
				upload.setUrl(fileUrl+fileName);
				if( uploadService.insert(upload) == 1 ){
					temp.add(fileUrl + fileName);
				}else{
					retval.put("status", false);
					retval.put("msg", "Save file url to database failed, please try again later");
					return retval.toString();
				}
            }
		}
		
		/*
		 * 推送在下
		 */
		
		/*
		 * 推送在上
		 */
		retval.put("status", true);
		retval.put("data", temp);
		
		return retval.toString();
	}
}