package com.ipuzhe.vo;

import java.io.Serializable;

public class Ip implements Serializable , Comparable<Ip>{
	
	private static final long serialVersionUID = -2063989362618200142L;

	private String host;// ip
	
	private String port;//端口
	
	private String hostAndPort;// ip 和  端口
	
	private long lastesTime = 0;//最近一次使用的时间
	
	private long useTime = 0;//已经使用的次数
	
	private boolean available = true;//是否可用
	
	public Ip(){
		this.lastesTime = 0;
		this.useTime = 0 ;
		this.available = false;
	}
	
	public Ip(String host,String port){
		hostAndPort = host + ":" + port;
		this.host = host;
		this.port = port;
		this.lastesTime = 0;
		this.useTime = 0 ;
		this.available = false;		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHostAndPort() {
		return hostAndPort;
	}

	public void setHostAndPort(String hostAndPort) {
		this.hostAndPort = hostAndPort;
	}

	public long getLastesTime() {
		return lastesTime;
	}

	public void setLastesTime(long lastesTime) {
		this.lastesTime = lastesTime;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	//这里进行策略的处理
	@Override
	public int compareTo(Ip o) {
		
		//首先以最近使用的时间来进行区分，类似LRU算法,lastesTime 越小的就在前面
		if(this.getLastesTime() > o.getLastesTime()){
			return 1;
		}else if(this.getLastesTime() < o.getLastesTime()){
			return -1;
		}else if(this.getLastesTime() == o.getLastesTime()){
			return 0;
		}
		return 0;
	}
}
