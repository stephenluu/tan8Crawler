package com.ipuzhe.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;

import com.ipuzhe.data.DataManager;
import com.ipuzhe.data.Persistence;
import com.ipuzhe.servlet.Download;

/**
 * 下载图片客户端
 * @author luliuyu
 *
 */
public class Downloader {

	private static final String format = "http://www.tan8.com/flash_get_yp_info.php%s";
	private static int updatedCount = 0;
	
	public static void main(String[] args) {
		
		execute();
	}
	
	public static void execute(){
		
		Queue<String> data =  Persistence.notNullUrl();
		
		if(data.isEmpty())
			System.out.println("图片下载完成");
		else{
			DataManager dataManager = new DataManager(data );
			@SuppressWarnings("unused")
			Download downlad  = null;
			
			while(dataManager.hasNext()){
				
				downlad  = new Download();
				String auth = dataManager.next();
				String phpUrl = String.format(format, auth);
				phpUrl = "http://localhost:8080/77CrawlerServer/Download?url="+phpUrl;
				
				try {
					URL url = new URL(phpUrl);
					System.out.println("访问："+phpUrl);
					
					HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
					conn.setConnectTimeout(5000);
					InputStream is = conn.getInputStream();
					is.close();
					
					System.out.println("No."+ ++updatedCount); 
					//conn.getInputStream();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			execute();
		}
	}
}
