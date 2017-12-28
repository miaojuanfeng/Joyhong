package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Device;

public interface DeviceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Integer id);
    
    Device selectByDeviceId(String device_id);
    
    List<Device> selectLikeDeviceId(String device_id);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}