package com.ipuzhe.test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import com.ipuzhe.data.Persistence;

public class DeleteDuplicate {

	private static final String path = "e:/crawler_77";
	
	public static void main(String[] args) throws SQLException {

		List<String> list = Persistence.getNewPicUrls();
		
		File folder = new File (path);
		File[] files = folder.listFiles();
		for (File file : files) {
			
			String str = "/upload/images/musiccase/"+file.toString().substring(14);
			if(!list.contains(str)){
				
				file.delete();
				System.out.println(file+ " is deleted");
			}
			
		}
	}

}
