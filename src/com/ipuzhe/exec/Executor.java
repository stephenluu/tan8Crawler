package com.ipuzhe.exec;

import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import com.ipuzhe.client.UpdateClient;
import com.ipuzhe.data.DataManager;
import com.ipuzhe.robot.CrawlerRobot;

/**
 * Executor or Controller
 * @author Leon Lee  luliuyu
 * @since 2014-06-30
 */
public class Executor extends Thread {

	private static final Logger logger = Logger.getLogger(Executor.class);
	
	private static CrawlerRobot robot;
	
	private static final String SWF_URL = "http://www.77music.com/flash/77player.swf?id=%s&ver=20130318c&ref=";
	/**
	 * 浏览器任务栏坐标
	 */
	private static final int BROWSER_X = 90;//150; 
	private static final int BROWSER_Y = 870;//880;
	/**
	 * 地址栏坐标
	 */
	private static final int ADDRESS_BAR_X = 200;
	private static final int ADDRESS_BAR_Y = 50;
	/**
	 * 播放按钮坐标
	 */
	private static final int BUTTON_X = 260;// 710;
	private static final int BUTTON_Y = 450;//
	private static final int READ_DELAY = 2*1000;		// 加载等待时间
	
	private boolean isContinue = true;
	
	private DataManager dateManager;
	
	
	
	public Executor(DataManager dateManager) {
		super();
		this.dateManager = dateManager;
	}

	public static void init() {
		CrawlerRobot robot = Executor.getRobot();
		robot.delay(3*1000);
		robot.moveTo(BROWSER_X, BROWSER_Y);
		robot.mouseLeftClick();
	}
	
	public void run() {
		while(isContinue) {
			isContinue = excute();
			
		}
	}
	
	
	public  boolean excute() {
		
		robot = getRobot();
		String swfUrl = null;
		if (dateManager.hasNext()) {
			swfUrl = String.format(SWF_URL, dateManager.next());
			logger.info("[Executor] Browser loading " + swfUrl);
			// process
			// click address bar
			robot.moveTo(ADDRESS_BAR_X, ADDRESS_BAR_Y);
			robot.mouseLeftClick();
			// all select
			robot.shortcutKey(KeyEvent.VK_A);
			// enter the swf url
			robot.setSystemClipboard(swfUrl);	
			robot.shortcutKey(KeyEvent.VK_V);
			robot.click(KeyEvent.VK_ENTER);
			// load url, read delay
			robot.delay(READ_DELAY);
			// click button
			robot.moveTo(BUTTON_X, BUTTON_Y);
			robot.mouseLeftClick();
			return true;
		} else {
			
			UpdateClient.executeNewThread(); //重新来一遍
			logger.info("[Executor] Data is empty, Executor shutdow now...");
			return false;
		}
	}
	
	private static CrawlerRobot getRobot() {
		if (null == robot) {
			robot = new CrawlerRobot();
		}
		return robot;
	}
	
	
	public static void main(String[] args) {
		init();
	}
}
