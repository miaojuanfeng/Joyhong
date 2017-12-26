package com.joyhong.dao;

import com.joyhong.model.UserDevice;

public interface UserDeviceMapper {
    int deleteByPrimaryKey(Integer id);
    
    int deleteByUserId(Integer userId);

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);
}