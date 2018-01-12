package com.joyhong.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.ConfigMapper;
import com.joyhong.model.Config;
import com.joyhong.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {
	
	@Autowired
	private ConfigMapper configMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return configMapper.deleteByPrimaryKey(id);
	}

	public int insert(Config record) {
		// TODO Auto-generated method stub
		return configMapper.insert(record);
	}

	public int insertSelective(Config record) {
		// TODO Auto-generated method stub
		return configMapper.insertSelective(record);
	}

	public Config selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return configMapper.selectByPrimaryKey(id);
	}
	
	public Config selectByTitle(String title) {
		// TODO Auto-generated method stub
		return configMapper.selectByTitle(title);
	}

	public int updateByPrimaryKeySelective(Config record) {
		// TODO Auto-generated method stub
		return configMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKeyWithBLOBs(Config record) {
		// TODO Auto-generated method stub
		return configMapper.updateByPrimaryKeyWithBLOBs(record);
	}

	public int updateByPrimaryKey(Config record) {
		// TODO Auto-generated method stub
		return configMapper.updateByPrimaryKey(record);
	}

	public int updateByTitleWithBLOBs(Config config) {
		// TODO Auto-generated method stub
		config.setModifyDate(new Date());
		return configMapper.updateByTitleWithBLOBs(config);
	}
	
}
