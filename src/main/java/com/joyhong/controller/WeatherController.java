package com.joyhong.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyhong.model.Weather;
import com.joyhong.service.WeatherService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 天气接口控制器
 * @url {base_url}/weather/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping("/weather")
public class WeatherController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private String apiUrl = "http://api.openweathermap.org/data/2.5/";
	private String appId = "?appid=8682b00098a8ecd0c5c472848fdf836c&";
	
	@Autowired
	private WeatherService weatherService;
	
	/**
	 * 从mysql查询天气缓存信息
	 * @param city_id
	 * @return Weather
	 */
	private Weather fetch_weather(Integer city_id){
		return this.weatherService.selectByCityId(city_id);
	}
	
	/**
	 * 从mysql查询天气缓存信息
	 * @param city_name
	 * @return Weather
	 */
	private Weather fetch_weather(String city_name, String zip_code){
		if( city_name != null ){
			return this.weatherService.selectByCityName(city_name);
		}else if( zip_code != null ){
			return this.weatherService.selectByZipCode(zip_code);
		}
		return null;
	}
	
	/**
	 * 检查缓存的天气数据是否过期
	 * @param weather
	 * @return bool
	 */
	private boolean invalid_time(Weather weather){
		return ( new Date().getTime()/1000 - weather.getTime().getTime()/1000 ) < 3600;
	}
	
	/**
	 * 缓存天气信息到mysql
	 * @param time
	 * @param cityId
	 * @param cityName
	 * @param lon
	 * @param lat
	 * @param zipCode
	 * @param data
	 * @return bool
	 */
	private int cache_weather(Date time, String country, Integer cityId, String cityName, Float lon, Float lat, String zipCode, String data){
		Weather weather = new Weather();
		weather.setTime(time);
		weather.setCountry(country);
		weather.setCityId(cityId);
		weather.setCityName(cityName);
		weather.setLon(lon);
		weather.setLat(lat);
		weather.setZipCode(zipCode);
		weather.setData(data);
		weather.setCreateDate(new Date());
		weather.setModifyDate(new Date());
		weather.setDeleted(0);
		
		Integer retval = this.weatherService.insert(weather);
		if( retval != 1 ){
			logger.info("Save weather to database failed: " + data);
		}
		return retval;
	}
	
	/**
	 * 更新天气信息
	 * @param id
	 * @param time
	 * @param cityId
	 * @param cityName
	 * @param lon
	 * @param lat
	 * @param zipCode
	 * @param data
	 * @return
	 */
	private int update_weather(Integer id, Date time, String country, Integer cityId, String cityName, Float lon, Float lat, String zipCode, String data, Date createDate){
		Weather weather = new Weather();
		weather.setId(id);
		weather.setTime(time);
		weather.setCountry(country);
		weather.setCityId(cityId);
		weather.setCityName(cityName);
		weather.setLon(lon);
		weather.setLat(lat);
		weather.setZipCode(zipCode);
		weather.setData(data);
		weather.setCreateDate(createDate);
		weather.setModifyDate(new Date());
		weather.setDeleted(0);
		
		Integer retval = this.weatherService.updateByPrimaryKey(weather);
		if( retval != 1 ){
			logger.info("Update weather from database failed: " + data);
		}
		return retval;
	}
	
	/** 
     * 时间戳转换成日期格式字符串 
     * @param seconds 精确到秒的字符串 
     * @param formatStr 
     * @return 
     */  
    private String timeStamp2Date(String seconds, String format) {  
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
            return "";  
        }  
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }   
        SimpleDateFormat sdf = new SimpleDateFormat(format);  
        return sdf.format(new Date(Long.valueOf(seconds+"000")));  
    }
	
    /**
     * 处理天气数据
     * @param zip_code
     * @param time
     * @param weather
     * @param jsonResult
     * @param isSave
     * @return
     */
	private String weather_data(String zip_code, Date time, Weather weather, JSONObject jsonResult, boolean isSave){
		JSONObject retObj = new JSONObject();
		
		String country = jsonResult.getJSONObject("city").getString("country");
		Integer cityId = 0;
		String cityName = jsonResult.getJSONObject("city").getString("name");
		Float lon = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lon"));
		Float lat = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lat"));
		String zipCode = "";
		if( zip_code == null ){
			cityId = jsonResult.getJSONObject("city").getInt("id");
		}else{
			zipCode = zip_code;
		}
		JSONArray list = jsonResult.getJSONArray("list");
		Integer i;
		String todayDate = timeStamp2Date(String.valueOf(new Date().getTime()/1000), "yyyy-MM-dd");
		String todayDatetime = timeStamp2Date(String.valueOf(new Date().getTime()/1000), "yyyy-MM-dd HH:mm:ss");
		Float temp_min = 99999999.0F;
		Float temp_max = 0.0F;
		JSONObject day_temp = null;
		
		for(i = 0; i < list.size(); i++){
			try{
				JSONObject timeStamp = list.getJSONObject(i);
				String date = timeStamp.getString("dt_txt").substring(0, 10);
				String datetime = timeStamp.getString("dt_txt");
				/*
				 * 当前时间
				 */
				if( !retObj.has("now") && datetime.compareTo(todayDatetime) > 0 ){
					retObj.put("now", timeStamp);
				}
				/*
				 * 未来时间的最高温和最低温
				 */
				if( !date.equals(todayDate) ){
					
					if( !retObj.has(date) ){
						temp_min = 99999999.0F;
						temp_max = 0.0F;
						day_temp = new JSONObject();
					}
					if( Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min")) < temp_min ){
						temp_min = Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min"));
						day_temp.put("min", timeStamp);
					}
					if( Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min")) > temp_max ){
						temp_max = Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min"));
						day_temp.put("max", timeStamp);
					}
					retObj.put(date, day_temp);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
			}
		}
		
		Iterator iterator = retObj.keys();
		String key;
		String value;
		JSONObject retval = new JSONObject();
		JSONArray retArr = new JSONArray();
		
		while(iterator.hasNext()){
			key = (String) iterator.next();
			value = retObj.getString(key);
			if( key.equals("now") ){
				retval.put("cur_data", value);
			}else{
				retArr.add(value);
			}
		}
		retval.put("days_data", retArr);
		retval.put("city", jsonResult.getJSONObject("city"));
		
		if( isSave ){
			if( weather == null ){
				this.cache_weather(time, country, cityId, cityName, lon, lat, zipCode, retval.toString());
			}else{
				this.update_weather(weather.getId(), time, country, cityId, cityName, lon, lat, zipCode, retval.toString(), weather.getCreateDate());
			}
		}
		
		return retval.toString();
	}
	
	/**
	 * 根据city id获取天气信息
	 * @url {base_url}/weather/city_id?city_id={city_id}
	 * @param city_id
	 * @return json
	 */
	@RequestMapping(value="/city_id", method=RequestMethod.GET)
	@ResponseBody
	public String city_id(@RequestParam("city_id") Integer city_id){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(city_id);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", true);
			retval.put("time", weather.getTime().getTime()/1000);
			retval.put("data", weather.getData());
		}else{
			try{
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet(apiUrl + "forecast" + appId + "id=" + city_id);
				CloseableHttpResponse response = httpclient.execute(httpget);
				String result = EntityUtils.toString(response.getEntity());
				
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonResult = JSONObject.fromObject(result);
					
					Date time = new Date();
					result = weather_data(null, time, weather, jsonResult, true);
					
					retval.put("status", true);
					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", false);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", false);
				retval.put("msg", e.getMessage());
			}
		}
		return retval.toString();
	}
	
	/**
	 * 根据city name获取天气信息
	 * @url {base_url}/weather/city_name?city_name={city_name}
	 * @param city_name
	 * @return json
	 */
	@RequestMapping(value="/city_name", method=RequestMethod.GET)
	@ResponseBody
	public String city_name(@RequestParam("city_name") String city_name){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(city_name, null);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", true);
			retval.put("time", weather.getTime().getTime()/1000);
			retval.put("data", weather.getData());
		}else{
			try{
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet(apiUrl + "forecast" + appId + "q=" + city_name);
				CloseableHttpResponse response = httpclient.execute(httpget);
				String result = EntityUtils.toString(response.getEntity());
				
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonResult = JSONObject.fromObject(result);
					
					Date time = new Date();
					result = weather_data(null, time, weather, jsonResult, true);
					
					retval.put("status", true);
					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", false);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", false);
				retval.put("msg", e.getMessage());
			}
		}
		return retval.toString();
	}
	
	/**
	 * 根据lat lon获取天气信息
	 * @url {base_url}/weather/lat_lon?lat={lat}&lon={lon}
	 * @param lat
	 * @param lon
	 * @return json
	 */
	@RequestMapping(value="/lat_lon", method=RequestMethod.GET)
	@ResponseBody
	public String lat_lon(@RequestParam("lat") Float lat, @RequestParam("lon") Float lon){
		JSONObject retval = new JSONObject();
		
		// 根据经纬度查询的天气信息不缓存
		try{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(apiUrl + "forecast" + appId + "lat=" + lat + "&lon=" + lon);
			CloseableHttpResponse response = httpclient.execute(httpget);
			String result = EntityUtils.toString(response.getEntity());
			
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject jsonResult = JSONObject.fromObject(result);
				
				Date time = new Date();
				result = weather_data(null, time, null, jsonResult, false);
				
				retval.put("status", true);
				retval.put("time", time.getTime()/1000);
				retval.put("data", result);
			}else{
				retval.put("status", false);
				retval.put("msg", result);
			}
		}catch(Exception e){
			logger.info(e.getMessage());
			retval.put("status", false);
			retval.put("msg", e.getMessage());
		}
			
		return retval.toString();
	}
	
	/**
	 * 根据zip_code获取天气信息
	 * @url {base_url}/weather/zip_code?zip_code={zip_code}&country={country}
	 * @param zip_code
	 * @param country
	 * @return json
	 */
	@RequestMapping(value="/zip_code", method=RequestMethod.GET)
	@ResponseBody
	public String zip_code(@RequestParam("zip_code") String zip_code, @RequestParam("country") String country){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(null, zip_code);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", true);
			retval.put("time", weather.getTime().getTime()/1000);
			retval.put("data", weather.getData());
		}else{
			try{
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet(apiUrl + "forecast" + appId + "zip=" + zip_code + "," + country);
				CloseableHttpResponse response = httpclient.execute(httpget);
				String result = EntityUtils.toString(response.getEntity());
				
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonResult = JSONObject.fromObject(result);
					
					Date time = new Date();
					result = weather_data(zip_code, time, weather, jsonResult, true);
					
					retval.put("status", true);
					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", false);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", false);
				retval.put("msg", e.getMessage());
			}
		}
		return retval.toString();
	}
}
