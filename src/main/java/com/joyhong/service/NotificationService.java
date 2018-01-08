package com.joyhong.service;

import com.joyhong.model.Notification;

public interface NotificationService {
	int deleteByPrimaryKey(Integer id);

    int insert(Notification record);

    int insertSelective(Notification record);

    Notification selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Notification record);

    int updateByPrimaryKeyWithBLOBs(Notification record);

    int updateByPrimaryKey(Notification record);
    
    public int push(Integer sender_id, 
			String sender_name, 
			Integer receive_id, 
			String receive_name, 
			String to_fcm_token,
			String text,
			String image_url,
			String video_url,
			String type,
			String platform,
			String title,
			String body);
}
