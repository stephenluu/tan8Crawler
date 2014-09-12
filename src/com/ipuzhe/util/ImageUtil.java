package com.ipuzhe.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class ImageUtil {
	/**
	 * 获取图片的像素
	 * 
	 * @param bImage
	 * @return 像素数组
	 * @throws InterruptedException 
	 */
	public static int[] getPixels(BufferedImage bImage) throws InterruptedException {
		int iw = bImage.getWidth();
		int ih = bImage.getHeight();
		int[] pixels = new int[iw * ih];
		PixelGrabber pg = new PixelGrabber(bImage.getSource(), 0, 0, iw, ih, pixels, 0, iw);
		pg.grabPixels();
		return pixels;
	}
	
	/** 
	 *缩放 
	 *原理：缩小后的图像某点是对应原图多个点的，所经取多个点的平均值即可 
	 */
	public static BufferedImage scaleSmaller(BufferedImage bImage, double sx, double sy) {
		int H = (int)(bImage.getHeight()*sy);
		int W = (int)(bImage.getWidth()*sx);
		BufferedImage destImage = new   BufferedImage(W,   H,   BufferedImage.TYPE_INT_RGB); 
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < W; j++) {
				int y1 = (int)(i/sy);
				int y2 = (int)((i+1)/sy);
				int x1 = (int)(j/sx);
				int x2 = (int)((j+1)/sx);
				int rgbSumRED=0,rgbSumGREEN=0,rgbSumBLUE=0;
				for(int k = x1; k < x2; k++){                
					for(int l=y1; l<y2; l++){    
						rgbSumRED += new Color(bImage.getRGB(k, l)).getRed();
						rgbSumGREEN += new Color(bImage.getRGB(k, l)).getGreen();
						rgbSumBLUE += new Color(bImage.getRGB(k, l)).getBlue();
					}
				}
				int num = (x2-x1)*(y2-y1);
//				System.out.println((rgbSumRED/num)+"," + ((rgbSumGREEN/num) ) +","+ (rgbSumBLUE/num));
				int rgb = ((rgbSumRED/num)<< 16) + ((rgbSumGREEN/num) << 8) + (rgbSumBLUE/num);
				destImage.setRGB(j, i, rgb);
			}
		}
		
		return destImage;
	}
	
	
	/** 
	 *缩放 
	 *原理：缩小后的图像某点是对应原图多个点的，所经取多个点的平均值即可 
	 */
	public static BufferedImage scaleSmaller2(BufferedImage bImage, double sx, double sy) {
		int H = (int)(bImage.getHeight()*sy);
		int W = (int)(bImage.getWidth()*sx);
		BufferedImage destImage = new   BufferedImage(W,   H,   BufferedImage.TYPE_INT_RGB); 
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < W; j++) {
				int y1 = (int)(i/sy);
				int x1 = (int)(j/sx);
				destImage.setRGB(j, i, bImage.getRGB(x1, y1));
			}
		}
		
		return destImage;
	}
	
	
	/** 平滑缩放 */
	public static BufferedImage scaling(double s, BufferedImage bImage) {

		AffineTransform tx = new AffineTransform();
		tx.scale(s, s);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
		return op.filter(bImage, null);
	}
	
	/** 旋转 */
	public static BufferedImage rotate(double radian, BufferedImage bufferedImage) {
		AffineTransform tx = new AffineTransform();
		tx.rotate(radian);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(bufferedImage, null);
		
		
//		  int   width   =   bufferedImage.getWidth(); 
//	                int   height   =   bufferedImage.getHeight(); 
//
//	                BufferedImage   dstImage   =   null; 
//	                AffineTransform   affineTransform   =   new   AffineTransform(); 
//
//	                if   (radian   ==   180)   { 
//	                        affineTransform.translate(width,   height); 
//	                        dstImage   =   new   BufferedImage(width,   height,   bufferedImage.getType()); 
//	                }   else   if   (radian   ==   90)   { 
//	                        affineTransform.translate(height,   0); 
//	                        dstImage   =   new   BufferedImage(height,   width,   bufferedImage.getType()); 
//	                }   else   if   (radian   ==   270)   { 
//	                        affineTransform.translate(0,   width); 
//	                        dstImage   =   new   BufferedImage(height,   width,   bufferedImage.getType()); 
//	                } 
//
//	                affineTransform.rotate(java.lang.Math.toRadians(radian)); 
//	                AffineTransformOp   affineTransformOp   =   new   AffineTransformOp( 
//	                                affineTransform, 
//	                                hints); 
//
//	                return   affineTransformOp.filter(bufferedImage,   dstImage); 
//		
		
	}


	public static BufferedImage imageToBufferedImage(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		return bufferedImage;
	}

	public static BufferedImage imageProducerToBufferedImage(ImageProducer imageProducer) {
		return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(imageProducer));
	}

	public static boolean isBlack(int colorInt) {
		Color color = new Color(colorInt);
//		System.out.println(color.getRed()+","+color.getGreen()+","+color.getBlue());
		return (color.getRed() + color.getGreen() + color.getBlue() < 1);
	}

	public static boolean isWhite(int colorInt) {
		Color color = new Color(colorInt);
		return (color.getRed() + color.getGreen() + color.getBlue() > 764);
	}
	public static boolean isRed(int colorInt) {
		Color color = new Color(colorInt);
		return (color.getRed() == 255 && color.getGreen()==0 &&  color.getBlue() == 0);
	}

	public static boolean compareImage(BufferedImage image1, BufferedImage image2) {
		// System.out.println(image1.getWidth()+", " + image1.getHeight() + " ===" + image2.getWidth()+", " + image2.getHeight() );
		if (image1.getWidth() == image2.getWidth() && image1.getHeight() == image2.getHeight()) {
			for (int i = 0; i < image1.getWidth(); i++) {
				for (int j = 0; j < image1.getHeight(); j++) {
					// System.out.println(image1.getRGB(i, j) + ",,,,," + image2.getRGB(i, j));
					Color color = new Color(image1.getRGB(i, j));
					// System.out.println("1:"+color.getRed() +","+ color.getGreen() +","+ color.getBlue());
					color = new Color(image2.getRGB(i, j));
					// System.out.println("2:"+color.getRed() +","+ color.getGreen() +","+ color.getBlue());

					if (image1.getRGB(i, j) != image2.getRGB(i, j)) {
						return false;
					}
				}
			}
			// System.out.println("相同......");
			return true;
		}
		return false;
	}

	/**
	 * 将图像灰度化及二值化
	 * grayAndBinary
	 * @param bImage
	 * @return
	 */
	public static BufferedImage grayAndBinary(BufferedImage bImage) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		int area = width * height;
		int gray[][] = new int[width][height];
		int u = 0;// 灰度平均值
		int graysum = 0;
		int graymean = 0;
		int grayfrontmean = 0;
		int graybackmean = 0;
		Color color;
		int pixl[][] = new int[width][height];
		int pixelsR;
		int pixelsG;
		int pixelsB;
		int pixelGray;
		int front = 0;
		int back = 0;
		for (int i = 0; i < width; i++) { // 不算边界行和列，为避免越界
			for (int j = 0; j < height; j++) {
				pixl[i][j] = bImage.getRGB(i, j);
				color = new Color(pixl[i][j]);
				pixelsR = color.getRed();// R空间
				pixelsB = color.getBlue();// G空间
				pixelsG = color.getGreen();// B空间
				pixelGray = (int) (0.3 * pixelsR + 0.59 * pixelsG + 0.11 * pixelsB);// 计算每个坐标点的灰度
				gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
				graysum += pixelGray;
			}

		}
		graymean = (int) (graysum / area);// 整个图的灰度平均值
		u = graymean;
//		System.out.println(u);

		for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
		{
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < graymean) {
					graybackmean += ((gray[i][j]) & (0x0000ff));
					back++;
				} else {
					grayfrontmean += ((gray[i][j]) & (0x0000ff));
					front++;
				}
			}
		}
		int frontvalue = (int) (grayfrontmean / front);// 前景中心
		int backvalue = (int) (graybackmean / back);// 背景中心
		float G[] = new float[frontvalue - backvalue + 1];// 方差数组
		int s = 0;
//		System.out.println(front);
//		System.out.println(frontvalue);
//		System.out.println(backvalue);
		for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法
		{
			back = 0;
			front = 0;
			grayfrontmean = 0;
			graybackmean = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
						graybackmean += ((gray[i][j]) & (0x0000ff));
						back++;
					} else {
						grayfrontmean += ((gray[i][j]) & (0x0000ff));
						front++;

					}
				}
			}
			grayfrontmean = (int) (grayfrontmean / front);
			graybackmean = (int) (graybackmean / back);

			G[s] = (((float) back / area) * (graybackmean - u) * (graybackmean - u) + ((float) front / area) * (grayfrontmean - u) * (grayfrontmean - u));
			s++;
		}
		float max = G[0];
		int index = 0;
		for (int i = 1; i < frontvalue - backvalue + 1; i++) {
			if (max < G[i]) {
				max = G[i];
				index = i;
			}
		}
		// System.out.println(G[index]);
		// System.out.println(index);
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
					bImage.setRGB(i, j, 0x000000);
//					System.out.print("1");
				} else {
					bImage.setRGB(i, j, 0xffffff);
//					System.out.print("0");
				}
			}
//			System.out.println();
		}
		return bImage;
	}
	
	/**
	 * 将图像灰度化及二值化
	 * grayAndBinary
	 * @param bImage
	 * @return
	 */
	public static int[][] toBinaryArray(BufferedImage bImage) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		int area = width * height;
		int gray[][] = new int[width][height];
		int u = 0;// 灰度平均值
		int graysum = 0;
		int graymean = 0;
		int grayfrontmean = 0;
		int graybackmean = 0;
		Color color;
		int pixl[][] = new int[width][height];
		int pixelsR;
		int pixelsG;
		int pixelsB;
		int pixelGray;
		int front = 0;
		int back = 0;
		for (int i = 0; i < width; i++) { // 不算边界行和列，为避免越界
			for (int j = 0; j < height; j++) {
				pixl[i][j] = bImage.getRGB(i, j);
				color = new Color(pixl[i][j]);
				pixelsR = color.getRed();// R空间
				pixelsB = color.getBlue();// G空间
				pixelsG = color.getGreen();// B空间
				pixelGray = (int) (0.3 * pixelsR + 0.59 * pixelsG + 0.11 * pixelsB);// 计算每个坐标点的灰度
				gray[i][j] = (pixelGray << 16) + (pixelGray << 8) + (pixelGray);
				graysum += pixelGray;
			}
			
		}
		graymean = (int) (graysum / area);// 整个图的灰度平均值
		u = graymean;
//		System.out.println(u);
		
		for (int i = 0; i < width; i++) // 计算整个图的二值化阈值
		{
			for (int j = 0; j < height; j++) {
				if (((gray[i][j]) & (0x0000ff)) < graymean) {
					graybackmean += ((gray[i][j]) & (0x0000ff));
					back++;
				} else {
					grayfrontmean += ((gray[i][j]) & (0x0000ff));
					front++;
				}
			}
		}
		int frontvalue = (int) (grayfrontmean / front);// 前景中心
		int backvalue = (int) (graybackmean / back);// 背景中心
		float G[] = new float[frontvalue - backvalue + 1];// 方差数组
		int s = 0;
//		System.out.println(front);
//		System.out.println(frontvalue);
//		System.out.println(backvalue);
		for (int i1 = backvalue; i1 < frontvalue + 1; i1++)// 以前景中心和背景中心为区间采用大津法算法
		{
			back = 0;
			front = 0;
			grayfrontmean = 0;
			graybackmean = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (((gray[i][j]) & (0x0000ff)) < (i1 + 1)) {
						graybackmean += ((gray[i][j]) & (0x0000ff));
						back++;
					} else {
						grayfrontmean += ((gray[i][j]) & (0x0000ff));
						front++;
						
					}
				}
			}
			grayfrontmean = (int) (grayfrontmean / front);
			graybackmean = (int) (graybackmean / back);
			
			G[s] = (((float) back / area) * (graybackmean - u) * (graybackmean - u) + ((float) front / area) * (grayfrontmean - u) * (grayfrontmean - u));
			s++;
		}
		float max = G[0];
		int index = 0;
		for (int i = 1; i < frontvalue - backvalue + 1; i++) {
			if (max < G[i]) {
				max = G[i];
				index = i;
			}
		}
		// System.out.println(G[index]);
		// System.out.println(index);
		int result[][] = new int[height][width];
//		int count = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (((gray[i][j]) & (0x0000ff)) < (index + backvalue)) {
//					bImage.setRGB(i, j, 0x000000);
//					System.out.print("1");
					result[j][i] = 1;
//					count++;
				} else {
//					bImage.setRGB(i, j, 0xffffff);
//					System.out.print("0");
					result[j][i] = 0;
				}
			}
//			System.out.println();
		}
//		System.out.println("黑色像素"+count +"个.");
		return result;
	}
	
/*	*//**
	 * 边界检测
	 * 	从图片左上
		角开始扫描，由上到下，由左至右找到第1 个黑色像素点，
		设置为外部边界的开始点，使用约定符号(0, 1, 2, 3, 4, 5, 6, 7)
		对该点的8 个相邻像素点进行标号。用2表示该边界像素点已经被搜索。
		从外边界开始点开始搜索，先找到标号的8 个相邻像素点中一个没有被搜索的黑色
		像素点，并且该像素点的相邻一个像素点为白色，即为下一
		个开始搜索的轮廓像素点，将该点的标记信息设置为3，从
		该点开始接着找相邻的未搜索过的边界像素点。以此类推可
		以得到一个封闭的回路，直到搜索到第一个被标记的边界像
		素点。便得到了4 个字符的整个外部边界。
		i=0,1,2
		j=0□□□
		j=1□■□
		j=2□□□
	 * edgeDetect
	 * @param sourceArray
	 * @return
	 *//*
	public static int[][] edgeDetect(int[][] sourceArray, int startx, int starty){
		if (startx == -1 && starty == -1) {
			//高度
			for (int i = 0; i < sourceArray.length; i++) {
				//宽度
				for (int j = 0; j < sourceArray[i].length; j++) {
					//如果搜索到了开始点, 就结束
					if (-1 == sourceArray[i][j]) {
						return sourceArray;
					}
					//黑色像素点
					if (1 == sourceArray[i][j]) {
						startx = j;
						starty = i;
						sourceArray[i][j] = -1;
						return edgeDetect(sourceArray, startx, starty);
					}
				}
			}
		}
		
		//返回到起点了
//		if (sourceArray[starty][startx] == -1) {
//			return sourceArray;
//		}
		
		//以当前点右边第一个点为起点，逆时针搜索当前点的相邻8个点
		//先找到标号的8个相邻像素点中一个没有被搜索的黑色像素点，并且该像素点的相邻一个像素点为白色，
		//即为下一个开始搜索的轮廓像素点，将该点的标记信息设置为3，从
		//该点开始接着找相邻的未搜索过的边界像素点。
		//当前点为sourceArray[starty][startx]
		//右sourceArray[starty][startx+1]
		if (1 == sourceArray[starty][startx+1] && 3 != sourceArray[starty][startx+1] && -1 != sourceArray[starty][startx+1]) {
			if (0 == sourceArray[starty][startx+2] || 0 == sourceArray[starty-1][startx+1] || 0 == sourceArray[starty+1][startx+1]) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty][startx+1] = 3;
				return edgeDetect(sourceArray, startx+1, starty);
			}
		}
		//右上sourceArray[starty-1][startx+1]
		if (1 == sourceArray[starty-1][startx+1] && 3 != sourceArray[starty-1][startx+1] && -1 != sourceArray[starty-1][startx+1]) {
			if (0 == sourceArray[starty-2][startx+1] || 0 == sourceArray[starty][startx+1]  || 0 == sourceArray[starty-1][startx] ||0 == sourceArray[starty-1][startx+2] ) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty-1][startx+1] = 3;
				return edgeDetect(sourceArray, startx+1, starty-1);
			}
		}
		//上sourceArray[starty-1][startx]
		if (1 == sourceArray[starty-1][startx] && 3 != sourceArray[starty-1][startx] && -1 != sourceArray[starty-1][startx]) {
			if (0 == sourceArray[starty-1][startx-1] || 0 == sourceArray[starty-2][startx] || 0 == sourceArray[starty-1][startx+1]) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty-1][startx] = 3;
				return edgeDetect(sourceArray, startx, starty-1);
			}
		}
		//左上sourceArray[starty-1][startx-1]
		if (1 == sourceArray[starty-1][startx-1] && 3 != sourceArray[starty-1][startx-1] && -1 != sourceArray[starty-1][startx-1]) {
			if (0 == sourceArray[starty-2][startx-1] ||0 == sourceArray[starty][startx-1] ||0 == sourceArray[starty-1][startx-2] ||0 == sourceArray[starty-1][startx] ) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty-1][startx-1] = 3;
				return edgeDetect(sourceArray, startx -1, starty-1);
			}
		}
		
		//左下sourceArray[starty+1][startx-1]
		if (1 == sourceArray[starty+1][startx-1] && 3 != sourceArray[starty+1][startx-1] && -1 != sourceArray[starty+1][startx-1]) {
			if (0 == sourceArray[starty+2][startx-1] || 0 == sourceArray[starty][startx-1] || 0 == sourceArray[starty+1][startx] ||0 == sourceArray[starty+1][startx-2]) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty+1][startx-1] = 3;
				return edgeDetect(sourceArray, startx-1, starty+1);
			}
		}
		//下sourceArray[starty+1][startx]
		if (1 == sourceArray[starty+1][startx] && 3 != sourceArray[starty+1][startx]&& -1 != sourceArray[starty+1][startx]) {
			if (0 == sourceArray[starty+2][startx] || 0 == sourceArray[starty+1][startx - 1] || 0 == sourceArray[starty+1][startx+1] ) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty+1][startx] = 3;
				return edgeDetect(sourceArray, startx, starty+1);
			}
		}
		//右下sourceArray[starty+1][startx+1]
		if (1 == sourceArray[starty+1][startx+1] && 3 !=  sourceArray[starty+1][startx+1] && -1 !=  sourceArray[starty+1][startx+1]) {
			if (0 == sourceArray[starty+2][startx+1] || 0 == sourceArray[starty][startx+1] ||0 == sourceArray[starty+1][startx+2] ||0 == sourceArray[starty+1][startx]) {
				//该像素点为边界,并为下一开始点
				sourceArray[starty+1][startx+1] = 3;
				return edgeDetect(sourceArray, startx+1, starty+1);
			}
		}
		return sourceArray;
	}
	
	*//**
	 * 边界检测
	 * 	从图片左上
		角开始扫描，由上到下，由左至右找到第1 个黑色像素点，
		设置为外部边界的开始点，使用约定符号(0, 1, 2, 3, 4, 5, 6, 7)
		对该点的8 个相邻像素点进行标号。用2表示该边界像素点已经被搜索。
		从外边界开始点开始搜索，先找到标号的8 个相邻像素点中一个没有被搜索的黑色
		像素点，并且该像素点的相邻一个像素点为白色，即为下一
		个开始搜索的轮廓像素点，将该点的标记信息设置为3，从
		该点开始接着找相邻的未搜索过的边界像素点。以此类推可
		以得到一个封闭的回路，直到搜索到第一个被标记的边界像
		素点。便得到了4 个字符的整个外部边界。
		i=0,1,2
		j=0□□□
		j=1□■□
		j=2□□□
	 * edgeDetect
	 * @param sourceArray
	 * @return
	 *//*
	public static int[][] edgeDetect2(int[][] sourceArray) {
		// 高度
		for (int i = 0; i < sourceArray.length; i++) {
			// 宽度
			for (int j = 0; j < sourceArray[i].length; j++) {
				// 黑色像素点
				if (1 == sourceArray[i][j]) {
					// 先判断上下左右是否有白色点,如果有刚表示当前点是边界
					// 右sourceArray[starty][startx+1]
					if (1 == sourceArray[i][j + 1] && 3 != sourceArray[i][j + 1] && -1 != sourceArray[i][j + 1]) {
						if (0 == sourceArray[i][j + 2] || 0 == sourceArray[i - 1][j + 1] || 0 == sourceArray[i + 1][j + 1]) {
							// 该像素点为边界,并为下一开始点
							sourceArray[i][j + 1] = 3;
						}
					}
					// 上sourceArray[i-1][j]
					if (1 == sourceArray[i - 1][j] && 3 != sourceArray[i - 1][j] && -1 != sourceArray[i - 1][j]) {
						if (0 == sourceArray[i - 1][j - 1] || 0 == sourceArray[i - 2][j] || 0 == sourceArray[i - 1][j + 1]) {
							// 该像素点为边界,并为下一开始点
							sourceArray[i - 1][j] = 3;
						}
					}
					// 左sourceArray[i][j-1]
					if (1 == sourceArray[i][j - 1] && 3 != sourceArray[i][j - 1] && -1 != sourceArray[i][j - 1]) {
						if (0 == sourceArray[i - 1][j - 1] || 0 == sourceArray[i + 1][j - 1] || 0 == sourceArray[i][j - 2]) {
							// 该像素点为边界,并为下一开始点
							sourceArray[i][j - 1] = 3;
						}
					}
					// 下sourceArray[i+1][j]
					if (1 == sourceArray[i + 1][j] && 3 != sourceArray[i + 1][j] && -1 != sourceArray[i + 1][j]) {
						if (0 == sourceArray[i + 2][j] || 0 == sourceArray[i + 1][j - 1] || 0 == sourceArray[i + 1][j + 1]) {
							// 该像素点为边界,并为下一开始点
							sourceArray[i + 1][j] = 3;
						}
					}
				}
			}
		}
		return sourceArray;
	}*/
	
	/**
	 * 如果当前点(目标黑色点)的上下左右其中一个点为白色,则当前点为边界点
	 * 未考虑越界问题
	 * edgeDetect3
	 * @param sourceArray 图像二值化后的矩阵, 0为背景, 1为前景
	 * @return
	 */
	public static int[][] edgeDetectArray(int[][] sourceArray) {
		int count = 0;
		// 高度
		for (int i = 0; i < sourceArray.length; i++) {
			// 宽度
			for (int j = 0; j < sourceArray[i].length; j++) {
				// 黑色像素点
				if (1 == sourceArray[i][j]) {
					// 先判断上下左右是否有白色点,如果有刚表示当前点是边界
					if (0 == sourceArray[i - 1][j] || 0 == sourceArray[i+1][j] || 0 == sourceArray[i][j-1] || 0 == sourceArray[i][j + 1]){
						sourceArray[i][j] = 3;
						count++;
					}
				}
			}
		}
		System.out.println("边界点共有"+count+"个.");
		return sourceArray;
	}
	
	public static BufferedImage edgeDetectImg(BufferedImage bImage) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// 高度
		for (int i = 0; i < height; i++) {
			// 宽度
			for (int j = 0; j < width; j++) {
				// 黑色像素点
				if (isBlack(bImage.getRGB(j, i))) {
					//当前是否边界
					if (0 == i || 0 == j || width == (j +1) ||  height == (i + 1)) {
						bImage.setRGB(j, i, 0xff0000);
						continue;
					}
					// 先判断上下左右是否有白色点,如果有刚表示当前点是边界
					if(isWhite(bImage.getRGB(j, i - 1)) || isWhite(bImage.getRGB(j, i+1)) || isWhite(bImage.getRGB(j-1, i)) || isWhite(bImage.getRGB(j+1, i ))){
						System.out.println("红。。。"+i+","+j);
						bImage.setRGB(j, i, 0xff0000);
					}
				}
			}
		}
		// 高度
		for (int i = 0; i < height; i++) {
			// 宽度
			for (int j = 0; j < width; j++) {
				// 刚才标记的红色边缘点
				if (isRed(bImage.getRGB(j, i))) {
						bImage.setRGB(j, i, 0x000000);
				}else {
					bImage.setRGB(j, i, 0xffffff);
				}
			}
		}
		return bImage;
	}
	
	/**
	 * 获取连通像素<br>
	 * 从第一个像素开始遍历，找到一个黑色像素作一个标记，并从左往右、从上往下遍历，把黑色标记，直到八相邻点都被标记为止结束
	 * getConnectedImg
	 * @param bImage
	 * @return
	 */
	public static BufferedImage getConnectedPoint(BufferedImage bImage) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		int x = -1, y = -1;
		// 宽度
		for (int j = 0; j < width && x == -1 && y == -1; j++) {
		// 高度
		for (int i = 0; i < height; i++) {
				// 黑色像素点
				if (isBlack(bImage.getRGB(j, i))) {
					x = j;
					y = i;
				}
			}
		}
		//已经找到了第一个点
		if(x != -1 && y != -1){
			bImage = connect(bImage, x, y);
		}
		
		return bImage;
	}
	
	public static BufferedImage connect(BufferedImage bImage, int x, int y) {
		bImage.setRGB(x, y, 0xff0000);
		if (isBlack(bImage.getRGB((x == 0 ? 0 : x -1) , (y == 0 ? 0 : y -1)))) {//左上
			x = (x == 0 ? 0 : x -1);
			y = (y == 0 ? 0 : y -1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB(x , (y == 0 ? 0 : y -1)))) {//上
			y = (y == 0 ? 0 : y -1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB((x == bImage.getWidth()-1?bImage.getWidth()-1: x+1) , (y == 0 ? 0 : y -1)))) {//右上
			x = (x == bImage.getWidth()-1?bImage.getWidth()-1: x+1) ;
			y = (y == 0 ? 0 : y -1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB((x == 0 ? 0 : x -1) , y))) {
			x = (x == 0 ? 0 : x -1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB((x == bImage.getWidth()-1?bImage.getWidth()-1: x+1) , y))) {
			x = (x == bImage.getWidth()-1?bImage.getWidth()-1: x+1) ;
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB((x == 0 ? 0 : x -1) , (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1) ))) {
			x = (x == 0 ? 0 : x -1);
			y = (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB(x , (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1)))) {
			y = (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1);
			connect(bImage, x, y);
		}
		if (isBlack(bImage.getRGB((x == bImage.getWidth()-1?bImage.getWidth()-1: x+1)  , (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1)))) {
			x = (x == bImage.getWidth()-1?bImage.getWidth()-1: x+1) ;
			y = (y == bImage.getHeight()-1?bImage.getHeight()-1: y+1);
			connect(bImage, x, y);
		}
		return bImage;
	}
	
	/**
	 * 半圆极坐取“样本”特征，注意：图片上下左右不能有空
	 * 从左往单像素左移动，对目标图片每个点进行取样、对比
	 *                  |90
	 *                  | 
	 *                  |
	 *                  |
	 *180 ---------------------0
	 *                  | 
	 *                  | 
	 *                  | -90
	 * 
	 * getSampleFeatures
	 * @param bImage
	 * @return
	 */
	public static Map<Integer[], Double[]> getSampleFeatures(BufferedImage bImage, int r) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// 宽度的一半作为极坐标圆的半径
//		int r = (int) (width / 2.0);
//		System.out.println("极坐标圆半径: " + r);
		Map<Integer[], Double[]> features = new HashMap<Integer[], Double[]>();//<[x,y], [,,,,,,,,,]>
		// 宽度
		for (int j = 0; j < width; j++) {
			// 高度
			for (int i = 0; i < height; i++) {
				// 有效点(黑色点)
				if (isBlack(bImage.getRGB(j, i))) {
					int r1 = (int) (r / 3.0 + 0.5);
					int r2 = (int) (r / 3.0 * 2 + 0.5);
					int r3 = r;
					// 当前的(j, i)为圆心,r为半径, 获取极坐标圆内的特征点(把圆分成三个同心圆、45度角切成24等份)
					
					//左边字符编码右半圆[0,90),[-90,0)
					if (j < width/2) {
						Double[] count1 = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
						// 宽度
						int edgeW =  j + r > width ? width : j + r;
						int edgeH =  i + r > height ? height : i + r;
						for (int w = j; w < edgeW; w++) {
							// 高度
							for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
								double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
								// System.out.println("R="+R);
								if (R <= r && R > 0) {//R=0是圆心
									if (isBlack(bImage.getRGB(w, h))) {
										//TODO
										double coordinatorY = i - h;
										double coordinatorX = w - j;
										
										double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
										if (theater >= -90 && theater < -45) {
											if (R <= r1) {
												count1[0]++;
											} else if (R <= r2) {
												count1[1]++;
											} else if (R <= r3) {
												count1[2]++;
											}
										} else if (theater >= -45 && theater < 0) {
											if (R <= r1) {
												count1[3]++;
											} else if (R <= r2) {
												count1[4]++;
											} else if (R <= r3) {
												count1[5]++;
											}
										} else if (theater >= 0 && theater < 45) {
											if (R <= r1) {
												count1[6]++;
											} else if (R <= r2) {
												count1[7]++;
											} else if (R <= r3) {
												count1[8]++;
											}
										} else if (theater >= 45 && theater <= 90) {
											if (R <= r1) {
												count1[9]++;
											} else if (R <= r2) {
												count1[10]++;
											} else if (R <= r3) {
												count1[11]++;
											}
										}
									}
								}
								
							}
						}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count1.length; h++) {
//							System.out.print(count1[h]+"\t");
//						}
//						System.out.println();
						features.put(new Integer[]{j,i}, count1);
					}
					//右边字符编码左半圆[90,180)，[-180,-90)
					else {
						Double[] count2 = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
						// 宽度
						int edgeH =  i + r > height ? height : i + r;
						for (int w = (j - r < 0 ? 0 : j -r); w < j; w++) {
							// 高度
							for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
								double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
								// System.out.println("R="+R);
								if (R <= r && R > 0) {//R=0是圆心
									if (isBlack(bImage.getRGB(w, h))) {
										//TODO
										double coordinatorY = i - h;
										double coordinatorX = w - j;
										
										double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
										if (theater > 90 && theater < 135) {
											if (R <= r1) {
												count2[0]++;
											} else if (R <= r2) {
												count2[1]++;
											} else if (R <= r3) {
												count2[2]++;
											}
										} else if (theater >= 135 && theater < 180) {
											if (R <= r1) {
												count2[3]++;
											} else if (R <= r2) {
												count2[4]++;
											} else if (R <= r3) {
												count2[5]++;
											}
										} else if (theater >= -180 && theater < -135) {
											if (R <= r1) {
												count2[6]++;
											} else if (R <= r2) {
												count2[7]++;
											} else if (R <= r3) {
												count2[8]++;
											}
										} else if (theater >= -135 && theater < -90) {
											if (R <= r1) {
												count2[9]++;
											} else if (R <= r2) {
												count2[10]++;
											} else if (R <= r3) {
												count2[11]++;
											}
										}
									}
								}
								
							}
						}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count2.length; h++) {
//							System.out.print(count2[h]+"\t");
//						}
//						System.out.println();
						features.put(new Integer[]{j,i}, count2);
					}					
				}
			}
		}
		return features;
	}
	
	
	/**
	 * 全圆极坐取“样本”特征，注意：图片上下左右不能有空
	 * 从左往单像素左移动，对目标图片每个点进行取样、对比
	 *                  |90
	 *                  | 
	 *                  |
	 *                  |
	 *180 ---------------------0
	 *                  | 
	 *                  | 
	 *                  | -90
	 * 
	 * getSampleFeatures
	 * @param bImage
	 * @return
	 */
	public static Map<Integer[], Double[]> getSampleFeatures2(BufferedImage bImage, int r) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// 宽度的一半作为极坐标圆的半径
//		int r = (int) (width / 2.0);
//		System.out.println("极坐标圆半径: " + r);
		Map<Integer[], Double[]> features = new HashMap<Integer[], Double[]>();//<[x,y], [,,,,,,,,,]>
		// 宽度
		for (int j = 0; j < width; j++) {
			// 高度
			for (int i = 0; i < height; i++) {
				// 有效点(黑色点)
				if (isBlack(bImage.getRGB(j, i))) {
					int r1 = (int) (r / 3.0 + 0.5);
					int r2 = (int) (r / 3.0 * 2 + 0.5);
					int r3 = r;
					// 当前的(j, i)为圆心,r为半径, 获取极坐标圆内的特征点(把圆分成三个同心圆、45度角切成24等份)
					Double[] count = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
					// 宽度
					int edgeW =  j + r > width ? width : j + r;
					int edgeH =  i + r > height ? height : i + r;
					for (int w = (j - r < 0 ? 0 : j -r) ; w < edgeW; w++) {
						// 高度
						for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
							double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
							// System.out.println("R="+R);
							if (R <= r && R > 0) {//R=0是圆心
								if (isBlack(bImage.getRGB(w, h))) {
									//TODO
									double coordinatorY = i - h;
									double coordinatorX = w - j;
									
									double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
									if (theater >= -90 && theater < -45) {
										if (R <= r1) {
											count[0]++;
										} else if (R <= r2) {
											count[1]++;
										} else if (R <= r3) {
											count[2]++;
										}
									} else if (theater >= -45 && theater < 0) {
										if (R <= r1) {
											count[3]++;
										} else if (R <= r2) {
											count[4]++;
										} else if (R <= r3) {
											count[5]++;
										}
									} else if (theater >= 0 && theater < 45) {
										if (R <= r1) {
											count[6]++;
										} else if (R <= r2) {
											count[7]++;
										} else if (R <= r3) {
											count[8]++;
										}
									} else if (theater >= 45 && theater <= 90) {
										if (R <= r1) {
											count[9]++;
										} else if (R <= r2) {
											count[10]++;
										} else if (R <= r3) {
											count[11]++;
										}
									}else if (theater > 90 && theater < 135) {
										if (R <= r1) {
											count[12]++;
										} else if (R <= r2) {
											count[13]++;
										} else if (R <= r3) {
											count[14]++;
										}
									} else if (theater >= 135 && theater < 180) {
										if (R <= r1) {
											count[15]++;
										} else if (R <= r2) {
											count[16]++;
										} else if (R <= r3) {
											count[17]++;
										}
									} else if (theater >= -180 && theater < -135) {
										if (R <= r1) {
											count[18]++;
										} else if (R <= r2) {
											count[19]++;
										} else if (R <= r3) {
											count[20]++;
										}
									} else if (theater >= -135 && theater < -90) {
										if (R <= r1) {
											count[21]++;
										} else if (R <= r2) {
											count[22]++;
										} else if (R <= r3) {
											count[23]++;
										}
									}
								}
							}
							
						}
					}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count.length; h++) {
//							System.out.print(count[h]+"\t");
//						}
//						System.out.println();
					features.put(new Integer[]{j,i}, count);
				}
			}
		}
		return features;
	}
	
	
	/**
	 * 	 * 极坐圆取特征，注意：如果是样本，图片上下左右不能有空
	 匹配目标图片时，以样本的宽度为窗口，从左往单像素左移动，对目标图片每个点进行取样、对比
	 *                  |-90
	 *                  | 
	 *                  |
	 *                  |
	 * -----------------------------------0
	 *                  | 
	 *                  | 
	 *                  | 90
	 * 
	 * getFeatures
	 * @param bImage
	 * @param x 水平方向坐标，即从左到右
	 * @param y 垂直方向坐标，即从上到下
	 * @param r 以样本宽度一半的半径长度
	 * @return
	 */
	public static Map<Integer[], Double[]> getFeatures(BufferedImage bImage, int x, int y, int r) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// 宽度的一半作为极坐标圆的半径
//		int r = (int) (width / 2.0 + 0.5);
//		System.out.println("极坐标圆半径: " + r);
		Map<Integer[], Double[]> features = new HashMap<Integer[], Double[]>();//<[x,y], [,,,,,,,,,]>
		// 宽度
		int scanWidth = (x + 2*r>width? width:x + 2*r);
		for (int j = x; j < scanWidth; j++) {
			// 高度
			for (int i = 0; i < height; i++) {
				
				// 有效点(黑色点)
				if (isBlack(bImage.getRGB(j, i))) {
					int r1 = (int) (r / 3.0 + 0.5);
					int r2 = (int) (r / 3.0 * 2 + 0.5);
					int r3 = r;
					// 当前的(j, i)为圆心,r为半径, 获取极坐标圆内的特征点(把圆分成三个同心圆、45度角切成24等份)
					
					//左边字符编码右半圆[0,90),[-90,0)
					if (j < x + r) {
						Double[] count1 = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
						// 宽度
						int edgeW =  j + r > width ? width : j + r;
						int edgeH =  i + r > height ? height : i + r;
						for (int w = j; w < edgeW; w++) {
							// 高度
							for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
								double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
								// System.out.println("R="+R);
								if (R <= r && R > 0) {//R=0是圆心
									if (isBlack(bImage.getRGB(w, h))) {
										//TODO
										double coordinatorY = i - h;
										double coordinatorX = w - j;
										
										double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
										if (theater >= -90 && theater < -45) {
											if (R <= r1) {
												count1[0]++;
											} else if (R <= r2) {
												count1[1]++;
											} else if (R <= r3) {
												count1[2]++;
											}
										} else if (theater >= -45 && theater < 0) {
											if (R <= r1) {
												count1[3]++;
											} else if (R <= r2) {
												count1[4]++;
											} else if (R <= r3) {
												count1[5]++;
											}
										} else if (theater >= 0 && theater < 45) {
											if (R <= r1) {
												count1[6]++;
											} else if (R <= r2) {
												count1[7]++;
											} else if (R <= r3) {
												count1[8]++;
											}
										} else if (theater >= 45 && theater <= 90) {
											if (R <= r1) {
												count1[9]++;
											} else if (R <= r2) {
												count1[10]++;
											} else if (R <= r3) {
												count1[11]++;
											}
										}
									}
								}
								
							}
						}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count1.length; h++) {
//							System.out.print(count1[h]+"\t");
//						}
//						System.out.println();
						features.put(new Integer[]{j,i}, count1);
					}
					//右边字符编码左半圆[90,180)，[-180,-90)
					else {
						Double[] count2 = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
						// 宽度
						int edgeH =  i + r > height ? height : i + r;
						for (int w = (j - r < 0 ? 0 : j -r); w < j; w++) {
							// 高度
							for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
								double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
								// System.out.println("R="+R);
								if (R <= r && R > 0) {//R=0是圆心
									if (isBlack(bImage.getRGB(w, h))) {
										//TODO
										double coordinatorY = i - h;
										double coordinatorX = w - j;
										
										double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
										if (theater > 90 && theater < 135) {
											if (R <= r1) {
												count2[0]++;
											} else if (R <= r2) {
												count2[1]++;
											} else if (R <= r3) {
												count2[2]++;
											}
										} else if (theater >= 135 && theater < 180) {
											if (R <= r1) {
												count2[3]++;
											} else if (R <= r2) {
												count2[4]++;
											} else if (R <= r3) {
												count2[5]++;
											}
										} else if (theater >= -180 && theater < -135) {
											if (R <= r1) {
												count2[6]++;
											} else if (R <= r2) {
												count2[7]++;
											} else if (R <= r3) {
												count2[8]++;
											}
										} else if (theater >= -135 && theater < -90) {
											if (R <= r1) {
												count2[9]++;
											} else if (R <= r2) {
												count2[10]++;
											} else if (R <= r3) {
												count2[11]++;
											}
										}
									}
								}
								
							}
						}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count2.length; h++) {
//							System.out.print(count2[h]+"\t");
//						}
//						System.out.println();
						features.put(new Integer[]{j,i}, count2);
					}					
				}
			}
		}
		return features;
	}
	
	
	
	/**
	 * 全圆极坐圆取特征，注意：如果是样本，图片上下左右不能有空
	 匹配目标图片时，以样本的宽度为窗口，从左往单像素左移动，对目标图片每个点进行取样、对比
	 *                  |-90
	 *                  | 
	 *                  |
	 *                  |
	 * -----------------------------------0
	 *                  | 
	 *                  | 
	 *                  | 90
	 * 
	 * getFeatures
	 * @param bImage
	 * @param x 水平方向坐标，即从左到右
	 * @param y 垂直方向坐标，即从上到下
	 * @param r 以样本宽度一半的半径长度
	 * @return
	 */
	public static Map<Integer[], Double[]> getFeatures2(BufferedImage bImage, int x, int y, int r) {
		int width = bImage.getWidth();
		int height = bImage.getHeight();
		// 宽度的一半作为极坐标圆的半径
//		int r = (int) (width / 2.0 + 0.5);
//		System.out.println("极坐标圆半径: " + r);
		Map<Integer[], Double[]> features = new HashMap<Integer[], Double[]>();//<[x,y], [,,,,,,,,,]>
		// 宽度
		int scanWidth = (x + 2*r>width? width:x + 2*r);
		for (int j = x; j < scanWidth; j++) {
			// 高度
			for (int i = 0; i < height; i++) {
				
				// 有效点(黑色点)
				if (isBlack(bImage.getRGB(j, i))) {
					int r1 = (int) (r / 3.0 + 0.5);
					int r2 = (int) (r / 3.0 * 2 + 0.5);
					int r3 = r;
					// 当前的(j, i)为圆心,r为半径, 获取极坐标圆内的特征点(把圆分成三个同心圆、45度角切成24等份)
					Double[] count = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
					// 宽度
					int edgeW =  j + r > width ? width : j + r;
					int edgeH =  i + r > height ? height : i + r;
					for (int w = (j - r < 0 ? 0 : j -r); w < edgeW; w++) {
						// 高度
						for (int h = (i - r < 0 ? 0 : i -r) ; h < edgeH; h++) {
							double R = Math.sqrt((w - j) * (w - j) + (h - i) * (h - i));
							// System.out.println("R="+R);
							if (R <= r && R > 0) {//R=0是圆心
								if (isBlack(bImage.getRGB(w, h))) {
									//TODO
									double coordinatorY = i - h;
									double coordinatorX = w - j;
									
									double theater = Math.toDegrees(Math.atan2(coordinatorY, coordinatorX));
									if (theater >= -90 && theater < -45) {
										if (R <= r1) {
											count[0]++;
										} else if (R <= r2) {
											count[1]++;
										} else if (R <= r3) {
											count[2]++;
										}
									} else if (theater >= -45 && theater < 0) {
										if (R <= r1) {
											count[3]++;
										} else if (R <= r2) {
											count[4]++;
										} else if (R <= r3) {
											count[5]++;
										}
									} else if (theater >= 0 && theater < 45) {
										if (R <= r1) {
											count[6]++;
										} else if (R <= r2) {
											count[7]++;
										} else if (R <= r3) {
											count[8]++;
										}
									} else if (theater >= 45 && theater <= 90) {
										if (R <= r1) {
											count[9]++;
										} else if (R <= r2) {
											count[10]++;
										} else if (R <= r3) {
											count[11]++;
										}
									}else if (theater > 90 && theater < 135) {
										if (R <= r1) {
											count[12]++;
										} else if (R <= r2) {
											count[13]++;
										} else if (R <= r3) {
											count[14]++;
										}
									} else if (theater >= 135 && theater < 180) {
										if (R <= r1) {
											count[15]++;
										} else if (R <= r2) {
											count[16]++;
										} else if (R <= r3) {
											count[17]++;
										}
									} else if (theater >= -180 && theater < -135) {
										if (R <= r1) {
											count[18]++;
										} else if (R <= r2) {
											count[19]++;
										} else if (R <= r3) {
											count[20]++;
										}
									} else if (theater >= -135 && theater < -90) {
										if (R <= r1) {
											count[21]++;
										} else if (R <= r2) {
											count[22]++;
										} else if (R <= r3) {
											count[23]++;
										}
									}
									
								}
							}
							
						}
					}
//						System.out.println(count01 + "," + count02 + "," + count03 + "," + count04 + "," + count05 + "," + count06 + "," + count07 + "," + count08 + "," + count09 + "," + count10 + "," + count11 + "," + count12);
//						System.out.print(j+", " +i + "=");
//						for (int h = 0; h < count.length; h++) {
//							System.out.print(count[h]+"\t");
//						}
//						System.out.println();
					features.put(new Integer[]{j,i}, count);
				}
			}
		}
		return features;
	}
	
	
	/**
	 * 把两张图上下排列合并, 假设image1比image2宽度小
	 * merge
	 * @param image1
	 * @param image2
	 * @return
	 */
	public static BufferedImage merge(BufferedImage image1, BufferedImage image2) {
		//合并两个图像
		int w1 = image1.getWidth();
		int h1 = image1.getHeight();
		int w2 = image2.getWidth();
		int h2 = image2.getHeight();
		int newWidth =  w2;
		int newHeight = h1+h2;
		BufferedImage imageSaved = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imageSaved.createGraphics();
		g2d.drawImage(image1, null, 0, 0);
		for (int j = 0; j < newHeight; j++) {
			for (int i = 0; i < newWidth; i++) {
				int rgb;
				if (j < h1) {
					if (i < w1) {
						rgb = image1.getRGB(i, j);
					}else {
						rgb = Color.WHITE.getRGB();
					}
				}else {
					rgb = image2.getRGB(i, j-h1);
				}
				imageSaved.setRGB(i, j, rgb);
			}
		}
		return imageSaved;
	}
	
	//写入的样本最好是png，因为写入jpg时有失真现象
	public static List<BufferedImage> getSample() throws Exception {
		List<BufferedImage> sampleImages = new ArrayList<BufferedImage>();
		File file = new File(Test.class.getResource("").getPath() + "samples/");
		if (file.isDirectory()) {
			for (File file2 : file.listFiles(filter(".png"))) {
//				System.out.println(file2.getAbsolutePath());
				BufferedImage bImage = ImageIO.read(file2);
				sampleImages.add(bImage);
			}
		}
		return sampleImages;
	}

	// type：必须声明为final类型，作为一个匿名内部类，访问的变量都必须声明为final类型
	public static FilenameFilter filter(final String type){
		return new FilenameFilter(){

			public boolean accept(File file, String path) {
				String filename=new File(path).getName();
				return filename.indexOf(type) != -1;
			}

		};

	}
	
	/*static void mergeAndDraw(BufferedImage bImage, BufferedImage bImage2, Map<Integer[], Integer[]> matchPoints, String result) throws IOException {
		// 合并两张图，并标记相似的点的映射情况
		BufferedImage imageMerged = ImageUtil.merge(bImage, bImage2);
		// 对应点画直线
		Graphics2D g2d = imageMerged.createGraphics();
		g2d.setColor(Color.RED);
		// 0.5f 是半透明
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		Set<Integer[]> pointkeySet2 = matchPoints.keySet();
		// 保存最相似的点的坐标
		for (Integer[] integers : pointkeySet2) {
			// System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchPoints.get(integers)[0]+","+matchPoints.get(integers)[1]);
			g2d.drawLine(integers[0], integers[1], matchPoints.get(integers)[0], matchPoints.get(integers)[1] + bImage.getHeight());
		}

		File file4 = new File(ImageUtil.class.getResource("/").getPath() + "temp/" + result+"_"+System.currentTimeMillis() + ".png"); // dest是输出图片的文件名，例如"foo.jpg"
		System.out.println("保存合并标记文件：" + file4.getAbsolutePath());
		ImageIO.write(imageMerged, "png", file4);
	}
	*/
	/*public static double computeFeature(Map<Integer[], Double[]> sampleFeatures, Map<Integer[], Double[]> targetFeatures, BufferedImage bImage, BufferedImage bImage2, String result) throws IOException{
		//计算相似度, cost值越小, 两个点相似度越高
		//遍历样本的像素点，到目标图片查找相似度最高的点的集合
		Set<Integer[]> keySet = sampleFeatures.keySet();
		Set<Integer[]> keySet2 = targetFeatures.keySet();
		//保存最相似的点的坐标
		Map<Integer[], Integer[]> matchPoints = new HashMap<Integer[], Integer[]>();
		double similarCount = 0;
		for (Integer[] integers : keySet) {
				double minCost = 100000.0;
				Integer[] matchXY = new Integer[]{0,0};
//				System.out.print(integers[0]+","+integers[1]+":::[");
//				for (int j = 0; j < features.get(integers).length; j++) {
//					System.out.print(features.get(integers)[j]+",");					
//				}
//				System.out.println("]");
				for (Integer[] integers2 : keySet2) {
					double cost = 0;
//					System.out.print(integers2[0]+","+integers2[1]+":::[");
					for (int j = 0; j < targetFeatures.get(integers2).length; j++) {
//						System.out.print(features2.get(integers2)[j]+",");
						if (0 != (sampleFeatures.get(integers)[j]+ targetFeatures.get(integers2)[j])) {
							if (sampleFeatures.get(integers)[j] !=  targetFeatures.get(integers2)[j]) {
								cost += (targetFeatures.get(integers2)[j]-sampleFeatures.get(integers)[j])*(targetFeatures.get(integers2)[j]-sampleFeatures.get(integers)[j])/(targetFeatures.get(integers2)[j]+sampleFeatures.get(integers)[j]);
							}
						}
					}
//					System.out.println("]");
					if (cost/2.0 <= minCost) {
						matchXY = integers2;
						minCost = cost/2.0;
					}
				}
//				System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchXY[0]+","+matchXY[1]+": "+minCost);
//				System.out.println("minCost="+minCost);
				if (minCost < 1.5) {
					matchPoints.put(integers, matchXY);
					similarCount++;
				}
		}
		
		//合并两张图，并标记出匹配的点，用于分析
		mergeAndDraw(bImage, bImage2, matchPoints, result);
		
		return similarCount;
	}
	*/
	public static String recoginze(File targetImage) throws IOException{
//		System.out.println(file2.getAbsolutePath());
		BufferedImage targetBufferedImage = ImageIO.read(targetImage);
		
		//把四周的白边去掉
		int x1 = -1, x2 = -1, y1 = -1, y2 = -1;
		for (int i = 0; i < targetBufferedImage.getWidth() ; i++) {
			for (int j = 0; j < targetBufferedImage.getHeight(); j++) {
				if (ImageUtil.isBlack(targetBufferedImage.getRGB(i, j))) {
					if (-1 == x1) {
						x1 = i;
					}
				}
			}
		}
		for (int j = 0; j < targetBufferedImage.getHeight(); j++) {
			for (int i = 0; i < targetBufferedImage.getWidth() ; i++) {
				if (ImageUtil.isBlack(targetBufferedImage.getRGB(i, j))) {
					if (-1 == y1) {
						y1 = j;
					}
				}
			}
		}
		for (int i = targetBufferedImage.getWidth() -1; i >=0 ; i--) {
			for (int j = 0; j < targetBufferedImage.getHeight() ; j++) {
				if (ImageUtil.isBlack(targetBufferedImage.getRGB(i, j))) {
					if (-1 == x2) {
						x2 = i;
					}
				}
			}
		}
		for (int j = targetBufferedImage.getHeight() -1; j >=0 ; j--) {
			for (int i = 0; i < targetBufferedImage.getWidth() ; i++) {
				if (ImageUtil.isBlack(targetBufferedImage.getRGB(i, j))) {
					if (-1 == y2) {
						y2 = j;
					}
				}
			}
		}
		
		//遍历样本库
		File file = new File(ImageUtil.class.getResource("/").getPath() + "samples/");
		
		//极坐标圆半径
		int r = 5;
		
		if (file.isDirectory()) {
			
			for (File file2 : file.listFiles()) {
				//file2=样本目录
				if (file2.isDirectory()) {
					for (File file3 : file2.listFiles(filter(".png"))) {
						//单个样本
						BufferedImage bImage = ImageIO.read(file3);
						//当前样本特征
						Map<Integer[], Double[]> sampleFeatures= ImageUtil.getSampleFeatures(bImage, r);
						
						//保存每个样本匹配相似度，最后取最高的作为匹配结果
//						Map<Double, String> resultMap = new HashMap<Double, String>();
						String result = null;
						double maxSimilarCount = -1;
						Map<Integer[], Integer[]> maxMatchPoints = new HashMap<Integer[], Integer[]>();
						//以当前样本宽度计算目标特征
//						int r = (int) (bImage.getWidth() / 2.0);
						int scanWidth = (x2 - 2*r < 1 ? targetBufferedImage.getWidth(): x2 - 2*r);
						for (int i = x1; i < scanWidth; i++) {
							for (int j = y1; j <y2; j++) {
								Map<Integer[], Double[]> targetFeatures = ImageUtil.getFeatures(targetBufferedImage, i, j,  r);
								//计算相似度, cost值越小, 两个点相似度越高
								//遍历样本的像素点，到目标图片查找相似度最高的点的集合
								Set<Integer[]> keySet = sampleFeatures.keySet();
								
								//保存最相似的点的坐标
								Map<Integer[], Integer[]> matchPoints = new HashMap<Integer[], Integer[]>();
								double similarCount = 0.0;
								
								for (Integer[] integers : keySet) {
										double minCost = 100000.0;
										Integer[] matchXY = new Integer[]{0,0};
//										System.out.print(integers[0]+","+integers[1]+":::[");
//										for (int j = 0; j < features.get(integers).length; j++) {
//											System.out.print(features.get(integers)[j]+",");					
//										}
//										System.out.println("]");
										Set<Integer[]> keySet2 = targetFeatures.keySet();
										for (Integer[] integers2 : keySet2) {
											double cost = 0;
//											System.out.print(integers2[0]+","+integers2[1]+":::[");
											for (int j2 = 0; j2 < targetFeatures.get(integers2).length; j2++) {
//												System.out.print(targetFeatures.get(integers2)[j]+",");
												//样本如果需要顺时针旋转45度，将下标向后移3
//												int index = (j2+3)%24;
												int index = j2;
												
												if (0 != (sampleFeatures.get(integers)[index]+ targetFeatures.get(integers2)[j2])) {
													if (sampleFeatures.get(integers)[index] !=  targetFeatures.get(integers2)[j2]) {
														cost += (targetFeatures.get(integers2)[j2]-sampleFeatures.get(integers)[index])*(targetFeatures.get(integers2)[j2]-sampleFeatures.get(integers)[index])/(targetFeatures.get(integers2)[j2]+sampleFeatures.get(integers)[index]);
													}
												}
											}
//											System.out.println("]");
											if (cost/2.0 <= minCost && (cost/2.0)< 0.5) {
												matchXY = integers2;
												minCost = cost/2.0;
											}
										}
//										System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchXY[0]+","+matchXY[1]+": "+minCost);
//										System.out.println("minCost="+minCost);
										if (minCost < 0.5) {
											matchPoints.put(integers, matchXY);
											targetFeatures.remove(matchXY);
											similarCount++;
										}
								}
								
								if (similarCount>maxSimilarCount) {
									maxSimilarCount = similarCount;
									maxMatchPoints = matchPoints;
									result = file2.getName();
								}
								
							}
						}
						
						//保存相似度最大的
						DecimalFormat dcmFmt = new DecimalFormat("0.00");
						String simPercent = dcmFmt.format(maxSimilarCount/sampleFeatures.size()*100);
						System.out.println("相似点个数(匹配代价<=0.4)："+ maxSimilarCount+"，相似度："+ simPercent+"%");
						
						//合并两张图，并标记相似的点的映射情况
						BufferedImage imageMerged = ImageUtil.merge(bImage, targetBufferedImage);
						//对应点画直线
						Graphics2D g2d = imageMerged.createGraphics();
						g2d.setColor(Color.RED);
						// 0.5f 是半透明
					        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					                0.2f));
					        
						Set<Integer[]> pointkeySet2 = maxMatchPoints.keySet();
						//保存最相似的点的坐标
						for (Integer[] integers : pointkeySet2) {
		//					System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchPoints.get(integers)[0]+","+matchPoints.get(integers)[1]);
							g2d.drawLine(integers[0], integers[1], maxMatchPoints.get(integers)[0], maxMatchPoints.get(integers)[1]+ bImage.getHeight());
						}
						
						File file4 = new File(ImageUtil.class.getResource("/").getPath() + "temp/" + simPercent+"%_"+result+"_"+System.currentTimeMillis() + ".png"); // dest是输出图片的文件名，例如"foo.jpg"
						System.out.println("保存合并标记文件：" + file4.getAbsolutePath());
						ImageIO.write(imageMerged, "png", file4);
						
//						File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"_相似度："+ simPercent +"%"+System.currentTimeMillis()+".png"); // dest是输出图片的文件名，例如"foo.jpg"
//						System.out.println("保存合并标记文件："+file4.getAbsolutePath());
//						ImageIO.write(imageMerged, "png", file4);
					}
				}
			}
			//利用TreeMap来排序
//			 Map sortedMap = new TreeMap(resultMap); 
//			System.out.println(sortedMap); 
//			System.out.println("识别结果："+result +", 相似度："+ 100+"%");
		}
		return null;
	}
	
	public static BufferedImage thin2(BufferedImage bufferedImage){
		int h = bufferedImage.getHeight(),  w = bufferedImage.getWidth();
		int[] array = {0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,       
			         1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,       
			         0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,         
			         1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,         
			         1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,         
			         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,         
			         1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,1,         
			         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,         
			         0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,        
			         1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,        
			         0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,        
			         1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,        
			         1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,        
			         1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,        
			         1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,0,        
			         1,1,0,0,1,1,1,0,1,1,0,0,1,0,0,0};
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w;  j++) {
				if (isBlack(bufferedImage.getRGB(j, i))) {
					int a[] = {1,1,1,1,1,1,1,1,1,1};
					for (int k = 0; k < 3; k++) {
						for (int k2 = 0; k2 < 3; k2++) {
							if(-1<(i-1+k) && (i-1+k) < h && -1< (j-1+k2) && (j-1+k2) < w && isBlack(bufferedImage.getRGB(j-1+ k2, i-1+k))){                          
								a[k*3+k2] = 0;
							}
						}
					}
					int sum = a[0]*1+a[1]*2+a[2]*4+a[3]*8+a[5]*16+a[6]*32+a[7]*64+a[8]*128 ;   
					//查表，如果是1则删除该点，如果是0则要保留
					if (array[sum] == 1) {
						bufferedImage.setRGB(j, i, Color.WHITE.getRGB());
					}
					
				}
			}
		}
		
		return bufferedImage;
	}
	
	
	public static BufferedImage thin(BufferedImage bufferedImage, int num){
		for (int i = 0; i < num; i++) {
			thin(bufferedImage);
		}
		return bufferedImage;
	}
	
	public static BufferedImage thin(BufferedImage bufferedImage){
		int h = bufferedImage.getHeight(),  w = bufferedImage.getWidth();
		int[] array = {0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,       
				1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,       
				0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,         
				1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,         
				1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,         
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,         
				1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,1,         
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,         
				0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,        
				1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,1,        
				0,0,1,1,0,0,1,1,1,1,0,1,1,1,0,1,        
				1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,        
				1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,        
				1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,        
				1,1,0,0,1,1,0,0,1,1,0,1,1,1,0,0,        
				1,1,0,0,1,1,1,0,1,1,0,0,1,0,0,0};
		//水平方向
		//在每行水平扫描的过程中，先判断每一点的左右邻居，如果都是黑点，则该点不做处理。另外，如果某个黑店被删除了，则跳过它的右邻居，处理下一点。
		boolean deleteFlag = false;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (deleteFlag) {
					deleteFlag = false;
				} else {
					boolean M = false;
					if (0 < j && j < w - 1) {
						if (isBlack(bufferedImage.getRGB(j - 1, i))
								&& isBlack(bufferedImage.getRGB(j + 1, i))) {
							M = true;
						}
					}
					if (isBlack(bufferedImage.getRGB(j, i)) && !M) {
						int a[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
						for (int k = 0; k < 3; k++) {
							for (int k2 = 0; k2 < 3; k2++) {
								if (-1 < (i - 1 + k)
										&& (i - 1 + k) < h
										&& -1 < (j - 1 + k2)
										&& (j - 1 + k2) < w
										&& isBlack(bufferedImage.getRGB(j - 1
												+ k2, i - 1 + k))) {
									a[k * 3 + k2] = 0;
								}
							}
						}
						int sum = a[0] * 1 + a[1] * 2 + a[2] * 4 + a[3] * 8
						+ a[5] * 16 + a[6] * 32 + a[7] * 64 + a[8]
						                                        * 128;
						// 查表，如果是1则删除该点，如果是0则要保留
						if (array[sum] == 1) {
							bufferedImage.setRGB(j, i, Color.WHITE.getRGB());
							deleteFlag = true;
						}
					}
				}
			}
		}
		
		//垂直方向
		deleteFlag = false;
		for (int j = 0; j < w; j++) {
			for (int i = 0; i < h; i++) {
				if (deleteFlag) {
					deleteFlag = false;
				} else {
					boolean M = false;
					if (0 < i && i < h - 1) {
						if (isBlack(bufferedImage.getRGB(j, i -1))
								&& isBlack(bufferedImage.getRGB(j, i +1))) {
							M = true;
						}
					}
					if (isBlack(bufferedImage.getRGB(j, i)) && !M) {
						int a[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
						for (int k = 0; k < 3; k++) {
							for (int k2 = 0; k2 < 3; k2++) {
								if (-1 < (i - 1 + k)
										&& (i - 1 + k) < h
										&& -1 < (j - 1 + k2)
										&& (j - 1 + k2) < w
										&& isBlack(bufferedImage.getRGB(j - 1
												+ k2, i - 1 + k))) {
									a[k * 3 + k2] = 0;
								}
							}
						}
						int sum = a[0] * 1 + a[1] * 2 + a[2] * 4 + a[3] * 8
						+ a[5] * 16 + a[6] * 32 + a[7] * 64 + a[8]
						                                        * 128;
						// 查表，如果是1则删除该点，如果是0则要保留
						if (array[sum] == 1) {
							bufferedImage.setRGB(j, i, Color.WHITE.getRGB());
							deleteFlag = true;
						}
					}
				}
			}
		}
		
		return bufferedImage;
	}
	
	public static void polar() throws IOException{
		File file = new File(Test.class.getResource("/").getPath() + "samples/k/未标题-2-thin.png");
		System.out.println(file.getAbsolutePath());
		BufferedImage bImage = ImageIO.read(file);
		int w = bImage.getWidth();
		int h = bImage.getHeight();
		int x0 = w / 2;
		int y0 = h / 2;
		BufferedImage destImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g1 = destImage.createGraphics();
		// 用透明模式填充整个区域
		g1.fillRect(0, 0, destImage.getWidth(), destImage.getHeight());

		// int r0 = (int) Math.sqrt(x0*x0 + y0*y0);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (i == x0 && j == y0) {
					continue;
				}
				double r = Math.log(Math.sqrt((i - x0) * (i - x0) + (j - y0) * (j - y0))) * 50;
				double theata = Math.atan2(y0 - j, i - x0) * 50;
				// System.out.println(r+","+theata);
				// int x = (int)(r*Math.cos(theata)), y =(int)(r*Math.sin(theata));

				destImage.setRGB((int) r + 200, (int) theata + 200, bImage.getRGB(i, j));
			}
		}
		File file4 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + "_polar2.png");
		ImageIO.write(destImage, "png", file4);
		System.out.println(file4.getAbsolutePath());

	}
	
	
	public static void polar2() throws IOException{
		File file = new File(Test.class.getResource("/").getPath() + "samples/k/未标题-1-.png");
		System.out.println(file.getAbsolutePath());
		BufferedImage bImage = ImageIO.read(file);
		int w = bImage.getWidth();
		int h = bImage.getHeight();
		int x0 = w / 2;
		int y0 = h / 2;
		BufferedImage destImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g1 = destImage.createGraphics();
		// 用透明模式填充整个区域
		g1.setColor(Color.WHITE);
		g1.fillRect(0, 0, destImage.getWidth(), destImage.getHeight());
		/*
		double[] theatas = new double[64];
		for (int i = 0; i < theatas.length; i++) {
			theatas[i] = -180+(360/64.0)*i;
		}
		*/
		// int r0 = (int) Math.sqrt(x0*x0 + y0*y0);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (i == x0 && j == y0) {
					continue;
				}
				double r = Math.sqrt((i - x0) * (i - x0) + (j - y0) * (j - y0));
				double theata = Math.toDegrees(Math.atan2(y0 - j, i - x0)) +180;
				// System.out.println(r+","+theata);
				// int x = (int)(r*Math.cos(theata)), y =(int)(r*Math.sin(theata));
				int rgb;
				if (isBlack(bImage.getRGB(i, j))) {
					rgb = Color.RED.getRGB();
				}else {
					rgb = Color.GREEN.getRGB();
				}
				destImage.setRGB((int) theata, (int) (r*5), bImage.getRGB(i, j));
			}
		}
		File file4 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + "_polar2.png");
		ImageIO.write(destImage, "png", file4);
		System.out.println(file4.getAbsolutePath());
		
	}

	
	
	static void edge() throws IOException {

		File file2 = new File(Test.class.getResource("/").getPath() + "ots-imgages/img-8grayAndBinary.png");
//		System.out.println(file2.getAbsolutePath());
		BufferedImage bImage2 = ImageIO.read(file2);
		bImage2 = ImageUtil.edgeDetectImg(bImage2);
		//bImage2 = ImageUtil.scaleSmaller(bImage2, 0.5, 0.5);
		File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"edgeDetectImg.png");
		ImageIO.write(bImage2, "png", file4);
		System.out.println(file4.getAbsolutePath());
	}
	
	static void grayAndBinary() throws IOException {
		
		File file2 = new File(Test.class.getResource("/").getPath() + "ots-imgages/img-97.png");
//		System.out.println(file2.getAbsolutePath());
		BufferedImage bImage2 = ImageIO.read(file2);
		bImage2 = ImageUtil.grayAndBinary(bImage2);
		//bImage2 = ImageUtil.scaleSmaller(bImage2, 0.5, 0.5);
		File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"grayAndBinary.png");
		ImageIO.write(bImage2, "png", file4);
		System.out.println(file4.getAbsolutePath());
	}
	
	static void connected() throws IOException {
		
		File file2 = new File(Test.class.getResource("/").getPath() + "ots-imgages/img-8grayAndBinarygetConnectedImggetConnectedImggetConnectedImg.png");
//		System.out.println(file2.getAbsolutePath());
		BufferedImage bImage2 = ImageIO.read(file2);
		bImage2 = ImageUtil.getConnectedPoint(bImage2);
		//bImage2 = ImageUtil.scaleSmaller(bImage2, 0.5, 0.5);
		File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"getConnectedImg.png");
		ImageIO.write(bImage2, "png", file4);
		System.out.println(file4.getAbsolutePath());
	}
	
	public static void main(String[] args) throws IOException {
	/*
	 * //样本 File file = new File(Test.class.getResource("/").getPath() + "samples/d/d01.jpg"); System.out.println(file.getAbsolutePath()); BufferedImage bImage = ImageIO.read(file); //目标 File
	 * file2 = new File(Test.class.getResource("/").getPath() + "samples/d/d02.jpg"); System.out.println(file2.getAbsolutePath()); BufferedImage bImage2 = ImageIO.read(file2); //合并两张图，并标记相似的点的映射情况
	 * BufferedImage imageMerged = ImageUtil.merge(bImage, bImage2); File file4 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) +"_merged.png"); //
	 * dest是输出图片的文件名，例如"foo.jpg" ImageIO.write(imageMerged, "png", file4);
	 */
		/*
		File file2 = new File(Test.class.getResource("/").getPath() + "ots-imgages/img-8.png");
//		System.out.println(file2.getAbsolutePath());
		BufferedImage bImage2 = ImageIO.read(file2);
		bImage2 = ImageUtil.grayAndBinary(bImage2);
		//bImage2 = ImageUtil.scaleSmaller(bImage2, 0.5, 0.5);
		File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"grayAndBinary.png");
		ImageIO.write(bImage2, "png", file4);
		System.out.println(file4.getAbsolutePath());*/
//		polar2();
		
		connected();
//		grayAndBinary();
	}
}
