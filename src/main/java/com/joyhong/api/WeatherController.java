package com.joyhong.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

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
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.common.FileService;
import com.joyhong.service.common.RedisService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 天气接口控制器
 * @url {base_url}/weather/{method}
 * @author Michael.Miao
 */
@Controller
@RequestMapping(value="/weather", produces="application/json;charset=UTF-8")
public class WeatherController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private String apiUrl = "http://api.openweathermap.org/data/2.5/";
	private String appId = "?appid=8682b00098a8ecd0c5c472848fdf836c&";
	
	@Autowired
	private WeatherService weatherService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private FileService fileService;
	
	private final String weatherCityId = "weather_city_id";
	private final String weatherCityName = "weather_city_name";
	private final String weatherZipCode = "weather_zip_code";
	
	/**
	 * 从mysql查询天气缓存信息
	 * @param city_id
	 * @return Weather
	 */
	private Weather fetch_weather(Integer city_id){
		String redisResult = redisService.hget(this.weatherCityId, String.valueOf(city_id));
		if( redisResult != null ){
			return (Weather)fileService.unserialize(redisResult);
		}
		return this.weatherService.selectByCityId(city_id);
	}
	
	/**
	 * 从mysql查询天气缓存信息
	 * @param city_name
	 * @return Weather
	 */
	private Weather fetch_weather(String city_name, String zip_code){
		if( city_name != null ){
			String redisResult = redisService.hget(this.weatherCityName, city_name);
			if( redisResult != null ){
				return (Weather)fileService.unserialize(redisResult);
			}
			return this.weatherService.selectByCityName(city_name);
		}else if( zip_code != null ){
			String redisResult = redisService.hget(this.weatherZipCode, zip_code);
			if( redisResult != null ){
				return (Weather)fileService.unserialize(redisResult);
			}
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
	 * @return Object
	 */
	private Weather cache_weather(Date time, String country, Integer cityId, String cityName, Float lon, Float lat, String zipCode, String data){
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
		
		if( this.weatherService.insert(weather) != 1 ){
			logger.info("Save weather to database failed: " + data);
			return null;
		}
		return weather;
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
	 * @return Object
	 */
	private Weather update_weather(Integer id, Date time, String country, Integer cityId, String cityName, Float lon, Float lat, String zipCode, String data, Date createDate){
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
		
		if( this.weatherService.updateByPrimaryKey(weather) != 1 ){
			logger.info("Update weather from database failed: " + data);
			return null;
		}
		return weather;
	}
	
	/** 
     * 时间戳转换成日期格式字符串 
     * @param seconds 精确到秒的字符串 
     * @param formatStr 
     * @param timeZone
     * @return 
     */  
    private String timeStamp2Date(String seconds, String format, String timeZone) {  
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
            return "";  
        }  
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(new Date(Long.valueOf(seconds+"000")));  
    }
    
//    /**
//     * 日期格式字符串转换成时间戳
//     * @param date
//     * @return
//     */
//    private Long date2TimeStamp(String date, String timeZone) {  
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    	sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
//		Long time = 0L;
//		try {
//			Date d;
//			d = sdf.parse(date);
//			time = d.getTime();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		return time;
//    }
	
    /**
     * 处理天气数据
     * @param zip_code
     * @param time
     * @param weather
     * @param jsonResult
     * @param isSave
     * @return
     */
	private String weather_data(String zip_code, Date time, Weather weather, JSONObject jsonResult, boolean isSave, String timeZone, String hashTableName, String hashTableKey){
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
		String todayDate = timeStamp2Date(String.valueOf(new Date().getTime()/1000), "yyyy-MM-dd", timeZone);
		String todayDatetime = timeStamp2Date(String.valueOf(new Date().getTime()/1000), "yyyy-MM-dd HH:mm:ss", timeZone);
		Float temp_min = 99999999.0F;
		Float temp_max = 0.0F;
		JSONObject day_temp = null;
		
		for(i = 0; i < list.size(); i++){
			try{
				JSONObject timeStamp = list.getJSONObject(i);
//				String date = timeStamp.getString("dt_txt").substring(0, 10);
//				String datetime = timeStamp.getString("dt_txt");
				// 纠正时区
				String date = timeStamp2Date(timeStamp.getString("dt"), "yyyy-MM-dd", timeZone);
				String datetime = timeStamp2Date(timeStamp.getString("dt"), "yyyy-MM-dd HH:mm:ss", timeZone);
				
				/*
				 * 当前时间
				 */
				if( !retObj.has("now") && datetime.compareTo(todayDatetime) > 0 ){
					// 纠正时区
					timeStamp.put("dt_txt", datetime);
					//
					JSONObject temp = new JSONObject();
					JSONObject main = new JSONObject();
					main.put("temp", timeStamp.getJSONObject("main").getDouble("temp"));
					main.put("temp_min", timeStamp.getJSONObject("main").getDouble("temp_min"));
					main.put("temp_max", timeStamp.getJSONObject("main").getDouble("temp_max"));
					main.put("humidity", timeStamp.getJSONObject("main").getDouble("humidity"));
					main.put("temp_kf", timeStamp.getJSONObject("main").getDouble("temp_kf"));
					
					temp.put("dt", timeStamp.getLong("dt"));
					temp.put("main", main);
					temp.put("weather", timeStamp.getJSONArray("weather"));
					temp.put("wind", timeStamp.getJSONObject("wind"));
					retObj.put("now", temp);
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
						// 纠正时区
//						timeStamp.put("dt_txt", datetime);
						day_temp.put("min", timeStamp);
					}
					if( Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min")) > temp_max ){
						temp_max = Float.valueOf(timeStamp.getJSONObject("main").getString("temp_min"));
						// 纠正时区
//						timeStamp.put("dt_txt", datetime);
						day_temp.put("max", timeStamp);
					}
					
					JSONObject min_timeStamp = day_temp.getJSONObject("min");
					JSONObject max_timeStamp = day_temp.getJSONObject("max");
					
					JSONObject temp = null;
					JSONObject main = null;
					
					temp = new JSONObject();
					main = new JSONObject();
					
					main.put("temp", min_timeStamp.getJSONObject("main").getDouble("temp"));
					main.put("temp_min", min_timeStamp.getJSONObject("main").getDouble("temp_min"));
					main.put("temp_max", min_timeStamp.getJSONObject("main").getDouble("temp_max"));
					main.put("humidity", min_timeStamp.getJSONObject("main").getDouble("humidity"));
					main.put("temp_kf", min_timeStamp.getJSONObject("main").getDouble("temp_kf"));
					
					temp.put("dt", min_timeStamp.getLong("dt"));
					temp.put("main", main);
					temp.put("weather", min_timeStamp.getJSONArray("weather"));
					temp.put("wind", min_timeStamp.getJSONObject("wind"));
					
					day_temp.put("min", temp);
					
					//
					
					temp = new JSONObject();
					main = new JSONObject();
					
					main.put("temp", max_timeStamp.getJSONObject("main").getDouble("temp"));
					main.put("temp_min", max_timeStamp.getJSONObject("main").getDouble("temp_min"));
					main.put("temp_max", max_timeStamp.getJSONObject("main").getDouble("temp_max"));
					main.put("humidity", max_timeStamp.getJSONObject("main").getDouble("humidity"));
					main.put("temp_kf", max_timeStamp.getJSONObject("main").getDouble("temp_kf"));
					
					temp.put("dt", max_timeStamp.getLong("dt"));
					temp.put("main", main);
					temp.put("weather", max_timeStamp.getJSONArray("weather"));
					temp.put("wind", max_timeStamp.getJSONObject("wind"));
					
					day_temp.put("max", temp);
					
					retObj.put(date, day_temp);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
			}
		}
		
		Iterator<?> iterator = retObj.keys();
		String key;
		JSONObject value;
		JSONObject retval = new JSONObject();
		JSONArray retArr = new JSONArray();
		
		retval.put("id", jsonResult.getJSONObject("city").getString("id"));
		retval.put("name", jsonResult.getJSONObject("city").getString("name"));
		retval.put("coord", jsonResult.getJSONObject("city").getJSONObject("coord"));
		retval.put("country", jsonResult.getJSONObject("city").getString("country"));
		
		while(iterator.hasNext()){
			key = (String) iterator.next();
			value = retObj.getJSONObject(key);
			if( key.equals("now") ){
				retval.put("cur_data", value);
			}else{
				retArr.add(value);
			}
		}
		
		retval.put("days_data", retArr);
		
		if( isSave ){
			if( weather == null ){
				weather = this.cache_weather(time, country, cityId, cityName, lon, lat, zipCode, retval.toString());
			}else{
				weather = this.update_weather(weather.getId(), time, country, cityId, cityName, lon, lat, zipCode, retval.toString(), weather.getCreateDate());
			}
			// 同步redis
			if( weather != null && hashTableName != null && hashTableKey != null ){
				redisService.hset(hashTableName, hashTableKey, fileService.serialize(weather));
			}
		}
		
		return retval.toString();
	}
	
	/**
	 * 根据city id获取天气信息
	 * @url {base_url}/weather/city_id?city_id={city_id}
	 * @param city_id
	 * @param time_zone
	 * @return json
	 */
	@RequestMapping(value="/city_id", method=RequestMethod.GET)
	@ResponseBody
	public String city_id(@RequestParam("city_id") Integer city_id, @RequestParam("time_zone") String time_zone){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(city_id);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", ConstantService.statusCode_200);
//			retval.put("time", weather.getTime().getTime()/1000);
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
					result = weather_data(null, time, weather, jsonResult, true, time_zone, this.weatherCityId, String.valueOf(city_id));
					
					retval.put("status", ConstantService.statusCode_200);
//					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", ConstantService.statusCode_500);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", ConstantService.statusCode_500);
				retval.put("msg", e.getMessage());
			}
		}
		return retval.toString();
	}
	
	/**
	 * 根据city name获取天气信息
	 * @url {base_url}/weather/city_name?city_name={city_name}
	 * @param city_name
	 * @param time_zone
	 * @return json
	 */
	@RequestMapping(value="/city_name", method=RequestMethod.GET)
	@ResponseBody
	public String city_name(@RequestParam("city_name") String city_name, @RequestParam("time_zone") String time_zone){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(city_name, null);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", ConstantService.statusCode_200);
//			retval.put("time", weather.getTime().getTime()/1000);
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
					result = weather_data(null, time, weather, jsonResult, true, time_zone, this.weatherCityName, city_name);
					
					retval.put("status", ConstantService.statusCode_200);
//					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", ConstantService.statusCode_500);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", ConstantService.statusCode_500);
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
	 * @param time_zone
	 * @return json
	 */
	@RequestMapping(value="/lat_lon", method=RequestMethod.GET)
	@ResponseBody
	public String lat_lon(@RequestParam("lat") Float lat, @RequestParam("lon") Float lon, @RequestParam("time_zone") String time_zone){
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
				result = weather_data(null, time, null, jsonResult, false, time_zone, null, null);
				
				retval.put("status", ConstantService.statusCode_200);
//				retval.put("time", time.getTime()/1000);
				retval.put("data", result);
			}else{
				retval.put("status", ConstantService.statusCode_500);
				retval.put("msg", result);
			}
		}catch(Exception e){
			logger.info(e.getMessage());
			retval.put("status", ConstantService.statusCode_500);
			retval.put("msg", e.getMessage());
		}
			
		return retval.toString();
	}
	
	/**
	 * 根据zip_code获取天气信息
	 * @url {base_url}/weather/zip_code?zip_code={zip_code}&country={country}
	 * @param zip_code
	 * @param country
	 * @param time_zone
	 * @return json
	 */
	@RequestMapping(value="/zip_code", method=RequestMethod.GET)
	@ResponseBody
	public String zip_code(@RequestParam("zip_code") String zip_code, @RequestParam("country") String country, @RequestParam("time_zone") String time_zone){
		JSONObject retval = new JSONObject();
		
		Weather weather = this.fetch_weather(null, zip_code);
		if( weather != null && this.invalid_time(weather) ){
			retval.put("status", ConstantService.statusCode_200);
//			retval.put("time", weather.getTime().getTime()/1000);
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
					result = weather_data(zip_code, time, weather, jsonResult, true, time_zone, this.weatherZipCode, zip_code);
					
					retval.put("status", ConstantService.statusCode_200);
//					retval.put("time", time.getTime()/1000);
					retval.put("data", result);
				}else{
					retval.put("status", ConstantService.statusCode_500);
					retval.put("msg", result);
				}
			}catch(Exception e){
				logger.info(e.getMessage());
				retval.put("status", ConstantService.statusCode_500);
				retval.put("msg", e.getMessage());
			}
		}
		return retval.toString();
	}
}
