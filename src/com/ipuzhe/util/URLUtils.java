package com.ipuzhe.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class URLUtils {
	private static Logger logger = Logger.getLogger(FileUtils.class);
	
	@SuppressWarnings("unused")
	public static URL toURL(String urlString) {
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			logger.error("urlString to url has an error ", e);
		}
		return url;
	}
}
