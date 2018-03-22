package com.joyhong.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.UploadMapper;
import com.joyhong.model.Upload;
import com.joyhong.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService {
	
	@Autowired
	private UploadMapper uploadMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return uploadMapper.deleteByPrimaryKey(id);
	}

	public int insert(Upload record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
		return uploadMapper.insert(record);
	}

	public int insertSelective(Upload record) {
		// TODO Auto-generated method stub
		return uploadMapper.insertSelective(record);
	}

	public Upload selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return uploadMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Upload record) {
		// TODO Auto-generated method stub
		record.setModifyDate(new Date());
		return uploadMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Upload record) {
		// TODO Auto-generated method stub
		return uploadMapper.updateByPrimaryKey(record);
	}

	public Upload selectByNameAndMD5(Integer user_id, String md5) {
		// TODO Auto-generated method stub
		return uploadMapper.selectByNameAndMD5(user_id, md5);
	}

}
