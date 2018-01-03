package com.joyhong.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.DeviceMapper;
import com.joyhong.model.Device;
import com.joyhong.service.DeviceService;

@Service
public class DeviceServiceImpl implements DeviceService {
	
	@Autowired
	private DeviceMapper deviceMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return deviceMapper.deleteByPrimaryKey(id);
	}

	public int insert(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.insert(record);
	}

	public int insertSelective(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.insertSelective(record);
	}

	public Device selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.updateByPrimaryKey(record);
	}

	public Device selectByDeviceToken(String device_token) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByDeviceToken(device_token);
	}
	
	public List<Device> selectLikeDeviceToken(String device_token) {
		// TODO Auto-generated method stub
		return deviceMapper.selectLikeDeviceToken("%"+device_token+"%"); 
	}

}
