package com.joyhong.service.common;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

@Service
public class OssService {
	
	private Logger logger = Logger.getLogger(this.getClass());

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
		    Response response = uploadManager.put(source, target, upToken);
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
}
