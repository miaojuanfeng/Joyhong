package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.OtaMapper;
import com.joyhong.model.Ota;
import com.joyhong.service.OtaService;

@Service
public class OtaServiceImpl implements OtaService {
	
	@Autowired
	private OtaMapper otaMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return otaMapper.deleteByPrimaryKey(id);
	}

	public int insert(Ota record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
		return otaMapper.insert(record);
	}

	public int insertSelective(Ota record) {
		// TODO Auto-generated method stub
		return otaMapper.insertSelective(record);
	}

	public Ota selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return otaMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Ota record) {
		// TODO Auto-generated method stub
		record.setModifyDate(new Date());
		return otaMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Ota record) {
		// TODO Auto-generated method stub
		record.setModifyDate(new Date());
		return otaMapper.updateByPrimaryKey(record);
	}
	
	public int selectCount() {
		// TODO Auto-generated method stub
		return otaMapper.selectCount();
	}

	public List<Ota> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return otaMapper.selectOffsetAndLimit(offset, limit);
	}
	
	public Ota selectByOrderIdAndVersion(Integer order_id, Integer version) {
		// TODO Auto-generated method stub
		return null;
	}

	public int selectOrderCount(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String order = request.getParameter("order");
//		String action = request.getParameter("action");
		if( order != null ){
			Integer orderId = Integer.valueOf(order);
			return otaMapper.selectOrderCount(orderId);
//		}else if( action != null && action.equals("search") ){
//			String otaToken = request.getParameter("ota_token")!= null?"%"+request.getParameter("ota_token")+"%":"%%";
//			String otaFcmToken = request.getParameter("ota_fcm_token")!= null?"%"+request.getParameter("ota_fcm_token")+"%":"%%";
//			return this.selectSearchCount(otaToken, otaFcmToken);
		}else{
			return this.selectCount();
		}
	}

	public List<Ota> selectOrderOffsetAndLimit(HttpServletRequest request, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		String order = request.getParameter("order");
//		String action = request.getParameter("action");
		if( order != null ){
			Integer orderId = Integer.valueOf(order);
			return otaMapper.selectOrderOffsetAndLimit(orderId, offset, limit);
//		}else if( action != null && action.equals("search") ){
//			String otaToken = request.getParameter("ota_token")!= null?"%"+request.getParameter("ota_token")+"%":"%%";
//			String otaFcmToken = request.getParameter("ota_fcm_token")!= null?"%"+request.getParameter("ota_fcm_token")+"%":"%%";
//			return this.selectSearchOffsetAndLimit(otaToken, otaFcmToken, offset, limit);
		}else{
			return this.selectOffsetAndLimit(offset, limit);
		}
	}

	public int updateByPrimaryKeyWithBLOBs(Ota record) {
		// TODO Auto-generated method stub
		return otaMapper.updateByPrimaryKeyWithBLOBs(record);
	}

}
