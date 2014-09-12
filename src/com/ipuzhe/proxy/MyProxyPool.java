package com.ipuzhe.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MyProxyPool {
	
	private static Logger logger = Logger.getLogger(MyProxyPool.class);
	
	//这里是所有的代理信息     String[0]: host   String[1]: port
	private static List<String[]> proxyList = new ArrayList<String[]>();
	
	private static long proxyCount = 0;//可用代理的数量
	
	//初始换代理池
	private static void initPool(){
		
	}
	
	//设置一个新的所有代理信息
	private static void initPool(List<String[]> newProxyList){
		proxyList = newProxyList;
	}
	
	//添加一个新的代理
	private static boolean addProxy(String[] proxy){
		return proxyList.add(proxy);
	}
	
	//
	private static boolean addProxy(String host,String port){
		return addProxy(new String[]{host,port});
	}
	
	//删除所有的 代理信息
	private static boolean delAllProxy(){
		proxyList = null;
		return true;
	}
	
	//根据代理的 ip 来删除该ip 的代理信息
	private static boolean delProxy(String host){
		return proxyList.remove(host);
	}

}
