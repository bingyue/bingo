package bingo.spider.htmlparser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import bingo.spider.dao.Dao;
import bingo.spider.file.ContentSave;

/**
 * 启动解析html文件的服务
 * @author BingYue
 *
 */
public class Main {
	
	private Connection conn = null;//数据库的链接

	private Statement stat = null;//数据库的语句

	private ResultSet rs = null;//数据库返回的结果集

	private Dao dao = null;//数据访问对象
    
	/**
	 * Main函数
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String args[]) throws SQLException, IOException {
		new Main().start();
	}
	
	/**
	 * 构造函数
	 * 初始化数据库连接
	 */
	public Main() {
		initDatabase();
    }
    
	/**
	 * 链接数据库的初始化函数
	 */
	public void initDatabase() {
		dao = new Dao();
		conn = dao.getConnection();
		stat = dao.getStatement(conn);
		rs = dao.getResuleSet(stat);
	}
    
	 /**
	  * 启动Main类
	  */
	public void start() throws SQLException, IOException {
		while (rs.next()) {
			String link = rs.getString("link");
			parser(link);
		}
		//未使用PreparedStatement
		dao.closeAll(conn, null,stat, rs);
		quit();
	}
	
	public void parser(String link) throws IOException {
		String html = HtmlParser.getWebPage(link);
		String title = HtmlParser.getTitle(html);
		String keywords = HtmlParser.getKeywords(html);
		String description = HtmlParser.getDes(html);
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		//保存到本地文件系统
		ContentSave.SaveHTML(title, html);
		ContentSave.SaveTxt(date, title, link, keywords, description);
	}
	
	/**
	 * 程序运行结束时推出函数
	 */
	public void quit() {
		System.out.println("网页已经处理完毕,程序退出!");
		System.exit(0);
	}

	

}
