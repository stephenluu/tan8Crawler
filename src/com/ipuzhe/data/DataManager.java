package com.ipuzhe.data;

import java.util.Queue;
import org.apache.log4j.Logger;

public final class DataManager {

	private static final Logger logger = Logger.getLogger(DataManager.class);

	private Queue<String> data;
	private boolean loaded = false;

	public DataManager(Queue<String> data) {

		this.data = data;
	}

	public void printAll() {
		if (loaded) {
			for (String dataStr : data) {
				System.out.println(dataStr);
			}
		} else {
			logger.warn("[DataManager] DataManager isnot loaded!");
		}
	}

	public Queue<String> getData() {
		return data;
	}

	public boolean hasNext() {
		return !data.isEmpty();
	}

	public String next() {
		return data.poll();
	}

}
