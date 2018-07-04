package com.joyhong.service.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

@Service
public class OssService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private FileService fileService;
	
//	public final String filePath = "/Users/user/Desktop/file/";
	public final String filePath = "/home/wwwroot/default/oss/";
	public final String ossOtaPath = "ota/";
	public final String ossVersionPath = "version/";
	public final String ossUploadImagePath = "upload/image/";
	public final String ossUploadVideoPath = "upload/video/";

	private String accessKey = "2LlH425zih5U1CpzSE_-gl3BtvDH0nlLX8cDnQ16";
	private String secretKey = "yw56scCKFK9sL0hK6W0gItPhezGO82zkB4XhjEkn";
	private String bucket = "photopartner";
	public String callbackUrl = ConstantService.baseUrl + "upload/callback";
	private String callbackBody = "{\"type\":\"$(x:type)\",\"user_id\":\"$(x:user_id)\",\"name\":\"$(x:name)\",\"description\":\"$(x:description)\",\"url\":$(key),\"device_id\":\"$(x:device_id)\",\"md5\":\"$(x:md5)\"}";
	public String callbackBodyType = "application/json";
	private long expireSeconds = 3600;
	
	public Auth auth(){
		return Auth.create(this.accessKey, this.secretKey);
	}
	
	public String upToken(){
		Auth auth = this.auth();
		StringMap putPolicy = new StringMap();
		putPolicy.put("callbackUrl", this.callbackUrl);
		putPolicy.put("callbackBody", this.callbackBody);
		putPolicy.put("callbackBodyType", this.callbackBodyType);
		String upToken = auth.uploadToken(this.bucket, null, this.expireSeconds, putPolicy);
		return upToken;
	}
	
	public String upToken(String filename){
		Auth auth = this.auth();
		StringMap putPolicy = new StringMap();
		putPolicy.put("callbackUrl", this.callbackUrl);
		putPolicy.put("callbackBody", this.callbackBody);
		putPolicy.put("callbackBodyType", this.callbackBodyType);
		String upToken = auth.uploadToken(this.bucket, filename, this.expireSeconds, putPolicy);
		return upToken;
	}
	
	public void upload(String source, String target){
		this.doUpload(source, target, this.upToken());
	}
	
	public void upload(String source, String target, String filename){
		this.doUpload(source, target, this.upToken(filename));
	}
	
	private void doUpload(String source, String target, String upToken){
		Configuration cfg = new Configuration(Zone.zoneNa0());
		UploadManager uploadManager = new UploadManager(cfg);
		try {
			uploadManager.put(source, target, upToken);
//		    Response response = uploadManager.put(source, target, upToken);
		    //解析上传成功的结果
//		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		} catch (QiniuException ex) {
		    Response r = ex.response;
		    logger.info(r.toString());
		    try {
		    	logger.info(r.bodyString());
			} catch (QiniuException e) {
				// TODO Auto-generated catch block
				logger.info(e.getMessage());
			}
		}
	}
	
	public void delete(String filename){
		Configuration cfg = new Configuration(Zone.zoneNa0());
		Auth auth = this.auth();
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
		    bucketManager.delete(this.bucket, filename);
		} catch (QiniuException ex) {
			logger.info(ex.code());
			logger.info(ex.response.toString());
		}
	}
	
	/**
	 * 上传cms文件
	 * @param file
	 * @param filePath
	 * @param ossPath
	 * @param id
	 * @param overrideFile
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file, String filePath, String ossPath, String id, String overrideFile) throws IOException{
		String retval = overrideFile;
		
		if( !file.isEmpty() ){
			String fileName = file.getOriginalFilename();
			String fileDir = filePath + String.valueOf(new Date().getTime()/1000) + "/";
			String ossDir = ossPath + id + "/";
			/*
			 *  create folder
			 */
			fileService.makeDir(fileDir);
			Runtime.getRuntime().exec("chmod 777 " + fileDir);
			/*
			 *  move file
			 */
			file.transferTo(new File(fileDir + fileName));
			Runtime.getRuntime().exec("chmod 644 " + fileDir + fileName);
			/*	
			 * delete file
			 */
			if( overrideFile != null && !overrideFile.equals("") ){
				this.delete(overrideFile);
			}
			/*	
			 * 	upload file
			 */
			this.upload(fileDir + fileName, ossDir + fileName, ossDir + fileName);
			/*	
			 * 	delete local file
			 */
			fileService.deleteDir(fileDir);
			
			retval = ossDir + fileName;
		}
		
		return retval;
	}
	
	/**
	 * 上传twitter文件
	 * @param fileByte
	 * @param fileName
	 * @param filePath
	 * @param ossPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(byte[] fileByte, String fileName, String filePath, String ossPath, String id) throws IOException{
		String retval = "";
		
		if( fileByte.length > 0 ){
			String fileDir = filePath + String.valueOf(new Date().getTime()/1000) + "/";
			String ossDir = ossPath + id + "/";
			/*
			 *  create folder
			 */
			fileService.makeDir(fileDir);
			Runtime.getRuntime().exec("chmod 777 " + fileDir);
			/*
			 *  move file
			 */
			FileOutputStream fileOut = new FileOutputStream(fileDir + fileName);  
	        BufferedOutputStream bos = new BufferedOutputStream(fileOut); 
	        bos.write(fileByte, 0, fileByte.length);
	        bos.close();
	        Runtime.getRuntime().exec("chmod 644 " + fileDir + fileName);
			/*	
			 * 	upload file
			 */
			this.upload(fileDir + fileName, ossDir + fileName, ossDir + fileName);
			/*	
			 * 	delete local file
			 */
			fileService.deleteDir(fileDir);
			
			retval = ossDir + fileName;
		}
		
		return retval;
	}
}
