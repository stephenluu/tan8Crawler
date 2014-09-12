package com.ipuzhe.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RGBTest {

	public static void main(String[] args) throws IOException {
		// TODO
		writeIm();
		readIm();
	}

	static void writeIm() throws IOException {

		File file = new File("C:/1.png");

		BufferedImage image = new BufferedImage(100, 100, 1);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {

				//image.setRGB(i, j, 0xfef9e8);
				//image.setRGB(i, j, 0xfff9e8);
				
				image.setRGB(i, j, 0xfff9e9);
				
				//image.setRGB(i, j, 0xfefae8);
			}
		}

		ImageIO.write(image, "png", file);
	}

	static void readIm() throws IOException {

		File file = new File("C:/1.png");


		// BufferedImage image = new BufferedImage(100, 100, 13) ;
		BufferedImage image = ImageIO.read(file);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {

				int rgb = image.getRGB(i, j);
				String s = Integer.toHexString(rgb);

				//image.setRGB(i, j, -1);
			}
		}

		ImageIO.write(image, "png", file);
	}

}
