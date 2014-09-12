package com.ipuzhe.test;

public class Temp2Test {

	public static void main(String[] args) {

		//这是主进程
		System.out.println("主线程：" + Thread.currentThread().getName());
		Runnable r = new MyThread();
		Thread t = new Thread(r);
		t.start();
	}

}

class MyThread implements Runnable {

	@Override
	public void run() {
		System.out.println("第2个线程：" + Thread.currentThread().getName());
		
		new Thread() {
			@Override
			public void run() {
				System.out.println("第3个线程：" + Thread.currentThread().getName());
			}
		}.start();

	}
}
