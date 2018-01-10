package com.joyhong.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.UserMapper;
import com.joyhong.model.User;
import com.joyhong.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return userMapper.deleteByPrimaryKey(id);
	}

	public int insert(User record) {
		// TODO Auto-generated method stub
		return userMapper.insert(record);
	}

	public int insertSelective(User record) {
		// TODO Auto-generated method stub
		return userMapper.insertSelective(record);
	}

	public User selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return userMapper.selectByPrimaryKey(id);
	}
	
	public User selectByNumber(Integer number) {
		// TODO Auto-generated method stub
		return userMapper.selectByNumber(number);
	}

	public User selectByUsername(String username) {
		// TODO Auto-generated method stub
		return userMapper.selectByUsername(username);
	}

	public int updateByPrimaryKeySelective(User record) {
		// TODO Auto-generated method stub
		return userMapper.updateByPrimaryKeySelective(record);
	}
	
	public int selectCount() {
		// TODO Auto-generated method stub
		return userMapper.selectCount();
	}
	
	public int selectPlatformCount(String platform) {
		// TODO Auto-generated method stub
		return userMapper.selectPlatformCount(platform);
	}
	
	public List<User> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return userMapper.selectOffsetAndLimit(offset, limit);
	}
	
	public List<User> selectPlatformOffsetAndLimit(String platform, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return userMapper.selectPlatformOffsetAndLimit(platform, offset, limit);
	}

	public int updateByPrimaryKey(User record) {
		// TODO Auto-generated method stub
		return userMapper.updateByPrimaryKey(record);
	}

}
