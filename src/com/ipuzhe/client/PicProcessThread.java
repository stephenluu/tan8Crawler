package com.ipuzhe.client;

import java.io.File;

import com.ipuzhe.util.MusicCaseUtils;

public class PicProcessThread implements Runnable{

	private static boolean flag = true;
	
	@Override
	public void run() {
		// TODO 
		while (flag){
			File file = getFile();
			if (file != null) {
				//System.out.println(Thread.currentThread().getName());
				MusicCaseUtils.picProcess(file.toString());
			}
				
		}
		
		
	}

	private synchronized static File getFile() {
		
		int curr = PicProcessClient.getCurr();
		File[] files = PicProcessClient.getFiles();
		
		if(curr > files.length-1) {
			
			System.out.println("Congratulation! The transform is finished. by "+ Thread.currentThread().getName());
			stopThread();
			return null;
		}

		
		//System.out.println(curr + " by " + Thread.currentThread().getName());
		File file = files[curr];
		/*try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		PicProcessClient.setCurr(++curr);
		
		return file;
	}
	
	
	private static void stopThread(){
		
		flag = false;
	}
	
	

}
