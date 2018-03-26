package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.VersionMapper;
import com.joyhong.model.Version;
import com.joyhong.service.VersionService;

@Service
public class VersionServiceImpl implements VersionService {

	@Autowired
	private VersionMapper versionMapper;
	
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return versionMapper.deleteByPrimaryKey(id);
	}

	public int insert(Version record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
		return versionMapper.insert(record);
	}

	public int insertSelective(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.insertSelective(record);
	}

	public Version selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return versionMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Version record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setModifyDate(now);
		return versionMapper.updateByPrimaryKey(record);
	}

	public int selectCount() {
		// TODO Auto-generated method stub
		return versionMapper.selectCount();
	}

	public List<Version> selectOffsetAndLimit(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return versionMapper.selectOffsetAndLimit(offset, limit);
	}

	public Version selectByName(String name) {
		// TODO Auto-generated method stub
		return versionMapper.selectByName(name);
	}

	public int updateByPrimaryKeyWithBLOBs(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.updateByPrimaryKeyWithBLOBs(record);
	}

}
