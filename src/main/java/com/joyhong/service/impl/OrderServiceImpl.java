package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
		if( record.getLastVersion() == null ){
			record.setLastVersion(0);
		}
		if( record.getVersionDesc() == null ){
			record.setVersionDesc("");
		}
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
	
	public int selectCountByCategoryId(Integer categoryId){
		return orderMapper.selectCountByCategoryId(categoryId);
	}
	
	public int selectCount(){
		// TODO Auto-generated method stub
		return orderMapper.selectCount();
	}
	
	public List<Order> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return orderMapper.selectOffsetAndLimit(offset, limit);
	}
	
	public int selectCategoryCount(HttpServletRequest request){
		// TODO Auto-generated method stub
		String category = request.getParameter("category");
		String action = request.getParameter("action");
		if( category != null ){
			Integer categoryId = Integer.valueOf(category);
			return orderMapper.selectCategoryCount(categoryId);
		}else if( action != null && action.equals("search") ){
			String orderCode = request.getParameter("order_code")!= null?"%"+request.getParameter("order_code")+"%":"%%";
			String machineCode = request.getParameter("machine_code")!= null?"%"+request.getParameter("machine_code")+"%":"%%";
			String dealerCode = request.getParameter("dealer_code")!= null?"%"+request.getParameter("dealer_code")+"%":"%%";
			String keyCode = request.getParameter("key_code")!= null?"%"+request.getParameter("key_code")+"%":"%%";
			return this.selectSearchCount(orderCode, machineCode, dealerCode, keyCode);
		}else{
			return this.selectCount();
		}
	}
	
	public List<Order> selectCategoryOffsetAndLimit(HttpServletRequest request, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		String category = request.getParameter("category");
		String action = request.getParameter("action");
		if( category != null ){
			Integer categoryId = Integer.valueOf(category);
			return orderMapper.selectCategoryOffsetAndLimit(categoryId, offset, limit);
		}else if( action != null && action.equals("search") ){
			String orderCode = request.getParameter("order_code")!= null?"%"+request.getParameter("order_code")+"%":"%%";
			String machineCode = request.getParameter("machine_code")!= null?"%"+request.getParameter("machine_code")+"%":"%%";
			String dealerCode = request.getParameter("dealer_code")!= null?"%"+request.getParameter("dealer_code")+"%":"%%";
			String keyCode = request.getParameter("key_code")!= null?"%"+request.getParameter("key_code")+"%":"%%";
			return this.selectSearchOffsetAndLimit(orderCode, machineCode, dealerCode, keyCode, offset, limit);
		}else{
			return this.selectOffsetAndLimit(offset, limit);
		}
	}
	
	public int selectSearchCount(String orderCode, String machineCode, String dealerCode, String keyCode) {
		// TODO Auto-generated method stub
		return orderMapper.selectSearchCount(orderCode, machineCode, dealerCode, keyCode);
	}

	public List<Order> selectSearchOffsetAndLimit(String orderCode, String machineCode, String dealerCode,
			String keyCode, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return orderMapper.selectSearchOffsetAndLimit(orderCode, machineCode, dealerCode, keyCode, offset, limit);
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
