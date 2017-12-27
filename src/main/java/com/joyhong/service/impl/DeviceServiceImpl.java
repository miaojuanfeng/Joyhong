package com.joyhong.service.impl;

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
		return 0;
	}

	public int insert(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.insert(record);
	}

	public int insertSelective(Device record) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Device selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Device record) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int updateByPrimaryKey(Device record) {
		// TODO Auto-generated method stub
		return deviceMapper.updateByPrimaryKey(record);
	}

	public Device selectByDeviceId(String device_id) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByDeviceId(device_id);
	}

}
