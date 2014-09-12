package com.ipuzhe.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mysql.jdbc.Statement;

/**
 * 
 * @author luliuyu
 * @since  2014.8.13
 */
public class Persistence {
    static Connection conn = null;
    
   /**
    * 
    * @param content
    * @param errorUrl
    * @return
    */
    public static int saveLog(String content, String errorUrl) {
        Connection conn;
        int executeNum = 0;
        try {
            conn = getConnection();
            String sql = "insert into log (content,errorUrl) values (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, content);
            ps.setString(2, errorUrl);
            executeNum = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Persistence save log exception " + content + " " + errorUrl);
        }
        return executeNum;
    }
    
    /**
     * 
     * @param picUrl
     * @param page
     * @param musicCaseId
     * @return
     */
    public static int savePicture(String picUrl, int page, Long musicCaseId) {
        Connection conn;
        int executeNum = 0;
        try {
            conn = getConnection();
            String sql = "insert into mc_picture (picUrl,page,musicCaseId) values (?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, picUrl);
            ps.setInt(2, page);
            ps.setLong(3, musicCaseId);
            executeNum = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Persistence save picture exception " + page + " " + picUrl);
            saveLog("Persistence save picture exception " + page + " " + picUrl, null);
            
        }
        return executeNum;
    }
    
    /**
     * 
     * @param name
     * @return
     */
	public static Long saveAuthor(String name) {
		Connection conn;
		int executeNum = 0;
		Long id = (long) 1;
		try {
			conn = getConnection();

			String sql = "select id from mc_author where name = ?";
			PreparedStatement ps = conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				sql = "insert into mc_author (name) values (?)";
				ps = conn
						.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, name);
				executeNum = ps.executeUpdate();
				if (executeNum > 0) {
					ResultSet res = ps.getGeneratedKeys();
					if (res.next()) {
						id = res.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Persistence save author exception " + name);
			saveLog("Persistence save author exception " + name, null);
		}
		return id;

	}
            
            
           
    
    /**
     * 检查是否存在，存在返回id,不存在保存返回id
     * @param name
     * @return
     */
    public static Long getAuthor(String name) {
        Connection conn;
        Long id = 0L;
        try {
            conn = getConnection();
            String sql = "select * from mc_author where name = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
                id=res.getLong("id"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Persistence search author exception " + name);
            saveLog("Persistence search author exception " + name, null);
        }
        return id;
    }
    
    /**
     * 保存本地id与77id的关系
     * @param ququ_id
     * @return
     */
    public static Long saveCrawler77(Long ququ_id) {
        Connection conn;
        int executeNum = 0;
        Long id = (long)1;
        try {
            conn = getConnection();
            String sql = "insert into crawler_77 (ququ_id,isSynIpad) values (?,0)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, ququ_id);
            executeNum = ps.executeUpdate();
            if(executeNum > 0) { 
                ResultSet res = ps.getGeneratedKeys();
                if(res.next())  {
                    id=res.getLong(1); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveLog("Persistence save crawler_77 exception " + ququ_id, null);
        }
        return id;
    }
    
    /**
     * 
     * @param ququ_id
     * @return
     */
    public static Long isExistedQuqu(Long ququ_id) {
        Connection conn;
        Long id = 0L;
        try {
            conn = getConnection();
            String sql = "select * from crawler_77 where ququ_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, ququ_id);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
            	System.out.println("已经存在,77网id：" + ququ_id);
            	id = res.getLong("musicCaseId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    
    /**
     * 保存乐谱
     * @param id
     * @param title
     * @param authorId
     * @param musicCaseTypeId
     * @param musicStyleId
     * @param tonalityId
     * @return
     */
    public static Long saveMusicCase(Long id , String title, Long authorId, Long musicCaseTypeId,Long musicStyleId,Long tonalityId) {
        Connection conn;
        try {
            conn = getConnection();
            String sql = "insert into mc_musiccase (title, authorId, musicType,musicStyle,tonalityId,id) values (?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setLong(2, authorId);
            ps.setLong(3, musicCaseTypeId);
            ps.setLong(4, musicStyleId);
            ps.setLong(5, tonalityId);
            ps.setLong(6, id);
            
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Persistence save music case exception " + title + " " + authorId + "" + musicCaseTypeId);
            saveLog("Persistence save music case exception " + title + " " + authorId + "" + musicCaseTypeId, null);
        }
        return id;
    }
    
    /**
     * 补写之前的关联数据
     * @param isSynIpad
     * @param ququ_id
     * @return
     */
     public static int makeUpCrawler_77(boolean isSynIpad, long ququ_id) {
         Connection conn;
         int executeNum = 0;
         try {
             conn = getConnection();
             String sql = "insert into crawler_77 (isSynIpad,ququ_id) values (?,?)";
             PreparedStatement ps = conn.prepareStatement(sql);
             ps.setBoolean(1, isSynIpad);
             ps.setLong(2, ququ_id);
             executeNum = ps.executeUpdate();
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("Persistence save saveCrawler_77 exception " + isSynIpad + " " + ququ_id);
         }
         return executeNum;
     }
    
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        if(null == conn) {
            ConnectionFactory cf = new ConnectionFactory();
            conn = cf.getConnection();
        }
        return conn;
    }

	public static void updateMusicCase(Long id, String encryCode) {
        Connection conn;
        try {
            conn = getConnection();
            String sql = "update mc_musicCase set encryCode = ? where idForCrawler = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, encryCode);
            ps.setLong(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Persistence update musicCase exception " + id);
            saveLog("Persistence update musicCase exception " + id, null);
        }
	}

	public static Long countMusicCase() {
		Connection conn;
        Long num = 0L;
        try {
            conn = getConnection();
            String sql = "SELECT count(*) num FROM mc_musiccase";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
            	num=res.getLong("num"); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
	}
	
	
	public static Long saveStyle(String name) {
		
		Connection conn;
		int executeNum = 0;
		Long id = (long) 1;
		try {
			conn = getConnection();

			String sql = "select id from mc_musicstyle where name = ?";
			PreparedStatement ps = conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				sql = "insert into mc_musicstyle (name) values (?)";
				ps = conn
						.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, name);
				executeNum = ps.executeUpdate();
				if (executeNum > 0) {
					ResultSet res = ps.getGeneratedKeys();
					if (res.next()) {
						id = res.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Persistence save mc_musicstyle exception " + name);
			saveLog("Persistence save mc_musicstyle exception " + name, null);
		}
		return id;
	}

	public static Long saveTonaliy(String name) {
	
		Connection conn;
		int executeNum = 0;
		Long id = (long) 1;
		try {
			conn = getConnection();

			String sql = "select id from mc_tonality where name = ?";
			PreparedStatement ps = conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				sql = "insert into mc_tonality (name) values (?)";
				ps = conn
						.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, name);
				executeNum = ps.executeUpdate();
				if (executeNum > 0) {
					ResultSet res = ps.getGeneratedKeys();
					if (res.next()) {
						id = res.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Persistence save mc_tonality exception " + name);
			saveLog("Persistence save mc_tonality exception " + name, null);
		}
		return id;
	}
	
	
	public static void main(String[] args) {
		
		nullUrlId();
		//System.out.println(getUndownloadPicId());
	}

	public static Long getLatest77Id() {
		Connection conn;
        Long num = 0L;
        try {
            conn = getConnection();
            String sql = "SELECT ququ_id  FROM crawler_77 ORDER BY  musicCaseId DESC limit 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
            	num=res.getLong("ququ_id"); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
	}

	public static Long getMidByQuId(Long ququ_id) {
		Connection conn;
        Long num = 0L;
        try {
            conn = getConnection();
            String sql = "SELECT musicCaseId  FROM crawler_77 where ququ_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, ququ_id);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
            	num=res.getLong("musicCaseId"); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
	}

	public static int countPic(Long mid) {
		Connection conn;
        int num = 0;
        try {
            conn = getConnection();
            String sql = "SELECT count(*) num FROM mc_picture where musiccaseid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, mid);
            ResultSet res = ps.executeQuery();
            if(res.next())  {
            	num=res.getInt("num"); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
	}

	public static List<Integer> getPageList(Long mid) {
		Connection conn;
        List<Integer> list = Collections.emptyList();
        try {
            conn = getConnection();
            String sql = "SELECT page  FROM mc_picture where musiccaseid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, mid);
            ResultSet res = ps.executeQuery();
            
            list = new ArrayList<Integer>();
            while(res.next())  {
            	list.add(res.getInt("page"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
	}

	public static void updateIsDownPic(Long mid) {
		
		 try {
	            conn = getConnection();
	            String sql = "update   crawler_77 set isDownloadPic = 1 where musiccaseid = ?";
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setLong(1, mid);
	            ps.executeUpdate();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	public static void updateAuthParam(Long ququ_id, String auth)
			throws SQLException, ClassNotFoundException, IOException {

		conn = getConnection();
		String sql = "update   crawler_77 set auth_param = ? where ququ_id = ?  and isDownloadPic = 0";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, auth);
		ps.setLong(2, ququ_id);
		ps.executeUpdate();

	}

	public static Queue<String> getUndownloadPicId() {
		
		Connection conn;
		Queue<String> q = null;
        try {
            conn = getConnection();
            String sql = "SELECT DISTINCT ququ_id  FROM  crawler_77 where isDownloadPic = 0";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet res = ps.executeQuery();
            
            q = new ConcurrentLinkedQueue<String>();
            while(res.next())  {
            	q.add(String.valueOf(res.getInt("ququ_id")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return q;
	}

	/**
	 * 用于删除重复图片文件
	 * @return
	 * @throws SQLException 
	 */
	public static List<String> getNewPicUrls()  {
		
            String sql = "SELECT picUrl FROM `mc_picture` where musicCaseId >= 117197";
            ResultSet res =  execute(sql);
            
            ArrayList<String> list = new ArrayList<String>();
            
			try {
				while(res.next())  {
					list.add(res.getString("picUrl"));
				}
			} catch (SQLException e) {
				throw new IllegalArgumentException("数据库异常");
			}
            
        return list;
	}
	
	/**
	 * 执行sql
	 * @param sql
	 * @return
	 */
	static ResultSet execute(String sql){
		
		Connection conn;
		ResultSet res = null;
		
        try {
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return res;
	}

	public static Queue<String> nullUrlId() {
		
		  String sql = "SELECT * FROM `crawler_77` where auth_param is  null and isDownloadPic = 0";
          ResultSet res =  execute(sql);

          Queue<String> q = new ConcurrentLinkedQueue<String>();
          
			try {
				while(res.next())  {
					q.add(res.getString("ququ_id"));
				}
			} catch (SQLException e) {
				throw new IllegalArgumentException("数据库异常");
			}
			
          return q;
	}
	
	public static Queue<String> notNullUrl() {
		
		  String sql = "SELECT auth_param FROM `crawler_77` where auth_param is not null and isDownloadPic = 0";
        ResultSet res =  execute(sql);

        Queue<String> q = new ConcurrentLinkedQueue<String>();
        
			try {
				while(res.next())  {
					q.add(res.getString("auth_param"));
				}
			} catch (SQLException e) {
				throw new IllegalArgumentException("数据库异常");
			}
			
        return q;
	}
}
