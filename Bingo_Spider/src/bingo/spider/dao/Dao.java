package bingo.spider.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * 数据库存储DAO
 * @author BingYue
 *
 */
public class Dao {
	
	//静态全局变量
	public static String DRIVERNAME="com.mysql.jdbc.Driver";
	public static String URL="jdbc:mysql://localhost/";
	public static String USER="root";
	public static String PASSWORD="mysql";
	/*
	 * 静态代码块，实例化时加载JDBC驱动
	 */
	static{
		try {
			Class.forName(DRIVERNAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得数据库连接
	 * @return
	 */
	public Connection getConnection(){
		Connection conn=null;
		 try {
			conn=DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;	
	}
	
	/**
	 * 预查询语句
	 * @param conn
	 * @return
	 */
	public PreparedStatement getPreStatement(Connection conn){
		PreparedStatement preStat=null;
		StringBuffer sqlbuffer=new StringBuffer();
		sqlbuffer.append("SELECT link FROM test.LINK_INFO");
		try {
			preStat=conn.prepareStatement(sqlbuffer.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preStat;
	}
	
	/**
	 * 获得查询结果集
	 * @param preStat
	 * @return
	 */
	public ResultSet getResuleSet(PreparedStatement preStat){
		ResultSet rs = null;
		try {
			rs=preStat.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 读取数据库link_info表中储存的url
	 * @param stat
	 * @return
	 */
	public ResultSet getResuleSet(Statement stat){
		ResultSet rs = null;
		String sql = "SELECT link FROM test.LINK_INFO";
		try {
			rs = stat.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 查询条件
	 * @param conn
	 * @return
	 */
	public Statement getStatement(Connection conn) {
		Statement stat = null;
		try {
			stat = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stat;
	}
	
	public void saveData(Statement stat, String str){
		try {
			stat.executeUpdate(str);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 关闭数据库连接
	 * @param conn
	 * @param preStat
	 * @param stat
	 * @param rs
	 */
	public void closeAll(Connection conn,PreparedStatement preStat,Statement stat,ResultSet rs){
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
