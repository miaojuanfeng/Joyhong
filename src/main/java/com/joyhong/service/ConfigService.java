package com.joyhong.service;

import java.util.List;

import com.joyhong.model.Config;

public interface ConfigService {
	int deleteByPrimaryKey(Integer id);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Integer id);
    
    List<Config> selectAllRecord();

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKeyWithBLOBs(Config record);

    int updateByPrimaryKey(Config record);
}
