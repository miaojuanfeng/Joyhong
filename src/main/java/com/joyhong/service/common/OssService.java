package com.joyhong.service.common;

import java.io.File;
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

@Service
public class OssService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private FileService fileService;
	
	public final String filePath = "/Users/user/Desktop/file/";
	public final String ossOtaPath = "ota/";
	public final String ossVersionPath = "version/";

	private String accessKey = "2LlH425zih5U1CpzSE_-gl3BtvDH0nlLX8cDnQ16";
	private String secretKey = "yw56scCKFK9sL0hK6W0gItPhezGO82zkB4XhjEkn";
	private String bucket = "photopartner";
	
	public String upToken(){
		Auth auth = Auth.create(this.accessKey, this.secretKey);
		String upToken = auth.uploadToken(this.bucket);
		return upToken;
	}
	
	public String upToken(String filename){
		Auth auth = Auth.create(this.accessKey, this.secretKey);
		String upToken = auth.uploadToken(this.bucket, filename);
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
		Auth auth = Auth.create(this.accessKey, this.secretKey);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
		    bucketManager.delete(this.bucket, filename);
		} catch (QiniuException ex) {
			logger.info(ex.code());
			logger.info(ex.response.toString());
		}
	}
	
	public String uploadFile(MultipartFile file, String filePath, String ossPath, Integer id, String overrideFile) throws IOException{
		String retval = overrideFile;
		
		if( !file.isEmpty() ){
			String fileName = file.getOriginalFilename();
			String fileDir = filePath + String.valueOf(new Date().getTime()/1000) + "/";
			String ossDir = ossPath + String.valueOf(id) + "/";
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
}
