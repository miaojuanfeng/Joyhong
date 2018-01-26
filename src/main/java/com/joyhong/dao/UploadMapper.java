package com.joyhong.dao;

import com.joyhong.model.Upload;

public interface UploadMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Upload record);

    int insertSelective(Upload record);

    Upload selectByPrimaryKey(Integer id);
    
    Upload selectByNameAndMD5(String name, String md5);

    int updateByPrimaryKeySelective(Upload record);

    int updateByPrimaryKey(Upload record);
}