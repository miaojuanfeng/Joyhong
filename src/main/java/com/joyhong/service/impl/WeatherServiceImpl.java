package com.joyhong.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyhong.dao.WeatherMapper;
import com.joyhong.model.Weather;
import com.joyhong.service.WeatherService;

@Service
public class WeatherServiceImpl implements WeatherService {

	@Autowired
	private WeatherMapper weatherMapper;
	
	public int insert(Weather weather) {
		return weatherMapper.insert(weather);
	}

	public Weather selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return weatherMapper.selectByPrimaryKey(id);
	}

}
