package com.joyhong.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.joyhong.model.Ota;

public interface OtaService {

    int deleteByPrimaryKey(Integer id);

    int insert(Ota record);

    int insertSelective(Ota record);

    Ota selectByPrimaryKey(Integer id);
    
    Ota selectByOrderIdAndVersion(Integer order_id, Integer version);
    
    int selectCount();
    
    List<Ota> selectOffsetAndLimit(Integer offset, Integer limit);
    
    int selectOrderCount(HttpServletRequest request);
    
    List<Ota> selectOrderOffsetAndLimit(HttpServletRequest request, Integer offset, Integer limit);
    
    int updateByPrimaryKeySelective(Ota record);
    
    int updateByPrimaryKeyWithBLOBs(Ota record);

    int updateByPrimaryKey(Ota record);
}
