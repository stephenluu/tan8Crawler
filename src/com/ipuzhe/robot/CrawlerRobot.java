package com.ipuzhe.robot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * 77music.com crawler robot
 * @author Leon Lee
 * @since 2014-06-30
 */
public class CrawlerRobot {

	private Robot robot;
	private Clipboard sysClipboard;
	
	public CrawlerRobot() {
		try {
			robot = new Robot();
			robot.setAutoDelay(100);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	public void moveTo(int x, int y) {
		robot.mouseMove(x, y);
	}
	
	public void delay(int ms) {
		robot.delay(ms);
	}

	public void mouseLeftClick() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public void click(int keycode) {
		robot.keyPress(keycode);
		robot.keyRelease(keycode);
	}
	
	public void shortcutKey(int keyCode) {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	public void setSystemClipboard(String cont) {   
		StringSelection ss = new StringSelection(cont);  
		sysClipboard.setContents(ss, null);  
	}  
	
	public Robot getRobot() {
		return robot;
	}
	
}