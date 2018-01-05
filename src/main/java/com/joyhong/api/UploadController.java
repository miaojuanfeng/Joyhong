package com.joyhong.api;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;

/**
 * 文件上传控制器
 * @author user
 *
 */
@Controller
public class UploadController {
	
	private String tempPath = "/Users/user/Desktop/";
	private String filePath = "/Users/user/Desktop/";
	
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
		String fileSize = request.getHeader("File-Size");
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
		String range = request.getHeader("File-Range");
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
        String fileName = request.getHeader("Content-Disposition");
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
        
        byte[] buffer = new byte[4096];
        
        int needSize = requestSize; 
        
	    oSavedFile.seek(nPos); 
	    
	    while(needSize > 0){
	    	int len = is.read(buffer);
	    	if(needSize < buffer.length){  
	    		oSavedFile.write(buffer, 0, needSize);  
            } else {  
            	oSavedFile.write(buffer, 0, len);  
                if(len < buffer.length){  
                    break;  
                }
            }
            needSize -= buffer.length;
	    }
		
		oSavedFile.close();
        
		JSONObject temp = new JSONObject();
		if( end == endPoint ){
			String error = renameFile(tempPath, fileName+".temp", fileName);
			if( error == null ){
				retval.put("status", true);
				temp.put("complete", true);
				temp.put("file", filePath+fileName);
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
		}
		retval.put("data", temp);
        
        return retval.toString();
	}
	
	/**
	 * 重命名文件
	 * @param path
	 * @param oldname
	 * @param newname
	 * @return
	 */
	private String renameFile(String path, String oldname, String newname){
		
        File oldfile = new File(path+"/"+oldname); 
        File newfile = new File(path+"/"+newname); 
        if( !oldfile.exists() ){
            return "Temp file not exists";//重命名文件不存在
        }
        if( newfile.exists() ){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
            return newname + "already exists"; 
        }else{ 
            oldfile.renameTo(newfile); 
        } 
        return null;
    }
}
