package com.ipuzhe.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ipuzhe.data.Persistence;
import com.ipuzhe.proxy.MyProxyUtil;
import com.ipuzhe.vo.Ip;


/**
 * 直接自己发送请求
 * @author Administrator
 *
 */
public class HttpUtil {

	private static final String DIR = "E:/crawler_77";//保存在本地电脑中的位置
	
	private static final String IMAGE_SUFFIX = ".png";//图片的后缀名
	
	private static final Logger logger = Logger.getLogger(HttpUtil.class);

	
	
	/**
	 * 自定义的 get 请求，codeType 是用来标识该网页的编码类型
	 * @param url
	 * @return 文件名
	 */
	public static String getMethod(String iamgePath,String pid) {
		
		String fileName = null;
		try {
	        //new一个URL对象  
	        URL url = new URL(iamgePath);  
	        //打开链接  
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
	        //设置请求方式为"GET"  
	        conn.setRequestMethod("GET");  
	        //超时响应时间为5秒  
	        conn.setConnectTimeout(5 * 1000);  
	        //通过输入流获取图片数据  
	        InputStream inStream = conn.getInputStream();  
	        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
	        byte[] data = readInputStream(inStream); //图片的所有二进制内容 
	        
	        //new一个文件对象用来保存图片，默认保存当前工程根目录  
	        String imageName = getImageName(iamgePath);//获取图片的全名
	        
	        //这里根据图片的 id 来创建一个新的文件夹
	        //String dir = String.format(DIR, pid);
	        
	        fileName = pid+"_"+(System.currentTimeMillis()-518400000) + ".png";
			
	        File folder = new File(DIR);
	        if(!folder.exists())
	        	folder.mkdirs();
	        File imageFile = new File( DIR,fileName );  
	        
	        //创建输出流  
	        FileOutputStream outStream = new FileOutputStream(imageFile);  
	        //写入数据  
	        outStream.write(data);  
	        //关闭输出流  
	        outStream.close();  

		} catch (MalformedURLException e) {
			
			Persistence.saveLog("下载图片出错", iamgePath);
			logger.error("[HttpUtil] getMethod() MalformedURLException " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Persistence.saveLog("下载图片出错", iamgePath);
			logger.error("[HttpUtil] getMethod() IOException " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Persistence.saveLog("下载图片出错", iamgePath);
			logger.error("[HttpUtil] getMethod() Exception " + e.getMessage());
			
			e.printStackTrace();
		}
		return fileName;
	}
	
	
	/**
	 * 自定义的  httpGet 请求
	 * TODO:实现代理功能
	 * @param url
	 * @return
	 */
	public static String httpGetMethod(String url,String codeType) {
		try {
			
			URL getUrl = new URL(url);

			//这里实现代理的功能
			Proxy proxy = null ;//
			
			if(MyProxyUtil.isQueueEmpty() == false){
				
				Date date = new Date();
				Ip ip = MyProxyUtil.getProxyFromQueue();// 取出 代理 ip 的相关信息
				
				logger.debug("正在尝试使用代理 ：" + ip.getHost() + ":" + ip.getPort());
				
				long curTime = date.getTime();
				long lastesTime = ip.getLastesTime();//

				if(lastesTime == 0 || curTime - lastesTime >= 15000){
					
					logger.debug("这个代理符合要求 "+ ip.getHost() + ":" + ip.getPort());
					logger.debug("curTime = " + curTime);
					logger.debug("lastesTime = " + lastesTime);
					
					proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip.getHost(),Integer.parseInt(ip.getPort())));
					ip = refreshIp(ip);
					MyProxyUtil.addProxy2Queue(ip);//回收代理
				}else{
					int tryTime = 0;//尝试连接次数，超过三次就不再尝试
					
					while(tryTime < 3 ){
						tryTime ++;
						logger.debug("[正在尝试等待连接]...");
						try{
						     Thread.currentThread().sleep(3000); //自行等待一秒
						}catch(InterruptedException e){
							logger.error("[HttpUtil] Proxy wait 1 second error " + e.getMessage());
						     e.printStackTrace();
						}		
						
						curTime = new Date().getTime();
						
						if(curTime - lastesTime >= 15000){
							proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip.getHost(),Integer.parseInt(ip.getPort())));
							ip = refreshIp(ip);
							MyProxyUtil.addProxy2Queue(ip);	
						}					
					}
					
					ip.setLastesTime(new Date().getTime());
					ip = refreshIp(ip);
					MyProxyUtil.addProxy2Queue(ip);
					logger.debug("[尝试等待连接失败]...tryTime = " + tryTime);
				}
			}
			
			//测试用的
//			Ip ip = new Ip();
//			ip.setHost("42.96.202.45");
//			ip.setPort("82");
//			proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip.getHost(),Integer.parseInt(ip.getPort())));
//			
			// 初始化一个链接到那个url的连接
			HttpURLConnection conn = null;
			if(null != proxy){
				conn = (HttpURLConnection) getUrl.openConnection(proxy);//使用代理
				logger.debug("使用了代理下载 代理ip 为" + proxy.toString() + "下载资源位 ： " + url);
			}else{
				conn = (HttpURLConnection) getUrl.openConnection();//不使用代理
				logger.debug("没有使用代理下载" + "下载资源位 ： " + url);
			}
			
			conn.setConnectTimeout(8000); // 10 秒的超时时间
			
			// 进行链接
			conn.connect();

			// 初始化 BufferedReader输入流来读取URL的响应
			BufferedReader reader = null;
			if ("GBK".equals(codeType)) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));// 链接
			} else if ("UTF-8".equals(codeType)) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));// 链接
			}

			String lines;
			StringBuilder html = new StringBuilder();

			while ((lines = reader.readLine()) != null) {
				System.out.println(lines);
				// 遍历抓取到的每一行并将其存储到result里面
				html.append(lines);
			}
			reader.close();

			return html.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * 读取图片的二进制内容
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
   public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  
   
   /**
    * 获取图片的全名，包括图片的后缀
    * @param imagePath
    * @return
    */
   public static String getImageName(String imagePath){
	   
	   String[] splitMethod = imagePath.split("[/?]");
	   String iamgeName = null;
	   
	   for(int i = 0 ;i < splitMethod.length; i++){
		   
		   if(splitMethod[i].endsWith(IMAGE_SUFFIX)){
			   iamgeName = splitMethod[i];
		   }
	   }
	   
	   return iamgeName;
   }
   
   /**
    * 跟新优先队列中的元素的值
    * @param ip
    */
   public static Ip refreshIp(Ip ip){
	   
	   ip.setLastesTime(new Date().getTime());
	   ip.setUseTime(ip.getUseTime()+1);
	   return ip;
   }
   
   public static void main(String[] args) {
	 getMethod("http://77music2.oss-cn-hangzhou.aliyuncs.com/yuepuku/66/33237/prev_33237.0.png","33237");
}
}
