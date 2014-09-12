package com.ipuzhe.client;

import java.util.Queue;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.ipuzhe.data.DataManager;
import com.ipuzhe.data.Persistence;
import com.ipuzhe.exec.Executor;

/**
 * 乐谱更新客户端 下载信息
 * @author luliuyu
 * @see Downloader 下载图片
 */
public class UpdateClient {

	private static final String format = "http://www.tan8.com/yuepuku.php?ypop=all&sortby=createtime&page=%d";
	private static String UPDATE_URL = "http://www.tan8.com/yuepuku.php?ypop=all&sortby=createtime";
	private static int updatedNum = 0;
	
	private static boolean isFirst = true;
	public static boolean isRunning = true;

	public static void main(String[] args) throws ParserException {

		StringFilter filter = new StringFilter("编号");
		NodeList nodes = newParser(filter);

		Node p = nodes.elementAt(0);
		String mcTotal = p.getText();
		int latest77Id = Integer.valueOf(mcTotal.substring(3));

		Long latestLocalId = Persistence.getLatest77Id();

		//latestLocalId = 33375L;
		int dist = (int) (latest77Id - latestLocalId) ;
		
		if (dist > 0) {
			
			for(int i = (dist)/10 + 1 ;  i > 0 ;i--  )
			{
				UPDATE_URL = String.format(format, i);
				//System.out.println(UPDATE_URL);
				update();
			}
			
			System.out.println("更新了 "+updatedNum+" 个");
			
		}
		else {
			System.out.println("木有更新");
		}
		
		//下载InfoURL
		executeNewThread();

	}
	
	public static void executeNewThread(){
		Queue<String> data =  Persistence.nullUrlId();
		
		if(data.isEmpty()){
			System.out.println("URL下载完成");
		}else{
			DataManager dataManager = new DataManager(data );
			Executor executor = new Executor(dataManager);
			
			if(isFirst){
				Executor.init();
				isFirst = false;
			}
		
			executor.start();
		}

	}

	private static NodeList newParser(NodeFilter filter) throws ParserException {
		return new Parser(UPDATE_URL).extractAllNodesThatMatch(filter);
	}
	
	private static void update() throws ParserException{
		
			NodeList tables = newParser(new TagNameFilter("table"));

			Node tr = null;
			Node table = tables.elementAt(0);
			NodeList trs = table.getChildren();
			for (int i = trs.size() - 1; i >= 0; i--) {// 循环写入当页的乐谱信息

				tr = trs.elementAt(i);
				if (tr instanceof TableRow) {

					NodeList children = tr.getChildren();

					Node idNode = children.elementAt(21);
					String text = idNode.getChildren().elementAt(1)
							.getChildren().elementAt(0).getText();
					Long id = Long.valueOf(text.substring(3)); // 77 id

					Node node = children.elementAt(7);
					NodeList detials = node.getChildren();
					String title = detials.elementAt(1).getChildren()
							.elementAt(3).getChildren().elementAt(0).getText();

					String detail = detials.elementAt(3).getChildren()
							.elementAt(0).getText();
					String str = detail.substring(3, detail.lastIndexOf(']'));
					String[] a = str.split("[,，]");
					String style = a[0];
					String tonality = a[1];

					String author = children.elementAt(11).getChildren()
							.elementAt(1).getChildren().elementAt(0).getText();

					Long authorId = Persistence.saveAuthor(author);
					Long musicStyleId = Persistence.saveStyle(style);
					Long tonalityId = Persistence.saveTonaliy(tonality);
					// no eye to see
					
					if (Persistence.isExistedQuqu(id) == 0) {
						Long mid = Persistence.saveCrawler77(id);
						Persistence.saveMusicCase(mid, title, authorId, 3L,
								musicStyleId, tonalityId);
						updatedNum++;
					}
			}

		} 
	}

}
