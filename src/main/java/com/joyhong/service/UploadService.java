package com.joyhong.service;

import com.joyhong.model.Upload;

public interface UploadService {
	int deleteByPrimaryKey(Integer id);

    int insert(Upload record);

    int insertSelective(Upload record);

    Upload selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Upload record);

    int updateByPrimaryKey(Upload record);
}