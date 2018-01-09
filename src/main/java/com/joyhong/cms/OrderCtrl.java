package com.joyhong.cms;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joyhong.model.Order;
import com.joyhong.model.User;
import com.joyhong.service.OrderService;

@Controller
@RequestMapping("cms/order")
public class OrderCtrl {
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public String select(){
		return "redirect:/cms/order/select/1";
	}
	
	@RequestMapping(value="/select/{page}", method=RequestMethod.GET)
	public String select(Model model, HttpSession httpSession, @PathVariable(value="page") Integer page){
		
		if( !isLogin(model, httpSession, "select") ){
			return "redirect:/cms/user/login";
		}
		
		int pageSize = 20;
		int totalRecord = orderService.selectCount();
		int totalPage = (int)Math.ceil((double)totalRecord/pageSize);
		
		if( page < 1 || page > totalPage ){
			page = 1;
		}
		
		Integer offset = (page-1)*pageSize;
		List<Order> order = orderService.selectOffsetAndLimit(offset, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("order", order);
		
		return "OrderView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.GET)
	public String insert(Model model, HttpSession httpSession){
		
		if( !isLogin(model, httpSession, "insert") ){
			return "redirect:/cms/user/login";
		}
		
		model.addAttribute("order", new Order());

		return "OrderView";
	}
	
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public String insert(Model model, HttpSession httpSession, @ModelAttribute("order") Order order){
		
		if( !isLogin(model, httpSession, "insert") ){
			return "redirect:/cms/user/login";
		}
		
		if( orderService.insert(order) == 1 ){
			return "redirect:/cms/order/select";
		}
		
		return "OrderView";
	}
	
	@ModelAttribute
	public void upload(@RequestParam(value="id", required=false) Integer id, Model model){
		if( id != null ){
			Order order = orderService.selectByPrimaryKey(id);
			model.addAttribute("order", order);
		}
	}
	
	@RequestMapping(value="/update", method=RequestMethod.GET)
	public String update(Model model, HttpSession httpSession, @RequestParam("id") Integer id){
		
		if( !isLogin(model, httpSession, "update") ){
			return "redirect:/cms/user/login";
		}
		
		Order order = orderService.selectByPrimaryKey(id);
		if( order != null ){
			model.addAttribute("order", order);
		
			return "OrderView";
		}
		return "redirect:/cms/order/select";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(Model model, HttpSession httpSession, @RequestParam("id") Integer id, @ModelAttribute("order") Order order){
		
		if( !isLogin(model, httpSession, "insert") ){
			return "redirect:/cms/user/login";
		}
		
		order.setId(id);
		order.setModifyDate(new Date());
		if( orderService.updateByPrimaryKey(order) == 1 ){
			return "redirect:/cms/order/select";
		}
		
		return "OrderView";
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(@RequestParam("order_id") Integer order_id, Model model, HttpSession httpSession){
		
		if( !isLogin(model, httpSession, "delete") ){
			return "redirect:/cms/user/login";
		}
		
		Order order = orderService.selectByPrimaryKey(order_id);
		if( order != null ){
			order.setModifyDate(new Date());
			order.setDeleted(1);
			orderService.updateByPrimaryKey(order);
		}
		
		return "redirect:/cms/order/select";
	}
	
	private boolean isLogin(Model model, HttpSession httpSession, String method){
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			return false;
		}
		model.addAttribute("method", method);
		model.addAttribute("user_nickname", user.getNickname());
		return true;
	}
	
}
