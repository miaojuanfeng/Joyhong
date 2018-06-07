package com.joyhong.service;

import java.util.List;

import com.joyhong.model.Category;

public interface CategoryService {

	int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);
    
    int selectCount(String type);
    
    List<Category> selectOffsetAndLimit(String type, Integer offset, Integer limit);
    
    List<Category> selectAll();

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}