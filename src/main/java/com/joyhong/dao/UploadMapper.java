package com.joyhong.dao;

import com.joyhong.model.Upload;

public interface UploadMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Upload record);

    int insertSelective(Upload record);

    Upload selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Upload record);

    int updateByPrimaryKey(Upload record);
}