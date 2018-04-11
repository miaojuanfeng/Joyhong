package com.joyhong.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.joyhong.model.User;

public class LoginFilter implements Filter{
	
	private List<String> exceptPath;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		String requestURI = httpRequest.getRequestURI();
		String requestPath = requestURI.substring(httpRequest.getServletContext().getContextPath().length());
		
		if( this.exceptPath != null && this.exceptPath.contains(requestPath) ){
			chain.doFilter(httpRequest, httpResponse);
			return;
		}
		
		HttpSession httpSession = httpRequest.getSession();
		User user = (User)httpSession.getAttribute("user");
		if( user == null ){
			httpResponse.sendRedirect(httpRequest.getContextPath()+"/cms/login");
		}else{
			chain.doFilter(httpRequest, httpResponse);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		String path = filterConfig.getInitParameter("exceptPath");
		if( path != null ){
			this.exceptPath = new ArrayList<String>();
			for(String p : path.split(",")){
				this.exceptPath.add(p);
			}
		}else{
			this.exceptPath = null;
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
