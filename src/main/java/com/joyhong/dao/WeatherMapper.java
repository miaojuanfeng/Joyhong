package com.joyhong.dao;

import com.joyhong.model.Weather;

public interface WeatherMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Weather record);

    int insertSelective(Weather record);

    Weather selectByPrimaryKey(Integer id);
    
    Weather selectByCityId(Integer city_id);
    
    Weather selectByCityName(String city_name);
    
    Weather selectByZipCode(Integer zip_code);

    int updateByPrimaryKeySelective(Weather record);

    int updateByPrimaryKeyWithBLOBs(Weather record);

    int updateByPrimaryKey(Weather record);
}