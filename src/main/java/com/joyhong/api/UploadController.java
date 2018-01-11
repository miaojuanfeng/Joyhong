package com.joyhong.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Upload;
import com.joyhong.service.UploadService;

import net.sf.json.JSONObject;

/**
 * 文件上传控制器
 * @author user
 *
 */
@Controller
public class UploadController {
	
	@Autowired
	private UploadService uploadService;
	
	private String tempPath = "/home/wwwroot/default/upload/";
	private String filePath = "/home/wwwroot/default/upload/";
//	private String tempPath = "/Users/user/Desktop/";
//	private String filePath = "/Users/user/Desktop/";
	private String fileUrl = "http://47.89.32.89/upload/";
	
	/**
	 * 上传文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	@ResponseBody
	public String upload(HttpServletRequest request) throws Exception {
		JSONObject retval = new JSONObject();
		
		/*
		 * 获取文件大小
		 */
		String fileSize = URLDecoder.decode(request.getHeader("File-Size"), "UTF-8");
		int endPoint = 0;
		if( null != fileSize ){
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
		String range = URLDecoder.decode(request.getHeader("File-Range"), "UTF-8");
        int start = 0;
        int end = 0;
        if( null != range && range.startsWith("bytes=") ){
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
        String fileName = URLDecoder.decode(request.getHeader("Content-Disposition"), "UTF-8");
        if( null != fileName ){
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
         * 检查文件是否存在
         * 如果start为1表示需要重写文件，将已上传的数据块删除
         */
        File existsFile = new File(tempPath + fileName + ".temp");
        if( start == 1 && existsFile.exists() ){
        	existsFile.delete();
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
        
		JSONObject temp = new JSONObject();
		if( end == endPoint ){
			/*
    		 * 获取用户id
    		 */
    		String userId = URLDecoder.decode(request.getHeader("User-Id"), "UTF-8");
    		int user_id = 0;
    		if( null != userId ){
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
    		 * 获取文件描述
    		 */
    		String fileDesc = URLDecoder.decode(request.getHeader("File-Desc"), "UTF-8");
    		String file_desc = "";
    		if( null != fileDesc ){
    			file_desc = fileDesc;
    		}
    		/*
    		 * 写入完成，重命名文件
    		 */
			String error = renameFile(tempPath, filePath, fileName+".temp", fileName);
			if( error == null ){
				Upload upload = new Upload();
				upload.setUserId(user_id);
				upload.setDescription(file_desc);
				upload.setUrl(fileUrl+fileName);
				upload.setCreateDate(new Date());
				upload.setModifyDate(new Date());
				upload.setDeleted(0);
				if( uploadService.insert(upload) == 1 ){
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
	 * 重命名文件
	 * @param path
	 * @param oldname
	 * @param newname
	 * @return
	 */
	private String renameFile(String oldPath, String newPath, String oldname, String newname){
		
        File oldfile = new File(oldPath + "/" + oldname); 
        File newfile = new File(newPath + "/" + newname); 
        if( !oldfile.exists() ){
            return oldname + " not exists";//重命名文件不存在
        }
        if( newfile.exists() ){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
            return newname + " already exists"; 
        }else{ 
            oldfile.renameTo(newfile); 
        } 
        return null;
    }
}
