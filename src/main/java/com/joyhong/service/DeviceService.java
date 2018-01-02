package com.joyhong.service;

import java.util.List;

import com.joyhong.model.Device;

public interface DeviceService {

    int deleteByPrimaryKey(Integer id);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Integer id);
    
    Device selectByDeviceToken(String device_token);
    
    List<Device> selectLikeDeviceToken(String device_token);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}
