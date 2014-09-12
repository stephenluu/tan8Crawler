package com.ipuzhe.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Test {

	public static void main(String[] args) throws ParserException, ClientProtocolException, IOException {

		new Test().test();
		
		String url = "http://www.77music.com/yuepuku.php?ypop=all&sortby=createtime&time="+new Date().getTime();
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		System.out.println(EntityUtils.toString(response.getEntity()));
		
		Parser parser = new Parser(url);
		
		StringFilter filter = new StringFilter("共有");
		//TagNameFilter filter = new TagNameFilter("html");
		NodeList nodes = parser.extractAllNodesThatMatch(filter);
		Node div = nodes.elementAt(0);
		Node span = div.getNextSibling();
		System.out.println();
		
		System.out.println();
	}

	private void test() {

		String path = "/confg/proxyList.txt";

		URL url = getClass().getResource(path);
		System.out.println(url);
	}

}
