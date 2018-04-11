package com.joyhong.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Config;
import com.joyhong.service.ConfigService;
import com.joyhong.service.common.FuncService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("cms/config")
public class ConfigCtrl {
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FuncService funcService;
	
	@RequestMapping(value="/update", method=RequestMethod.GET)
	public String update(
			Model model
	){	
		Config administrator = configService.selectByTitle("Administrator");
		JSONObject administratorObj = JSONObject.fromObject(administrator.getValue());
		model.addAttribute("username", administratorObj.getString("username"));
		
		return "ConfigView";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(
			@RequestParam("username") String username,
			@RequestParam("password") String password
	){
		Config config = new Config();
		/*
		 * update administrator
		 */
		JSONObject administratorObj = new JSONObject();
		administratorObj.put("username", username);
		if( password == "" ){
			Config administrator = configService.selectByTitle("Administrator");
			JSONObject temp = JSONObject.fromObject(administrator.getValue());
			administratorObj.put("password", temp.getString("password"));
		}else{
			administratorObj.put("password", DigestUtils.md5Hex(password));
		}
		config.setTitle("Administrator");
		config.setValue(administratorObj.toString());
		configService.updateByTitleWithBLOBs(config);
		
		return "redirect:/cms/config/update";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		funcService.modelAttribute(model, httpSession, request);
	}
	
}
