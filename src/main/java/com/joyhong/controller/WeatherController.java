package com.joyhong.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Weather;
import com.joyhong.service.WeatherService;

@Controller
public class WeatherController {
	
	@Autowired
	private WeatherService weatherService;
	
	@RequestMapping("/weather")
	@ResponseBody
	public String weather(){
		Weather weather = new Weather();
//		weather.setId(1);
		weather.setTime(new Date());
		weather.setCityId(2);
		weather.setCityName("London");
		weather.setLon(126.5F);
		weather.setLat(26.8F);
		weather.setZipCode(6);
		weather.setData("{'text': 'test'}");
		weather.setCreateDate(new Date());
		weather.setModifyDate(new Date());
		weather.setDeleted(7);
		System.out.println(weatherService.insert(weather));
		System.out.println(weatherService.selectByPrimaryKey(1));
		return "Success";
	}
}
