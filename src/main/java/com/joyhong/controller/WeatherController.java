package com.joyhong.controller;

import java.util.Date;

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

import net.sf.json.JSONObject;

/**
 * 天气接口控制器
 * @url https://well.bsimb.cn/weather/{method}
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
	private int update_weather(Integer id, Date time, String country, Integer cityId, String cityName, Float lon, Float lat, String zipCode, String data){
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
		weather.setCreateDate(new Date());
		weather.setModifyDate(new Date());
		weather.setDeleted(0);
		
		Integer retval = this.weatherService.updateByPrimaryKey(weather);
		if( retval != 1 ){
			logger.info("Update weather from database failed: " + data);
		}
		return retval;
	}
	
	/**
	 * 根据city id获取天气信息
	 * @url https://well.bsimb.cn/weather/city_id?city_id={city_id}
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
					String country = jsonResult.getJSONObject("city").getString("country");
					Integer cityId = jsonResult.getJSONObject("city").getInt("id");
					String cityName = jsonResult.getJSONObject("city").getString("name");
					Float lon = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lon"));
					Float lat = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lat"));
					String zipCode = "";
					String data = result;
					
					if( weather == null ){
						this.cache_weather(time, country, cityId, cityName, lon, lat, zipCode, data);
					}else{
						this.update_weather(weather.getId(), time, country, cityId, cityName, lon, lat, zipCode, data);
					}
					
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
	 * @url https://well.bsimb.cn/weather/city_name?city_name={city_name}
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
					String country = jsonResult.getJSONObject("city").getString("country");
					Integer cityId = jsonResult.getJSONObject("city").getInt("id");
					String cityName = jsonResult.getJSONObject("city").getString("name");
					Float lon = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lon"));
					Float lat = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lat"));
					String zipCode = "";
					String data = result;
					
					if( weather == null ){
						this.cache_weather(time, country, cityId, cityName, lon, lat, zipCode, data);
					}else{
						this.update_weather(weather.getId(), time, country, cityId, cityName, lon, lat, zipCode, data);
					}
					
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
	 * @url https://well.bsimb.cn/weather/lat_lon?lat={lat}&lon={lon}
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
				Date time = new Date();
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
	 * @url https://well.bsimb.cn/weather/zip_code?zip_code={zip_code}&country={country}
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
					country = jsonResult.getJSONObject("city").getString("country");
					Integer cityId = 0;
					String cityName = jsonResult.getJSONObject("city").getString("name");
					Float lon = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lon"));
					Float lat = Float.parseFloat(jsonResult.getJSONObject("city").getJSONObject("coord").getString("lat"));
					String zipCode = zip_code;
					String data = result;
					
					if( weather == null ){
						this.cache_weather(time, country, cityId, cityName, lon, lat, zipCode, data);
					}else{
						this.update_weather(weather.getId(), time, country, cityId, cityName, lon, lat, zipCode, data);
					}
					
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
