package com.ipuzhe.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.ipuzhe.util.ImageUtil;

/**
 * 乐谱底色换成白色  
 * 实现：把6种底色底色替换成白色（-1）
 * @author luliuyu
 *
 */
public class ImageTest {

	public static void main(String[] args) throws InterruptedException {

		File file = new File("C:/18.png");
		BufferedImage image = null;
		
		try {
			 image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int w = image.getWidth();
		int h = image.getHeight();
		int type = image.getType();
		int[] pixs = ImageUtil.getPixels(image); //像素数组
		
		
		for (int i = 0; i < pixs.length; i++) { //对6种颜色替换  	     
			
			if (pixs[i] == -1303 || pixs[i] == -1304 || pixs[i] == -67096) //fffae9(主) fffae8  fef9e8
				pixs[i] = -1;
			
			if (pixs[i] == -1559 ) //fff9e9
				pixs[i] = -1;
			if (pixs[i] == -1560 ) //fff9e8
				pixs[i] = -1;
			if (pixs[i] == -66840 ) //fefae8
				pixs[i] = -1;
		}
		
		BufferedImage newImage = new BufferedImage(w, h, type) ;
		
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				
				newImage.setRGB(i, j,pixs[j*w+i]);
			}
		}
		
		File output = new File("c:/19.png");
		try {
			ImageIO.write(newImage, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
