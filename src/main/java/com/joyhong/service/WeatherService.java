package com.joyhong.service;

import com.joyhong.model.Weather;

public interface WeatherService {
	
	int insert(Weather weather);
	
	Weather selectByPrimaryKey(Integer id);
	
	Weather selectByCityId(Integer city_id);
	
	Weather selectByCityName(String city_name);
	
	Weather selectByZipCode(String zip_code);
	
	int updateByPrimaryKey(Weather record);
}
