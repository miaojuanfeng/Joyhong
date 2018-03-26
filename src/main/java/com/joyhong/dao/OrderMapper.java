package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);
    
    int selectCount();
    
    List<Order> selectOffsetAndLimit(Integer offset, Integer limit);

    int updateByPrimaryKeySelective(Order record);
    
    int updateByPrimaryKeyWithBLOBs(Order record);

    int updateByPrimaryKey(Order record);
}