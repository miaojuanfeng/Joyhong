package com.joyhong.service;

import java.util.List;

import com.joyhong.model.UserDevice;

public interface UserDeviceService {
	int deleteByPrimaryKey(Integer id);
	
	int deleteByUserId(Integer userId);
	
	int deleteByUserIdAndDeviceId(Integer userId, Integer deviceId);

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Integer id);
    
    List<UserDevice> selectByDeviceId(Integer deviceId);
    
    List<UserDevice> selectByUserId(Integer userId);
    
    UserDevice selectByUserIdAndDeviceId(Integer userId, Integer deviceId);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);
}
