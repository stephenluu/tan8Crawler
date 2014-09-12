package com.ipuzhe.client;

import java.io.File;

/**
 * 图片处理客户端
 * 改底色 去水印
 * @author luliuyu
 * @see PicProcessThread MusicCaseUtils
 *
 */
public class PicProcessClient {

	private static  File[] files = null;
	private static int curr = 0;
	private static   File[] cloneFiles ;
	 
	
	public static void main(String[] args) {
		// TODO 

		//File folder = new File("E:/upload2/upload/images/musiccase");
		File folder = new File("E:/crawler_77");
		
		if(folder.isDirectory()){
			files = folder.listFiles();
			cloneFiles = files.clone();
			
		}else{
			System.out.println("目标不是文件夹");
		}
		
		
		for (int i = 0; i < 5; i++) {
			Thread t = new Thread(new PicProcessThread());
			t.start();
			System.out.println(t.getName()+" started.");
		}
	}


	/**
	 * 基于安全，返回数组的拷贝 , 性能相比估计会下降
	 * 所以使用新的域，避免多次开销
	 * @return
	 */
	public static File[] getFiles() {
		
		//return files;
		//return files.clone();
		return cloneFiles;
	}


	public static int getCurr() {
		
		return curr;
	}


	public static void setCurr(int curr) {
		PicProcessClient.curr = curr;
	}
	
	/**
	 * 多线程性能分析
	 * 
	 * 核心数：4 核
	 * 
	 * 张数 时间 线程数
	 * 
	 * 400 63 2
	 * 
	 * 400 47 4
	 * 
	 * 400 47 4
	 * 
	 * 400 47 5
	 * 
	 * 400 48 6
	 * 
	 * 400 48 8
	 * 
	 * 400 48 8
	 * 
	 * 400 49 10
	 * 
	 * 400 49 10
	 * 
	 * 400 47 20
	 * 
	 * 400 48 20
	 * 
	 * 400 49 30
	 * 
	 * 
	 * 结论 ： 线程数得大于核心核心才能100% 占用cpu。
	 *  	线程数多于核心数的也不会有速度的增加。 
	 *  	最佳线程数：核心数 + 1
	 */
	

}
