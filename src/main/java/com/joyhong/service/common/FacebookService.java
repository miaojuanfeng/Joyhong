package com.joyhong.service.common;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FacebookService {
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private OssService ossService;
	
	/**
	 * 上传facebook文件，针对facebook服务器回调时间超过10s就会重发请求的情况，此处采用异步方式来解决文件上传到oss时间过长导致不断回调问题
	 * @param url
	 * @param fileName
	 * @param filePath
	 * @param ossPath
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@Async
	public String uploadFile(String url, String fileName, String filePath, String ossPath, String id) throws IOException{
		
		String fileDir = filePath + String.valueOf(new Date().getTime()/1000) + "/";
		String ossDir = ossPath + id + "/";
		/*
		 *  create folder
		 */
		fileService.makeDir(fileDir);
		Runtime.getRuntime().exec("chmod 777 " + fileDir);
		/*
		 *  save file
		 */
		fileService.saveUrlAs(url, fileDir, fileName);
		/*	
		 * 	upload file
		 */
		ossService.upload(fileDir + fileName, ossDir + fileName, ossDir + fileName);
		/*	
		 * 	delete local file
		 */
		fileService.deleteDir(fileDir);
		
		return ossDir + fileName;
	}
}
