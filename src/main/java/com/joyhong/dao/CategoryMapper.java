package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Category;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);
    
    int selectCount(String type);
    
    List<Category> selectOffsetAndLimit(String type, Integer offset, Integer limit);
    
    List<Category> selectAll();

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}