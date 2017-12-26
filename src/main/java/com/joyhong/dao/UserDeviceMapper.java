package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.UserDevice;

public interface UserDeviceMapper {
    int deleteByPrimaryKey(Integer id);
    
    int deleteByUserId(Integer userId);

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Integer id);
    
    List<UserDevice> selectByDeviceId(Integer deviceId);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);
}