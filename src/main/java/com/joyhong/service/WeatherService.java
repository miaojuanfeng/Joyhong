package com.joyhong.service;

import com.joyhong.model.Weather;

public interface WeatherService {
	
	int insert(Weather weather);
	
	Weather selectByPrimaryKey(Integer id);
}
