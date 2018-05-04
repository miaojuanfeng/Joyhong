package com.joyhong.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.joyhong.api.TwitterController;

public class TwitterListener implements ServletContextListener{
	
	private Logger logger = Logger.getLogger(this.getClass());

	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		logger.info("TwitterListener: ServletContext initialized");
		/*
		 * 因为Listener先于Servlet启动，
		 * 此时DispatcherServlet还未启动，无法使用Autowired自动注入
		 * 所以需要手动创建对象
		 */
		TwitterController twitterController = new TwitterController();
		twitterController.listener();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		logger.info("TwitterListener: ServletContext destroyed");
	}
	
	
}
