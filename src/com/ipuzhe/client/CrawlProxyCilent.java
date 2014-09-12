package com.ipuzhe.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ipuzhe.init.SystemInit;
import com.ipuzhe.proxy.MyProxyUtil;
import com.ipuzhe.util.FileUtils;
import com.ipuzhe.vo.Ip;

public class CrawlProxyCilent {

	private static final String URL = "http://www.xici.net.co/nt/1";
	
	public static void main(String[] args) throws ClientProtocolException, IOException, ServletException, ParserException {
		
		Set<String> set = new HashSet<String>();
		
		SystemInit init = new SystemInit();
		init.init();
		PriorityQueue<Ip> ipsQue = MyProxyUtil.getProxyQueue();
		
		for (Ip ip : ipsQue) {
			if(check(ip.getHost(),ip.getPort())){
				set.add(ip.getHost()+":"+ip.getPort());//把本地有效的放进来
			}
		}
		
		//下载网上的放进set
		Parser parser = new Parser(URL);
		NodeList tables = parser.extractAllNodesThatMatch(new TagNameFilter("table"));

		Node tr = null;
		Node table = tables.elementAt(0);
		NodeList trs = table.getChildren();
		
		String port ;
		String host;
		for (int i = 0,length = trs.size(); i < length; i++) {
			tr = trs.elementAt(i);
			
			if (tr instanceof TableRow) {
				NodeList children = tr.getChildren();
				host = getFirstText(children.elementAt(3));
				port = getFirstText(children.elementAt(5));
				String ip = host + ":" + port;
				
				if (!set.contains(ip)  && check(host, port))
					set.add(ip);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (String ip : set) {
			sb.append(ip+"\n");
		}
		String content = sb.toString();
		
		String path = FileUtils.toURL("classpath:/proxyList.txt").getFile();
		FileUtils.writeByString(content, path);
		
		System.out.println("有效代理总数："+set.size());

	}
	
	public static boolean check(String host, String port) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://web.chacuo.net/netproxycheck");
		List<NameValuePair> nvps= new ArrayList<NameValuePair>();
	
		nvps.add(new BasicNameValuePair("data", host));
		nvps.add(new BasicNameValuePair("type","proxycheck"));
		
		nvps.add(new BasicNameValuePair("arg","p="+port+"_t=1_o=5"));
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		HttpResponse re = null;
		try {
			re = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean isValid = false;
		if(re.getStatusLine().getStatusCode() == 200 ) {
			String content = null;
			try {
				content = EntityUtils.toString(re.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String str = FileUtils.chinaToUnicode("属于");
			if(content.indexOf(str) != -1 )
				isValid =  true;
			else System.err.println(host);
		}else{
			System.err.println("请求失败");
		}
		
		return isValid;
	}
	
	private static String getFirstText(Node node){
		return node.getChildren().elementAt(0).getText();
	}
	

}
