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

	public Weather selectByCityId(Integer city_id) {
		// TODO Auto-generated method stub
		return weatherMapper.selectByCityId(city_id);
	}

	public Weather selectByCityName(String city_name) {
		// TODO Auto-generated method stub
		return weatherMapper.selectByCityName(city_name);
	}

	public int updateByPrimaryKey(Weather record) {
		// TODO Auto-generated method stub
		return weatherMapper.updateByPrimaryKey(record);
	}

	public Weather selectByZipCode(Integer zip_code) {
		// TODO Auto-generated method stub
		return weatherMapper.selectByZipCode(zip_code);
	}

	

}
