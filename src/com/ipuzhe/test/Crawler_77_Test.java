package com.ipuzhe.test;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ipuzhe.data.Persistence;

public class Crawler_77_Test {

	public static void main(String[] args) throws ParserException {
		
		//最新列表
		String url = "http://www.77music.com/yuepuku.php?ypop=all&sortby=createtime";
		
		Parser parser = new Parser();
		parser.setURL(url);
		
		StringFilter filter = new StringFilter("共有");
		NodeList nodes = parser.extractAllNodesThatMatch(filter);
		
		if (nodes.size() > 1) 
			throw new IllegalStateException("总数标签只能是1个");
		
		Node div = nodes.elementAt(0);
		Node span = div.getNextSibling();
		String mcTotal = span.getFirstChild().getText();
		int total_77 = Integer.valueOf(mcTotal); 
		
		
		Long count = Persistence.countMusicCase();
		
		if(total_77 > count){
			System.out.printf("有%d个乐谱更新  77网：%d个  本地：%d个",total_77 - count ,total_77,count);
			
			NodeList tables = new Parser(url).extractAllNodesThatMatch(new TagNameFilter("table"));
			
			Node tr = null;
			Node table = tables.elementAt(0);
			NodeList trs = table.getChildren();
			for(int i = 0,length = trs.size(); i<length;i++){
				
				tr = trs.elementAt(i);
				if (tr instanceof TableRow){
					
					NodeList children = tr.getChildren();
					
					Node idNode = children.elementAt(21);
					String text = idNode.getChildren().elementAt(1).getChildren().elementAt(0).getText();
					Long id = Long.valueOf(text.substring(3));
					System.out.println(id);
					
					Node node = children.elementAt(7);
					NodeList detials = node.getChildren();
					String title = detials.elementAt(1).getChildren().elementAt(3).getChildren().elementAt(0).getText();
					
					String detail = detials.elementAt(3).getChildren().elementAt(0).getText();
					String str = detail.substring(3, detail.lastIndexOf(']'));
					String[] a = str.split("[,，]");
					String style =  a[0];
					String tonality = a[1];
					
					String author = children.elementAt(11).getChildren().elementAt(1).getChildren().elementAt(0).getText();
					
					Long authorId = Persistence.saveAuthor(author);
					Long musicStyleId = Persistence.saveStyle(style);
					Long tonalityId = Persistence.saveTonaliy(tonality);//no eye to see
					Long mid = Persistence.saveCrawler77(id);
					
					Persistence.saveMusicCase(mid, title, authorId, (long) 3, musicStyleId, tonalityId);
					
					
					System.out.println(); 
				}
				
			}
			
			
			
			
		}else{
			System.out.println("木有更新");
		}
		
	}

}
