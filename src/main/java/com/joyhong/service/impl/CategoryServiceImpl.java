package com.joyhong.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.CategoryMapper;
import com.joyhong.model.Category;
import com.joyhong.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;

	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return categoryMapper.deleteByPrimaryKey(id);
	}

	public int insert(Category record) {
		// TODO Auto-generated method stub
		Date now = new Date();
		record.setCreateDate(now);
		record.setModifyDate(now);
		record.setDeleted(0);
		return categoryMapper.insert(record);
	}

	public int insertSelective(Category record) {
		// TODO Auto-generated method stub
		return categoryMapper.insertSelective(record);
	}

	public Category selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return categoryMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(Category record) {
		// TODO Auto-generated method stub
		return categoryMapper.updateByPrimaryKeySelective(record);
	}
	
	public int selectCount(String type) {
		// TODO Auto-generated method stub
		return categoryMapper.selectCount(type);
	}
	
	public List<Category> selectOffsetAndLimit(String type, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return categoryMapper.selectOffsetAndLimit(type, offset, limit);
	}

	public int updateByPrimaryKey(Category record) {
		// TODO Auto-generated method stub
		return categoryMapper.updateByPrimaryKey(record);
	}

	public List<Category> selectAll() {
		// TODO Auto-generated method stub
		return categoryMapper.selectAll();
	}

}
