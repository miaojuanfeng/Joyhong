package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.OrderMapper;
import com.joyhong.model.Order;
import com.joyhong.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderMapper orderMapper;
	
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return orderMapper.deleteByPrimaryKey(id);
	}

	public int insert(Order record) {
		// TODO Auto-generated method stub
		record.setCreateDate(new Date());
		record.setModifyDate(new Date());
		record.setDeleted(0);
		return orderMapper.insert(record);
	}

	public int insertSelective(Order record) {
		// TODO Auto-generated method stub
		return orderMapper.insertSelective(record);
	}

	public Order selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return orderMapper.selectByPrimaryKey(id);
	}
	
	public int selectCount(){
		// TODO Auto-generated method stub
		return orderMapper.selectCount();
	}
	
	public List<Order> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return orderMapper.selectOffsetAndLimit(offset, limit);
	}

	public int updateByPrimaryKeySelective(Order record) {
		// TODO Auto-generated method stub
		return orderMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Order record) {
		// TODO Auto-generated method stub
		return orderMapper.updateByPrimaryKey(record);
	}

	public int updateByPrimaryKeyWithBLOBs(Order record) {
		// TODO Auto-generated method stub
		return orderMapper.updateByPrimaryKeyWithBLOBs(record);
	}

}
