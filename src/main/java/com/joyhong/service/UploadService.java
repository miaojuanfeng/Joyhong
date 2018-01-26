package com.joyhong.service;

import com.joyhong.model.Upload;

public interface UploadService {
	int deleteByPrimaryKey(Integer id);

    int insert(Upload record);

    int insertSelective(Upload record);

    Upload selectByPrimaryKey(Integer id);
    
    Upload selectByNameAndMD5(String name, String md5);

    int updateByPrimaryKeySelective(Upload record);

    int updateByPrimaryKey(Upload record);
}
