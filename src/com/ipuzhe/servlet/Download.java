package com.ipuzhe.servlet;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.resource.spi.IllegalStateException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ipuzhe.data.Persistence;
import com.ipuzhe.util.HttpUtil;
import com.ipuzhe.vo.Piano;

/**
 * 接受chrome 插件转发过来的infourl
 * @author luliuyu
 *
 */
public class Download extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(Download.class);
	
	
	private static final String IMAGE_SUFFIX = ".ypad";//乐谱图片的后缀名字
	private static int totalPage = 0;

	public Download() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
//		String imagePath = request.getParameter("url");//获取乐谱图片的地址
//		getAndWriteImage(imagePath);//进行图片的读和写
		
		//http://www.77music.com/flash_get_yp_info.php?ypid=19001&sccode=cceae5d0ca49fc34bb75d0333558fde2&r1=4660&r2=5243&input=123
		String phpUrl = request.getParameter("url");
		String sccode = request.getParameter("sccode");
		String r1 = request.getParameter("r1");
		String r2 = request.getParameter("r2");
		
		phpUrl = phpUrl + "&sccode=" + sccode + "&r1=" + r1 + "&r2=" + r2;
		
		Piano piano = getJSonFromPhpUrl(phpUrl);//从 url 链接直接获取相关的信息
		
		
		createImageUrl(piano);
	}
	
	public void download(String phpUrl)throws Exception {
		
		Piano piano = getJSonFromPhpUrl(phpUrl);//从 url 链接直接获取相关的信息
		
		logger.debug("准备下载...");
		createImageUrl(piano);
	};
	
	/**
	 * 构造相应的 url ，并且进行下载
	 * @param piano
	 */
	public void createImageUrl(Piano piano){
		
		totalPage = piano.getCount();
		
		//下载第一张图片 http://77music2.oss-cn-hangzhou.aliyuncs.com/yuepuku/63/31510/prev_31510.0.png
		int index = "http://www.tan8.com/yuepuku/".length();
		String createUrl = piano.getUrl().substring(0, index);
		createUrl = createUrl + piano.getdId() + "/" + piano.getpId() + "/prev_" + piano.getpId() + ".0.png";
		
		getAndWriteImage(createUrl,0);//下载第一张图片
		
		//下载其他页数的一些图片
		for(int i=1;i<totalPage;i++){
			String imagePath = piano.getUrl() + "." + i + ".png";
			getAndWriteImage(imagePath, i);
		}
		
		
		
	}
	


	/**
	 * 获取一张图片，并且写入硬盘
	 * 保存数据库
	 * @param imagePath
	 */
	public void getAndWriteImage(String imagePath,int page){
		
		page++;
		
		String[] args = imagePath.split("[/?]");
		String pId = args[5];//乐谱的 id
		Long mid = Persistence.getMidByQuId(Long.valueOf(pId));
		if (mid == 0L)
			throw new IllegalArgumentException("乐谱不存在");
		
		int n = Persistence.countPic(mid);
		List<Integer> list =  Persistence.getPageList(mid);
		
		if (n < totalPage ){
			
			if(!list.contains(page))
			{
				String filename = HttpUtil.getMethod(imagePath, pId);//写入图片
				if(filename == null)
					throw new NullPointerException("IO写入出错");
				Persistence.savePicture("/upload/images/musiccase/"+filename, page, mid);
			}
				
			else logger.debug("图片已存在："+mid+" page:"+page);
		}
		if (++n >= totalPage){
			Persistence.updateIsDownPic(mid);
			logger.debug(totalPage+" 张图片已经全部下载："+mid);
		}
			
		
	}
	
	/**
	 * 拿取json的数据
	 * @param phpUrl
	 * @return
	 */
	public Piano getJSonFromPhpUrl(String phpUrl) {
		
		logger.debug("正在进行代理队列的选择");
		String jSonContent = HttpUtil.httpGetMethod(phpUrl,"UTF-8");
		
		String regex = "<yp_create_time>(.+?)</yp_create_time>";//创建时间
		String createTime = null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(jSonContent);
		
		if(m.find()) {
			createTime = m.group(1);
		}
		
		regex = "<yp_title>(.+?)</yp_title>";//标题
		String title = null;
		p = Pattern.compile(regex);
		m = p.matcher(jSonContent);
		
		if(m.find()) {
			title = m.group(1);
		}		
		
		
		regex = "<yp_page_count>(.+?)</yp_page_count>";//页数
		int count = 1;
		p = Pattern.compile(regex);
		m = p.matcher(jSonContent);
		if(m.find()) {
			count = Integer.parseInt(m.group(1));
		}		
		
		regex = "<ypad_url>(.+?)</ypad_url>";//图片的 url
		String securityCode = null;
		String url = null;
		String pName = null;
		p = Pattern.compile(regex);
		m = p.matcher(jSonContent);
		if(m.find()) {
			url = m.group(1);
		}
		
		
		String[] strs = url.split("[/]");
		for(String str : strs){
			if(str.endsWith(IMAGE_SUFFIX)){
				pName = str;
				break;
			}
		}
		
		strs = pName.split("[_.]");
		
		securityCode = strs[1];//防伪码
		
		String[] strs2;
		strs2 = url.split("/");
	
		String dId = strs2[4];
		String pId = strs2[5];
		
		
		Piano piano = new Piano();
		piano.setCount(count);
		piano.setCreateTime(createTime);
		piano.setdId(dId);
		piano.setpId(pId);
		piano.setSecurityCode(securityCode);
		piano.setUrl(url);
		piano.setTitle(title);
		
		printlnAll(piano);
		
		return piano;
	}
	
	public void printlnAll(Piano piano){
		
		System.out.println("pId = " + piano.getpId());
		System.out.println("dId = " + piano.getdId());
		System.out.println("count = " +piano.getCount());
		System.out.println("createTime = " + piano.getCreateTime());
		System.out.println("title = " + piano.getTitle());
		System.out.println("url = " + piano.getUrl());
		System.out.println("SecurityCode = " + piano.getSecurityCode());
		
	}
}