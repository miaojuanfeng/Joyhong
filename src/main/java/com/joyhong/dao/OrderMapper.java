package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);
    
    int selectCountByCategoryId(Integer categoryId);
    
    int selectCount();
    
    List<Order> selectOffsetAndLimit(Integer offset, Integer limit);
    
    int selectCategoryCount(Integer categoryId);
    
    List<Order> selectCategoryOffsetAndLimit(Integer categoryId, Integer offset, Integer limit);
    
    int selectSearchCount(String orderCode, String machineCode, String dealerCode, String keyCode);
    
    List<Order> selectSearchOffsetAndLimit(String orderCode, String machineCode, String dealerCode, String keyCode, Integer offset, Integer limit);

    int updateByPrimaryKeySelective(Order record);
    
    int updateByPrimaryKeyWithBLOBs(Order record);

    int updateByPrimaryKey(Order record);
}