package com.ipuzhe.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

public class Test {

	public static void getWebImage() throws IOException {
		String url = "http://regcheckcode.taobao.com/auction/checkcode?sessionID=842e8d093e5b988e6085b0aedc112ffe";
		for (int i = 10; i < 200; i++) {
			URL weburl = new URL(url);
			InputStream in = new BufferedInputStream(weburl.openStream());

			// 保存到文件
			File file = new File("tb-images/" + "img-" + i + ".png");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			int t;
			while ((t = in.read()) != -1) {
				fileOutputStream.write(t);
			}

			fileOutputStream.close();
			in.close();
		}

		System.out.println("保存图片完毕.");
	}
	
	static void testCompute() throws IOException{
		//样本
		File file = new File(Test.class.getResource("/").getPath() + "samples/6/未标题-6-thin.png");
//		System.out.println(file.getAbsolutePath());
		BufferedImage bImage = ImageIO.read(file);
		int r = 5;
		//计算特征
		Map<Integer[], Double[]> features= ImageUtil.getSampleFeatures(bImage, r);
		System.out.println("样本特征个数："+ features.size());
		
		//目标
		File file2 = new File(Test.class.getResource("/").getPath() + "image-thin/img-171-thin.png");
//		System.out.println(file2.getAbsolutePath());
		BufferedImage bImage2 = ImageIO.read(file2);
		//以样本宽度为窗口从左往右移动，计算特征
		int width = bImage2.getWidth();
		int height = bImage2.getHeight();
		
		
		
		//把四周的白边去掉
		int x1 = -1, x2 = -1, y1 = -1, y2 = -1;
		for (int i = 0; i < width ; i++) {
			for (int j = 0; j < height; j++) {
				if (ImageUtil.isBlack(bImage2.getRGB(i, j))) {
					if (-1 == x1) {
						x1 = i;
					}
				}
			}
		}
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width ; i++) {
				if (ImageUtil.isBlack(bImage2.getRGB(i, j))) {
					if (-1 == y1) {
						y1 = j;
					}
				}
			}
		}
		for (int i = width -1; i >=0 ; i--) {
			for (int j = 0; j < height ; j++) {
				if (ImageUtil.isBlack(bImage2.getRGB(i, j))) {
					if (-1 == x2) {
						x2 = i;
					}
				}
			}
		}
		for (int j = height -1; j >=0 ; j--) {
			for (int i = 0; i < width ; i++) {
				if (ImageUtil.isBlack(bImage2.getRGB(i, j))) {
					if (-1 == y2) {
						y2 = j;
					}
				}
			}
		}
		
		
		double maxSimilarCount = -1;
		int scanWidth = (x2 - 2*r < 1 ? bImage2.getWidth(): x2 - 2*r);
		for (int i = x1; i < scanWidth; i++) {
			for (int j = y1; j < y2; j++) {
				Map<Integer[], Double[]> features2 = ImageUtil.getFeatures(bImage2, i, j,  r);
//				System.out.println("目标特征个数："+ features2.size());
				//计算相似度, cost值越小, 两个点相似度越高
				//遍历样本的像素点，到目标图片查找相似度最高的点的集合
				Set<Integer[]> keySet = features.keySet();
				Set<Integer[]> keySet2 = features2.keySet();
				//保存最相似的点的坐标
				Map<Integer[], Integer[]> matchPoints = new HashMap<Integer[], Integer[]>();
				double similarCount = 0.0;
				
				for (Integer[] integers : keySet) {
						double minCost = 100000.0;
						Integer[] matchXY = new Integer[]{0,0};
//						System.out.print(integers[0]+","+integers[1]+":::[");
//						for (int j = 0; j < features.get(integers).length; j++) {
//							System.out.print(features.get(integers)[j]+",");					
//						}
//						System.out.println("]");
						for (Integer[] integers2 : keySet2) {
							double cost = 0;
//							System.out.print(integers2[0]+","+integers2[1]+":::[");
							for (int j2 = 0; j2 < features2.get(integers2).length; j2++) {
//								System.out.print(features2.get(integers2)[j]+",");
								
								//样本如果需要顺时针旋转45度，将下标向后移3
//								int index = (j2+3)%24;
								int index= j2;
								
								if (0 != (features.get(integers)[index]+ features2.get(integers2)[j2])) {
									if (features.get(integers)[index] !=  features2.get(integers2)[j2]) {
										cost += (features2.get(integers2)[j2]-features.get(integers)[index])*(features2.get(integers2)[j2]-features.get(integers)[index])/(features2.get(integers2)[j2]+features.get(integers)[index]);
									}
								}
							}
//							System.out.println("]");
							if (cost/2.0 <= minCost  && (cost/2.0)< 0.5) {
								matchXY = integers2;
								minCost = cost/2.0;
							}
						}
//						System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchXY[0]+","+matchXY[1]+": "+minCost);
//						System.out.println("minCost="+minCost);
						//保存匹配代价最小的即最相似的点
						if (minCost < 0.5) {
							matchPoints.put(integers, matchXY);
							similarCount++;
						}
				}
				
				if (similarCount>maxSimilarCount) {
					maxSimilarCount = similarCount;
					DecimalFormat dcmFmt = new DecimalFormat("0.00");
					String simPercent = dcmFmt.format(similarCount/features.size()*100);
					System.out.println("相似点个数(匹配代价<=0.5)："+ similarCount+"，相似度："+ simPercent+"%");
					
					//合并两张图，并标记相似的点的映射情况
					BufferedImage imageMerged = ImageUtil.merge(bImage, bImage2);
					//对应点画直线
					Graphics2D g2d = imageMerged.createGraphics();
					g2d.setColor(Color.RED);
					// 0.5f 是半透明
				        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				                0.2f));
				        
					Set<Integer[]> pointkeySet2 = matchPoints.keySet();
					//保存最相似的点的坐标
					for (Integer[] integers : pointkeySet2) {
	//					System.err.println("\n"+integers[0]+","+integers[1]+"-->"+matchPoints.get(integers)[0]+","+matchPoints.get(integers)[1]);
						g2d.drawLine(integers[0], integers[1], matchPoints.get(integers)[0], matchPoints.get(integers)[1]+ bImage.getHeight());
					}
					
					File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"_相似度："+ simPercent +"%"+System.currentTimeMillis()+".png"); // dest是输出图片的文件名，例如"foo.jpg"
					System.out.println("保存合并标记文件："+file4.getAbsolutePath());
					ImageIO.write(imageMerged, "png", file4);
				}
			}
		}
		
		
		
		/*
		for (int i = 0; i < featureList.size(); i++) {
			if (0 != i && i%12==0) {
				counter += cost/2.0;
				System.out.println(cost/2.0 +"======"+counter);
				cost = 0;
			}else {
				if (0 != (featureList2.get(i)+featureList.get(i))) {
					cost += (featureList2.get(i)-featureList.get(i))*(featureList2.get(i)-featureList.get(i))/(featureList2.get(i)+featureList.get(i));
				}
			}
			
		}
		
		System.out.println("总的匹配代价: "+counter);*/
//		System.out.println(1.3258176636680326);
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
	static void thinSample() throws IOException{
		File file = new File("C:\\Documents and Settings\\Administrator\\桌面\\原始字符");
		if (file.isDirectory()) {
			for (File file2 : file.listFiles(filter(".png"))) {
				System.out.println(file2.getAbsolutePath());
				BufferedImage bImage = ImageIO.read(file2);
				
//				ImageUtil.rotate(90,bImage);
//				bImage = ImageUtil.scaleSmaller2(bImage,0.8,0.8);
//				bImage = ImageUtil.grayAndBinary(bImage);
				bImage = ImageUtil.thin(bImage,10);
				
				File file4 = new File(file2.getAbsolutePath().substring(0, file2.getAbsolutePath().lastIndexOf("."))  +"-thin.png");
				ImageIO.write(bImage, "png", file4);
			}
		}
	}
	
	
	static void testCompute2() throws IOException{
		//样本
		File file = new File(Test.class.getResource("/").getPath() + "新建文件夹/test-6_2_.png");
//		System.out.println(file.getAbsolutePath());
		BufferedImage bImage = ImageIO.read(file);
		//计算特征
		Map<Integer[], Double[]> features= ImageUtil.getFeatures(bImage, 0, 0, (int) (bImage.getWidth() / 2.0 + 0.5));
		System.out.println("样本像素点数："+ features.size());
		
		Set<Integer[]> keySet = features.keySet();
		for (Integer[] integers : keySet) {
				System.out.print(integers[0]+","+integers[1]+":::[");
				for (int j = 0; j < features.get(integers).length; j++) {
					System.out.print(features.get(integers)[j]+",");					
				}
				System.out.println("]");
		}
	}

	public static void main(String[] args) {

		try {
			
			long start = System.currentTimeMillis();
			/*for (int i = 10; i < 11; i++) {
				File file = new File(Test.class.getResource("/").getPath() + "untitled.bmp");
				System.out.println(file.getAbsolutePath());
				BufferedImage bImage = ImageIO.read(file);
				
//				ImageUtil.rotate(90,bImage);
//				bImage = ImageUtil.scaleSmaller2(bImage,0.8,0.8);
				bImage = ImageUtil.grayAndBinary(bImage);
//				bImage = ImageUtil.thin(bImage,10);
				
				File file4 = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+"-thin.png"); 
				ImageIO.write(bImage, "png", file4);
			}*/
			/*
			File file = new File(Test.class.getResource("/").getPath() + "samples/d/d01.jpg");
			System.out.println(file.getAbsolutePath());
			BufferedImage bImage = ImageIO.read(file);
			int result[][] = ImageUtil.toBinaryArray(bImage);
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[i].length; j++) {
//					System.out.print(result[i][j]);
					System.out.print((0==result[i][j])?"□":"■");
				}
				System.out.println();
			}
			
			result = ImageUtil.edgeDetectArray(result);
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[i].length; j++) {
//					System.out.print(result[i][j]);
//					System.out.print(result[i][j]);
					System.out.print((3==result[i][j])?"■":"□");
				}
				System.out.println();
			}
			*/
			ImageUtil.recoginze(new File(Test.class.getResource("/").getPath() + "image-thin/img-17-thin.png"));
//			testCompute();
//			thinSample();
			System.out.println("耗时："+(System.currentTimeMillis() - start)/1000.0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
