package com.joyhong.service.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class FileService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 重命名文件
	 * @param path
	 * @param oldname
	 * @param newname
	 * @return
	 */
	public int renameFile(String oldPath, String newPath, String oldname, String newname){
		try{
	        File oldfile = new File(oldPath + "/" + oldname); 
	        File newfile = new File(newPath + "/" + newname); 
	        if( !oldfile.exists() ){
	            return ConstantService.statusCode_901;
	        }
	        if( newfile.exists() ){
	            return ConstantService.statusCode_902; 
	        }else{ 
	            oldfile.renameTo(newfile);
	            Runtime.getRuntime().exec("chmod 644 " + newPath + "/" + newname);
	        }
		}catch(Exception e){
			logger.info(e.getMessage());
		}
        return 0;
    }
	
	/**
	 * facebook与twitter同步url图片视频到本地服务器
	 * @param url
	 * @param filePath
	 * @param method
	 */
	public void saveUrlAs(String url, String filePath, String fileName){   
	     FileOutputStream fileOut = null;  
	     HttpURLConnection conn = null;  
	     InputStream inputStream = null;  
	     try {
	    	 if (!filePath.endsWith("/")) {  
	             filePath += "/";
	         }
	    	 File file = new File(filePath);
	    	 if(!file.exists()){
	    		 file .mkdir();
	    		 Runtime.getRuntime().exec("chmod 777 " + file);
	    	 }
	    	 
	         URL httpUrl=new URL(url);  
	         conn=(HttpURLConnection) httpUrl.openConnection();  
	         conn.setRequestMethod("GET");  
	         conn.setDoInput(true);    
	         conn.setDoOutput(true);   
	         conn.setUseCaches(false);  
	         conn.connect();  
	         inputStream=conn.getInputStream();  
	         BufferedInputStream bis = new BufferedInputStream(inputStream);
	         
	         fileOut = new FileOutputStream(filePath+fileName);  
	         BufferedOutputStream bos = new BufferedOutputStream(fileOut);  
	           
	         byte[] buf = new byte[4096];
	         int length = bis.read(buf);
	         while(length != -1)  
	         {  
	             bos.write(buf, 0, length);  
	             length = bis.read(buf);  
	         }
	         bos.close();
	         bis.close();
	         conn.disconnect();
	         Runtime.getRuntime().exec("chmod 644 " + filePath + fileName);
	    } catch (Exception e)  {
	    	logger.info(e.getMessage());
	    }
	 }
	
	/**
	 * 保存facebook和twitter监听到的消息到文件中
	 * @param fileName
	 * @param postdata
	 */
	public void savePostData(String fileName, String postdata){
		FileWriter fw = null;
	    try {
	    	//如果文件存在，则追加内容；如果文件不存在，则创建文件
	    	File f=new File(fileName);
	      	fw = new FileWriter(f, true);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    PrintWriter pw = new PrintWriter(fw);
	    pw.println(postdata);
	    pw.flush();
	    try {
			fw.flush();
			pw.close();
			fw.close();
	    } catch (IOException e) {
	    	logger.info(e.getMessage());
	    }
	}
	
	/**
	 * 获取文件加下文件数量
	 * @param filePath
	 * @param expectExt 期望文件后缀的文件
	 * @param exceptExt 排除文件后缀的文件
	 * @return
	 */
	public int getFileCount(String filePath, String expectExt, String exceptExt) {
		int count = 0;
        File file = new File(filePath);
        File[] listfile = file.listFiles();
        for (int i = 0; i < listfile.length; i++) {
            if( !listfile[i].isDirectory() ){
            	if( expectExt != null && !listfile[i].getName().endsWith(expectExt) ){
            		continue;
            	}
            	if( exceptExt != null && listfile[i].getName().endsWith(exceptExt) ){
            		continue;
            	}
            	if( listfile[i].getName().startsWith(".") ){
            		continue;
            	}
            	count++;
            }else{
            	getFileCount(listfile[i].toString(), expectExt, exceptExt);
            }
        }
        return count;
    }
	
	/**
	 * 创建文件
	 * @param fileName
	 * @throws Exception
	 */
	public void makeFile(String fileName) throws Exception{
		File file = new File(fileName);  
        if (!file.exists()) {  
            file.createNewFile();  
        }
	}
	
	/**
	 * 创建文件夹
	 * @param fileDir
	 */
	public void makeDir(String fileDir){
		File emptyDir = new File(fileDir);
        if( !emptyDir.exists() ){
        	emptyDir.mkdir();
        }
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 */
	public void deleteFile(String fileName){
		File existsFile = new File(fileName);
    	if( existsFile.exists() && existsFile.isFile() ){
    		existsFile.delete();
    	}
	}
	
	/**
	 * 删除目录及其下所有文件
	 * @param filePath
	 */
	public void deleteDir(String fileDir){
		File existsPath = new File(fileDir);
		File[] listfile = existsPath.listFiles();
		if( listfile != null ){
			for (int i = 0; i < listfile.length; i++) {
	            if( !listfile[i].isDirectory() ){
	            	listfile[i].delete();
	            }else{
	            	deleteDir(listfile[i].toString());
	            }
	        }
		}
		existsPath.delete();
	}
	
	/**
	 * 序列化对象
	 * @param object
	 * @return
	 */
	public String serialize(Object object){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream  oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toString("ISO-8859-1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 反序列化对象
	 * @param bytes
	 * @return
	 */
	public Object unserialize(String string){
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("ISO-8859-1"));
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
