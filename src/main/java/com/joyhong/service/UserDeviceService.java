package com.joyhong.service;

import com.joyhong.model.UserDevice;

public interface UserDeviceService {
	int deleteByPrimaryKey(Integer id);
	
	int deleteByUserId(Integer userId);

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);
}
