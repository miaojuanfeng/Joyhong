package com.joyhong.service.common;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class FuncService {
	
	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();
	}
}
