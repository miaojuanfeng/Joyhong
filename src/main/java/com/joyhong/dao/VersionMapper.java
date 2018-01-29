package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Version;

public interface VersionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Version record);

    int insertSelective(Version record);

    Version selectByPrimaryKey(Integer id);
    
    int selectCount();
    
    List<Version> selectOffsetAndLimit(Integer offset, Integer limit);
    
    Version selectByName(String name);

    int updateByPrimaryKeySelective(Version record);

    int updateByPrimaryKey(Version record);
}