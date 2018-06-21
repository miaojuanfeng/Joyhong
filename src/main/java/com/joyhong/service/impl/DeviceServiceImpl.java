package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
		Date now = new Date();
		record.setLoginTime(null);
		record.setHeartbeatTime(null);
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
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
		record.setModifyDate(new Date());
		return deviceMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Device record) {
		// TODO Auto-generated method stub
		record.setModifyDate(new Date());
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
	
	public int selectCountByOrderId(Integer orderId){
		return deviceMapper.selectCountByOrderId(orderId);
	}
	
	public int selectCount() {
		// TODO Auto-generated method stub
		return deviceMapper.selectCount();
	}

	public List<Device> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return deviceMapper.selectOffsetAndLimit(offset, limit);
	}
	
	public List<Device> selectByOrderId(Integer order_id) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByOrderId(order_id);
	}

	public List<String> selectByOrderIdReturnDeviceToken(Integer order_id) {
		// TODO Auto-generated method stub
		return deviceMapper.selectByOrderIdReturnDeviceToken(order_id);
	}

	public int selectOrderCount(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String order = request.getParameter("order");
		String action = request.getParameter("action");
		if( order != null ){
			Integer orderId = Integer.valueOf(order);
			return deviceMapper.selectOrderCount(orderId);
		}else if( action != null && action.equals("search") ){
			String deviceToken = request.getParameter("device_token")!= null?"%"+request.getParameter("device_token")+"%":"%%";
			String deviceFcmToken = request.getParameter("device_fcm_token")!= null?"%"+request.getParameter("device_fcm_token")+"%":"%%";
			return this.selectSearchCount(deviceToken, deviceFcmToken);
		}else{
			return this.selectCount();
		}
	}

	public List<Device> selectOrderOffsetAndLimit(HttpServletRequest request, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		String order = request.getParameter("order");
		String action = request.getParameter("action");
		if( order != null ){
			Integer orderId = Integer.valueOf(order);
			return deviceMapper.selectOrderOffsetAndLimit(orderId, offset, limit);
		}else if( action != null && action.equals("search") ){
			String deviceToken = request.getParameter("device_token")!= null?"%"+request.getParameter("device_token")+"%":"%%";
			String deviceFcmToken = request.getParameter("device_fcm_token")!= null?"%"+request.getParameter("device_fcm_token")+"%":"%%";
			return this.selectSearchOffsetAndLimit(deviceToken, deviceFcmToken, offset, limit);
		}else{
			return this.selectOffsetAndLimit(offset, limit);
		}
	}
	
	public int selectSearchCount(String deviceToken, String deviceFcmToken) {
		// TODO Auto-generated method stub
		return deviceMapper.selectSearchCount(deviceToken, deviceFcmToken);
	}

	public List<Device> selectSearchOffsetAndLimit(String deviceToken, String deviceFcmToken, Integer offset,
			Integer limit) {
		// TODO Auto-generated method stub
		return deviceMapper.selectSearchOffsetAndLimit(deviceToken, deviceFcmToken, offset, limit);
	}

}
