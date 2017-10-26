package com.dump;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接对象
 * 由于架构单线程无并发的特点 不使用连接池 保持一个连接即可
 */
public class DBConnection {	
	private String dbUrl;			//数据库连接URL
	private String dbUser;			//用户名
	private String dbPwd;			//密码
	
	/**
	 * 创建目标数据库连接
	 * @return
	 */
	public static DBConnection createTargetConn() {
		return new DBConnection(Config.TARGET_DBURL, Config.TARGET_DBUSER, Config.TARGET_DBPWD); 
	}
	
	/**
	 * 创建源数据库连接
	 * @return
	 */
	public static DBConnection createSourceConn() {
		return new DBConnection(Config.SOURCE_DBURL, Config.SOURCE_DBUSER, Config.SOURCE_DBPWD); 
	}
	
	/**
	 * 创建目标数据库连接
	 * @return
	 */
	public static DBConnection createTargetSystemConn() {
		String url = Config.TARGET_DBURL;
		String subUrl = url.substring(0, 28);
		StringBuilder sb = new StringBuilder(subUrl);
		sb.append("mysql");
		return new DBConnection(sb.toString(), Config.TARGET_DBUSER, Config.TARGET_DBPWD); 
	}
	
	public DBConnection(String dbUrl,String dbUser, String dbPwd) {
		try {
			this.dbUrl = dbUrl;
			this.dbUser = dbUser;
			this.dbPwd = dbPwd;
			
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取PrepareStatement对象
	 * @param sql
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return getConnection().prepareStatement(sql);
	}
	
	/**
	 * 获取Statement对象
	 * @return
	 * @throws SQLException 
	 */
	public Statement createStatement() throws SQLException {
		return getConnection().createStatement();
	}
	
	/**
	 * 关闭
	 * @throws SQLException 
	 */
	public void close() throws SQLException {
		if(CONN == null) return;

		CONN.close();
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 * @throws SQLException 
	 */
	private Connection getConnection() throws SQLException {
		if(CONN == null || !CONN.isValid(0)) {
			CONN = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			CONN.prepareStatement("set names utf8mb4").executeQuery();
			//记录日志
			System.out.println("创建新的数据库连接。" + dbUrl);
		}
		
		return CONN;
	}
	private Connection CONN = null;			//保持连接 不要直接使用这个属性来获取连接
	
	public static void main(String[] args) {
		createTargetSystemConn();
	}
}
