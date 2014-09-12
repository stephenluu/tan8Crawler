package com.ipuzhe.vo;

public class Piano {
	
	private String pId;//该乐谱的 id
	
	private String dId;//该乐谱的文件夹 id
	
	private int count;//多少张
	
	private String createTime;//给乐谱创建的时间
	
	private String title ;//该乐谱的名字
	
	private String url ;//只需要某一个url的值即可
	
	private String SecurityCode ;

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getdId() {
		return dId;
	}

	public void setdId(String dId) {
		this.dId = dId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSecurityCode() {
		return SecurityCode;
	}

	public void setSecurityCode(String securityCode) {
		SecurityCode = securityCode;
	}
	
	

}
