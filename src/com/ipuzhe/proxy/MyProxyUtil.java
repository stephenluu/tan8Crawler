package com.ipuzhe.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import com.ipuzhe.vo.Ip;

public class MyProxyUtil {
	
	private static Logger logger = Logger.getLogger(MyProxyUtil.class);
	
	//存储代理的优先队列
	private static PriorityQueue<Ip> proxyQueue = new PriorityQueue<Ip>();

	//存储所有代理的列表
	private static List<String[]> proxyList = new ArrayList<String[]>();
	
	//队列初始化,将所有的proxyList 都存入 这个优先队列当中去
	public static void proxyQueueInit(List<String[]> proxyList){
		for(String[] str : proxyList){
			Ip ip = new Ip(str[0],str[1]);
			proxyQueue.add(ip);
		}
	}
	
	//向队列中添加新的代理
	public static boolean addProxy2Queue(String host,String port){
		Ip ip = new Ip(host,port);
		return addProxy2Queue(ip);
	}
	
	public static boolean addProxy2Queue(String hostAndPort){
		String[] str = hostAndPort.split("[:]");
		if(str.length >= 2)
			return addProxy2Queue(str[0],str[1]);
		else 
			return false;
	}
	
	public static boolean addProxy2Queue(Ip ip){
		return proxyQueue.add(ip);
	}
	
	//将代理信息添加到 proxyList 中
	public static boolean addProxy2List(String host,String port){
		String[] str = new String[]{host,port};
		return addProxy2List(str);
	}
	
	public static boolean addProxy2List(String hostAndPort){
		String[] str = hostAndPort.split("[:]");
		if(str.length >= 2)
			return addProxy2List(str[0],str[1]);
		else 
			return false;
	}
	
	public static boolean addProxy2List(String[] str){
		return proxyList.add(str);
	}	
	
	//获取优先队列的顶部元素，并且进行删除操作
	public static Ip getProxyFromQueue(){
		return proxyQueue.poll();
	}
	
	//代理优先队列是否为空
	public static boolean isQueueEmpty(){
		return proxyQueue.isEmpty();
	}
	
	//代理列表是否为空
	public static boolean isListEmpty(){
		return proxyList.isEmpty();
	}
	
	//测试用的,第二次测试
	public static void printlnAllQueue(){
		System.out.println("=======================================");
		while(proxyQueue.isEmpty() == false){
			Ip ip = proxyQueue.poll();
			System.out.println(ip.getHost());
			System.out.println(ip.getPort());
			System.out.println(ip.getLastesTime());
		}
		System.out.println("=======================================");
	}

	public static PriorityQueue<Ip> getProxyQueue() {
		return proxyQueue;
	}

	public static void setProxyQueue(PriorityQueue<Ip> proxyQueue) {
		MyProxyUtil.proxyQueue = proxyQueue;
	}

	public static List<String[]> getProxyList() {
		return proxyList;
	}

	public static void setProxyList(List<String[]> proxyList) {
		MyProxyUtil.proxyList = proxyList;
	}
}
