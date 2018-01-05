package com.joyhong.cms;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public String select(Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession, "select") ){
			return "redirect:/cms/user/login";
		}
		
//		List<Order> order = orderService.selectLikeOrderToken("");
//		model.addAttribute("order", order);
		
		return "OrderView";
	}
	
	@RequestMapping(value="/insert", method={RequestMethod.GET,RequestMethod.POST})
	public String insert(Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession, "insert") ){
			return "redirect:/cms/user/login";
		}
		
//		List<Order> order = orderService.selectLikeOrderToken("");
//		model.addAttribute("order", order);
		
		return "OrderView";
	}
	
	@RequestMapping(value="/update/{order_id}", method={RequestMethod.GET,RequestMethod.POST})
	public String update(@PathParam("order_id") Integer order_id, Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession, "update") ){
			return "redirect:/cms/user/login";
		}
		
//		List<Order> order = orderService.selectLikeOrderToken("");
//		model.addAttribute("order", order);
		
		return "OrderView";
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(@RequestParam("order_id") Integer order_id, Model model, HttpSession httpSession){
		
		if( !permission(model, httpSession, "delete") ){
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
	
	private boolean permission(Model model, HttpSession httpSession, String router){
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			return false;
		}
		model.addAttribute("router", router);
		model.addAttribute("user_nickname", user.getNickname());
		return true;
	}
	
}
