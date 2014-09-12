package com.ipuzhe.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.ipuzhe.data.DataManager;
import com.ipuzhe.data.Persistence;

public class InitListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(InitListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//DataManager.load("classpath:data");
		logger.info("[DataManager] load data completed!");
		
		// TODO 检查77是否有新的乐谱，有就趴下来。
		updateFrom77();
		
		//eraser();
	}
	
	private void updateFrom77() {

		Long count = Persistence.countMusicCase();
		System.out.println(count);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO 
	}

	
}
