package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.UserDeviceMapper;
import com.joyhong.model.UserDevice;
import com.joyhong.service.UserDeviceService;

@Service
public class UserDeviceServiceImpl implements UserDeviceService{
	
	@Autowired
	private UserDeviceMapper userDeviceMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return userDeviceMapper.deleteByPrimaryKey(id);
	}
	
	public int deleteByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return userDeviceMapper.deleteByUserId(userId);
	}
	
	public int deleteByUserIdAndDeviceId(Integer userId, Integer deviceId){
		// TODO Auto-generated method stub
		return userDeviceMapper.deleteByUserIdAndDeviceId(userId, deviceId);
	}

	public int insert(UserDevice record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
		return userDeviceMapper.insert(record);
	}

	public int insertSelective(UserDevice record) {
		// TODO Auto-generated method stub
		return userDeviceMapper.insertSelective(record);
	}

	public UserDevice selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return userDeviceMapper.selectByPrimaryKey(id);
	}
	
	public List<UserDevice> selectByDeviceId(Integer deviceId) {
		// TODO Auto-generated method stub
		return userDeviceMapper.selectByDeviceId(deviceId);
	}
	
	public List<UserDevice> selectByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return userDeviceMapper.selectByUserId(userId);
	}
	
	public UserDevice selectByUserIdAndDeviceId(Integer userId, Integer deviceId) {
		// TODO Auto-generated method stub
		return userDeviceMapper.selectByUserIdAndDeviceId(userId, deviceId);
	}

	public int updateByPrimaryKeySelective(UserDevice record) {
		// TODO Auto-generated method stub
		return userDeviceMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(UserDevice record) {
		// TODO Auto-generated method stub
		return userDeviceMapper.updateByPrimaryKey(record);
	}

}
