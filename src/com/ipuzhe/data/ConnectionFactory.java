package com.ipuzhe.data;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;

public class ConnectionFactory {
	private static Properties prop;
	private static final String CONFIGNAME = "/musicscore.properties";
	private Connection conn;
	private String url;
	private String userName;
	private String password;
	
	public ConnectionFactory(){
		
	}
	
	public Connection getConnection() throws ClassNotFoundException,SQLException, IOException{
		prop = new Properties();
		prop.load(this.getClass().getResourceAsStream(CONFIGNAME));
		Class.forName(prop.getProperty("driverClassName"));
		url=prop.getProperty("url");
		userName=prop.getProperty("username");
		password=prop.getProperty("password");
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}
	
	public static void close(ResultSet rs,Statement stm,Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(stm!=null){
			try {
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
