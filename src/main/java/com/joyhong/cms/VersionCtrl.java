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

import com.joyhong.model.Version;
import com.joyhong.service.VersionService;
import com.joyhong.service.common.ConstantService;
import com.joyhong.service.common.FuncService;
import com.joyhong.service.common.OssService;

@Controller
@RequestMapping("cms/version")
public class VersionCtrl {
	
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private FuncService funcService;
	
	@Autowired
	private OssService ossService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(){
		return "redirect:/cms/version/select/1";
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(
			Model model,  
			@PathVariable(value="page") Integer page
	){
		int pageSize = 20;
		int totalRecord = versionService.selectCount();
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Version> version = versionService.selectOffsetAndLimit(offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("version", version);
		model.addAttribute("ossUrl", ConstantService.ossUrl);
		
		return "VersionView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.GET)
	public String insert(
			Model model
	){
		model.addAttribute("version", new Version());

		return "VersionView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public String insert(
			Model model, 
			@ModelAttribute("version") Version version, 
			@RequestParam("referer") String referer,
			@RequestParam(value="version_file", required=false) MultipartFile version_file
	) throws IOException{
		if( versionService.insert(version) == 1 ){
			Integer version_id = version.getId();
			version.setDownloadLink(ossService.uploadFile(version_file, ossService.filePath, ossService.ossVersionPath, "vid"+version_id, version.getDownloadLink()));
			versionService.updateByPrimaryKey(version);
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/version/select";
		}
		
		return "VersionView";
	}
	
	@RequestMapping(value="/update/{version_id}", method=RequestMethod.GET)
	public String update(
			Model model, 
			@PathVariable("version_id") Integer version_id
	){
		Version version = versionService.selectByPrimaryKey(version_id);
		if( version != null ){
			model.addAttribute("version", version);
			if( version.getDownloadLink() != "" ){
				model.addAttribute("ossUrl", ConstantService.ossUrl);
			}else{
				model.addAttribute("ossUrl", "");
			}
		
			return "VersionView";
		}
		return "redirect:/cms/version/select";
	}
	
	@RequestMapping(value="/update/{version_id}", method=RequestMethod.POST)
	public String update(
			Model model, 
			HttpSession httpSession, 
			HttpServletRequest request, 
			@PathVariable("version_id") Integer version_id, 
			@ModelAttribute("version") Version version, 
			@RequestParam("referer") String referer,
			@RequestParam(value="version_file", required=false) MultipartFile version_file
	) throws IOException{
		version.setDownloadLink(ossService.uploadFile(version_file, ossService.filePath, ossService.ossVersionPath, "vid"+version_id, version.getDownloadLink()));
		
		version.setId(version_id);
		if( versionService.updateByPrimaryKeyWithBLOBs(version) == 1 ){
			if( referer != "" ){
				return "redirect:"+referer.substring(referer.lastIndexOf("/cms/"));
			}
			return "redirect:/cms/version/select";
		}
		
		return "VersionView";
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(
			Model model, 
			@RequestParam("version_id") Integer version_id
	){	
		Version version = versionService.selectByPrimaryKey(version_id);
		if( version != null ){
			version.setModifyDate(new Date());
			version.setDeleted(1);
			versionService.updateByPrimaryKey(version);
		}
		
		return "redirect:/cms/version/select";
	}
	
	@ModelAttribute
	public void startup(Model model, HttpSession httpSession, HttpServletRequest request){
		funcService.modelAttribute(model, httpSession, request);
	}
}
