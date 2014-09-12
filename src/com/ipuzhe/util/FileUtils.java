package com.ipuzhe.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ipuzhe.util.EncoderUtil;

public class FileUtils {
	
	private static Logger logger = Logger.getLogger(FileUtils.class);

	public static final String CLASSPATH_URL_PREFIX = "classpath:"; // classpath
	public static final String FILE_URL_PREFIX = "file:"; // file prefix

	public static void main(String[] args) {
		
		URL url = toURL("classpath:/");
		System.out.println(url.getPath());
	}
	

	/**
	 * 相对路径转URL
	 * @param path
	 * @return
	 */
	public static URL toURL(String path) {
		URL url = null;
		if (StringUtils.isNotBlank(path)) {
			if (path.startsWith(CLASSPATH_URL_PREFIX)) {
				path = path.substring(CLASSPATH_URL_PREFIX.length());
				url = FileUtils.class.getResource(path);
				
			}

			if (null == url) {
				logger.error(path + " cannot be found.");
			}
		}

		return url;
	}

	public static URI toURI(URL url) {
		URI uri = null;
		try {
			uri = new URI(url.toString().replaceAll(" ", "%20"));
		} catch (URISyntaxException e) {
			logger.error("url to uri has an error.", e);
		}
		return uri;
	}

	public static InputStream openInputStream(String path) {
		InputStream inStream = null;
		if (StringUtils.isNotBlank(path)) {
			URL url = toURL(path);
			if (null != url) {
				try {
					inStream = new FileInputStream(toURI(url)
							.getSchemeSpecificPart());
				} catch (FileNotFoundException e) {
					logger.error(path + " file cannot be found.", e);
				}
			}
		}
		return inStream;
	}

	public static File getFile(String filePath) {
		File file = null;
		URL url = toURL(filePath);
		if (null != url) {
			file = new File(toURI(url).getSchemeSpecificPart());
		}
		return file;
	}

	public static File[] getFiles(String filePaths) {
		List<File> files = null;
		if (StringUtils.isNotBlank(filePaths)) {
			String[] filePathArr = filePaths.split("\\s*,\\s*");
			if (null != filePathArr && filePathArr.length > 0) {
				files = new ArrayList<File>();

				String filePath = null;
				int pos;
				String path, fileName, patternStr;
				File searchPath;
				File[] subFiles;
				for (int i = 0; i < filePathArr.length; i++) {
					filePath = filePathArr[i];
					if (filePath.indexOf("*") >= 0) {
						if (filePath.startsWith(CLASSPATH_URL_PREFIX)) {
							filePath = filePath.substring(CLASSPATH_URL_PREFIX
									.length());
						}
						pos = filePath.lastIndexOf("/");
						path = null;
						fileName = null;
						searchPath = null;
						if (pos >= 0) {
							path = filePath.substring(0, pos);
							searchPath = getFile(path);
							fileName = filePath.substring(pos + 1);
						} else {
							searchPath = getFile("./");
							fileName = filePath;
						}

						patternStr = fileName.replace('.', '#');
						patternStr = patternStr.replaceAll("#", "\\\\.");
						patternStr = patternStr.replace('*', '#');
						patternStr = patternStr.replaceAll("#", ".*");
						patternStr = patternStr.replace('?', '#');
						patternStr = patternStr.replaceAll("#", ".?");
						patternStr = "^" + patternStr + "$";

						final Pattern pattern = Pattern.compile(patternStr);
						subFiles = searchPath.listFiles(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return pattern.matcher(name).find();
							}
						});
						if (null != subFiles && 0 < subFiles.length) {
							for (File f : subFiles) {
								files.add(f);
							}
						}
					} else {
						files.add(getFile(filePath));
					}
				}
			}
		}
		return files.toArray(new File[files.size()]);
	}
	
	/** 
     * 把中文转成Unicode码 
     * @param str 
     * @return 
     */  
    public static String chinaToUnicode(String str){  
        String result="";  
        for (int i = 0; i < str.length(); i++){  
            int chr1 = (char) str.charAt(i);  
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)  
                result+="\\u" + Integer.toHexString(chr1);  
            }else{  
                result+=str.charAt(i);  
            }  
        }  
        return result;  
    }  
    
    /**
	 * 写字节数组
	 * @param b
	 * @param string
	 * @return
	 */
	public static String writeByte(byte[] b, String string){
		
		string = EncoderUtil.encode(string);
		
		int p = string.lastIndexOf('/');
		String folder = string.substring(0, p);
		//String filename = string.substring(p+1);
		
		//System.out.println("路径："+string);
		File f = new File(folder);
		f.mkdirs();
		
		File file = new File(string);
		
		OutputStream os = null;
		
		try {
			os = new FileOutputStream(file);
			os.write(b);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return string;
	}
	
	
	
	/**
	 * 写字符串文件
	 * @param content
	 * @param path
	 * @return
	 */
	public static String writeByString(String content,String path){
		
		File file = new File(path); 
		
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (os == null)  return null;
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os)); //一层一层装饰
		
		try {
			
			bw.write(content);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return path;
	}
}
