package com.joyhong.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.NotificationMapper;
import com.joyhong.model.Notification;
import com.joyhong.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	@Autowired
	private NotificationMapper notificationMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return notificationMapper.deleteByPrimaryKey(id);
	}

	public int insert(Notification record) {
		// TODO Auto-generated method stub
		return notificationMapper.insert(record);
	}

	public int insertSelective(Notification record) {
		// TODO Auto-generated method stub
		return notificationMapper.insertSelective(record);
	}

	public Notification selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return notificationMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Notification record) {
		// TODO Auto-generated method stub
		return notificationMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKeyWithBLOBs(Notification record) {
		// TODO Auto-generated method stub
		return notificationMapper.updateByPrimaryKeyWithBLOBs(record);
	}

	public int updateByPrimaryKey(Notification record) {
		// TODO Auto-generated method stub
		return notificationMapper.updateByPrimaryKey(record);
	}
	
}
