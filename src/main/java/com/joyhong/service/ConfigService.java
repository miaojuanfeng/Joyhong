package com.joyhong.service;

import com.joyhong.model.Config;

public interface ConfigService {
	int deleteByPrimaryKey(Integer id);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Integer id);
    
    Config selectByTitle(String title);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKeyWithBLOBs(Config record);

    int updateByPrimaryKey(Config record);
    
    int updateByTitleWithBLOBs(Config config);
}
