package com.joyhong.cms;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.joyhong.model.Ota;
import com.joyhong.service.OtaService;
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.common.FuncService;
import com.joyhong.service.common.OssService;

@Controller
@RequestMapping("cms/ota")
public class OtaCtrl {
	
	@Autowired
	private OtaService otaService;
	
	@Autowired
	private FuncService funcService;
	
	@Autowired
	private OssService ossService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(HttpServletRequest request){
		return "redirect:/cms/ota/select/1"+funcService.requestParameters(request);
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(
			Model model,
			HttpServletRequest request,
			@PathVariable(value="page") Integer page
	){
		int pageSize = 20;
		int totalRecord = otaService.selectOrderCount(request);
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Ota> ota = otaService.selectOrderOffsetAndLimit(request, offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("ota", ota);
		model.addAttribute("ossUrl", ConstantService.ossUrl);
		model.addAttribute("parameters", funcService.requestParameters(request));
		
		return "OtaView";
	}
	
	@RequestMapping(value="insert", method=RequestMethod.GET)
	public String insert(
			Model model,
			HttpServletRequest request
	){
		String order = request.getParameter("order");
		Integer orderId = 0;
		if( order != null ){
			orderId = Integer.valueOf(order);
		}
		
		Ota ota = new Ota();
		ota.setOrderId(orderId);
		model.addAttribute("ota", ota);

		return "OtaView";
	}
	
	@RequestMapping(value="insert", method=RequestMethod.POST)
	public String insert(
			Model model, 
			@ModelAttribute("ota") Ota ota, 
			@RequestParam("referer") String referer,
			@RequestParam(value="ota_file", required=false) MultipartFile ota_file
	) throws IOException{
		if( otaService.insert(ota) == 1 ){
			Integer ota_id = ota.getId();
			ota.setDownloadLink(ossService.uploadFile(ota_file, ossService.filePath, ossService.ossOtaPath, ota_id, ota.getDownloadLink()));
			otaService.updateByPrimaryKey(ota);
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/ota/select";
		}
		
		return "OtaView";
	}
	
	@RequestMapping(value="/update/{ota_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("ota_id") Integer ota_id
	){
		Ota ota = otaService.selectByPrimaryKey(ota_id);
		if( ota != null ){
			model.addAttribute("ota", ota);
			if( ota.getDownloadLink() != "" ){
				model.addAttribute("ossUrl", ConstantService.ossUrl);
			}else{
				model.addAttribute("ossUrl", "");
			}
		
			return "OtaView";
		}
		return "redirect:/cms/ota/select";
	}
	
	@RequestMapping(value="update/{ota_id}", method=RequestMethod.POST)
	public String update(
			Model model, 
			HttpSession httpSession, 
			HttpServletRequest request, 
			@PathVariable("ota_id") Integer ota_id, 
			@ModelAttribute("ota") Ota ota,
			@RequestParam("referer") String referer,
			@RequestParam(value="ota_file", required=false) MultipartFile ota_file
	) throws IOException{
		ota.setDownloadLink(ossService.uploadFile(ota_file, ossService.filePath, ossService.ossOtaPath, ota_id, ota.getDownloadLink()));
		
		ota.setId(ota_id);
		if( otaService.updateByPrimaryKeyWithBLOBs(ota) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/ota/select";
		}
		return "OrderView";
	}
	
	@RequestMapping(value="delete", method=RequestMethod.POST)
	public String delete(
			Model model, 
			@RequestParam("ota_id") Integer ota_id
	){
		Ota ota = otaService.selectByPrimaryKey(ota_id);
		if( ota != null ){
			ota.setModifyDate(new Date());
			ota.setDeleted(1);
			otaService.updateByPrimaryKey(ota);
		}
		
		return "redirect:/cms/ota/select";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		funcService.modelAttribute(model, httpSession, request);
	}
	
	
}
