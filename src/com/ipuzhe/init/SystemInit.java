package com.ipuzhe.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.ipuzhe.proxy.MyProxyUtil;
import com.ipuzhe.util.FileUtils;
import com.ipuzhe.vo.Ip;

public class SystemInit extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(SystemInit.class);
	
	private static final String DIR = "classpath:/proxyList.txt";

	
	public SystemInit() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}

	public void init() throws ServletException {
		
		//这里进行读取配置文件中的所有代理 ip 的内容,是一个txt文件 每一行的格式为 111.1.36.166:81
		
		URL path = FileUtils.toURL(DIR);
		File file = new File(path.getPath());
		
		try {
			InputStream in = new FileInputStream(file);
			String lines = null;
			BufferedReader reader = null;
			
			reader = new BufferedReader(new InputStreamReader(in));// 链接
			
			while( ( lines = reader.readLine() ) != null){
				System.out.println("lines = " + lines);
				MyProxyUtil.addProxy2Queue(lines);//添加到优先队列中
				MyProxyUtil.addProxy2List(lines);//添加到列表中
			}
			
			
			
//			Ip test = new Ip("1111","2222");
//			test.setLastesTime(new Date().getTime());
//			MyProxyUtil.addProxy2Queue(test);
//			test.setLastesTime(123456);
//			MyProxyUtil.addProxy2Queue(test);
//			
//			MyProxyUtil.printlnAllQueue();
			logger.debug("队列的大小为 "+ MyProxyUtil.getProxyQueue().size());
			
			reader.close();
			in.close();
			
		} catch (FileNotFoundException e) {
			logger.error("[SystemInit] init() 文件FileInputStream()出错 " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("[SystemInit] init() 文件BufferedReader()出错 " + e.getMessage());
			e.printStackTrace();
		}
		
	}

}
