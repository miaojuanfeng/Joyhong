package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Ota;

public interface OtaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Ota record);

    int insertSelective(Ota record);

    Ota selectByPrimaryKey(Integer id);
    
    Ota selectByOrderIdAndVersion(Integer order_id, Integer version);

    int updateByPrimaryKeySelective(Ota record);

    int updateByPrimaryKeyWithBLOBs(Ota record);

    int updateByPrimaryKey(Ota record);
    
    int selectCount();
    
    List<Ota> selectOffsetAndLimit(Integer offset, Integer limit);
    
    int selectOrderCount(Integer orderId);
    
    List<Ota> selectOrderOffsetAndLimit(Integer orderId, Integer offset, Integer limit);
}