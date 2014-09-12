package com.ipuzhe.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MusicCaseUtils {

	private static  String OUTPUT_FOLDER = "E:/crawler_77_processed/";
	
	public static String getOUTPUT_FOLDER() {
		return OUTPUT_FOLDER;
	}

	public static void setOUTPUT_FOLDER(String oUTPUT_FOLDER) {
		OUTPUT_FOLDER = oUTPUT_FOLDER;
	}

	/**
	 * 去水印 底色换成白色
	 */
	public static void picProcess(String path) {

		File file = new File(path);
		BufferedImage image = null;

		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int w = 0;
		int h = 0;
		int type = 0;
		try {
			w = image.getWidth();
			h = image.getHeight();
			type = image.getType();
		} catch (NullPointerException e2) {
			System.out.println(path);
			e2.printStackTrace();
		}

		// erase the upper left
		for (int i = 37; i <= 265; i++) {
			for (int j = 23; j <= 40; j++) {
				image.setRGB(i, j, 0xfffae9);
			}
		}
		// erase the lower left
		for (int i = 38; i <= 360; i++) {
			for (int j = h - 46, botm = h - 27; j <= botm; j++) {
				image.setRGB(i, j, 0xfffae9);
			}
		}

		// erase the upper right
		for (int i = w/3*2 , rig = w - 38; i <= rig; i++) {
			for (int j = 23; j <= 40; j++) {
				image.setRGB(i, j, 0xfffae9);
			}
		}

		int[] pixs = null;
		try {
			pixs = ImageUtil.getPixels(image);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} // 像素数组

		for (int i = 0; i < pixs.length; i++) { // 对6种颜色替换

			if (pixs[i] == -1303 || pixs[i] == -1304 || pixs[i] == -67096) // fffae9(主)
																			// fffae8
																			// fef9e8
				pixs[i] = -1;

			if (pixs[i] == -1559) // fff9e9
				pixs[i] = -1;
			if (pixs[i] == -1560) // fff9e8
				pixs[i] = -1;
			if (pixs[i] == -66840) // fefae8
				pixs[i] = -1;
		}

		BufferedImage newImage = new BufferedImage(w, h, type);

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				newImage.setRGB(i, j, pixs[j * w + i]);
			}
		}

		String subname = path.substring(path.lastIndexOf("\\") + 1);
		File output = new File(OUTPUT_FOLDER + subname);
		try {
			ImageIO.write(newImage, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
