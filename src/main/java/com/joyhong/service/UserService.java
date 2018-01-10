package com.joyhong.service;

import java.util.List;

import com.joyhong.model.User;

public interface UserService {

	int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);
    
    User selectByNumber(Integer number);
    
    User selectByUsername(String username);
    
    int selectCount();
    
    int selectPlatformCount(String platform);
    
    List<User> selectOffsetAndLimit(Integer offset, Integer limit);
    
    List<User> selectPlatformOffsetAndLimit(String platform, Integer offset, Integer limit);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}