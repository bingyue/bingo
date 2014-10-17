package bingo.spider.spider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import bingo.spider.dao.Dao;

/**
 * 爬虫主程序
 * @author BingYue
 *
 */
public class Spider {

	Connection conn = null;
	Statement stat = null;
	Dao dao = null;
	//储存错误链接 数组
	protected Collection<Object> workloadError = new ArrayList<Object>(3);
	//储存等待爬取的链接
	protected Collection<Object> workloadWaiting = new ArrayList<Object>(3);
	//储存已爬取的链接
	protected Collection<Object> workloadProcessed = new ArrayList<Object>(3);

	/**
	 * 调用IspiderReportable接口的实现类
	 */
	protected ISpiderReportable report;

	/**
	 * 取消爬取操作
	 */
	protected boolean cancel = false;

	/**
	 * 构造子
	 * @param report 接收爬取到的信息
	 */
	public Spider(ISpiderReportable report) {
		this.report = report;
		initDatabase();
	}
	
	/**
	 * 初始化数据库工作环境
	 */
	public void initDatabase() {
		dao = new Dao();
		conn = dao.getConnection();
		stat = dao.getStatement(conn);
	}

	/**
	 * workloadWaiting的get方法
	 * 初始值由用户输入
	 * @return url集合
	 */
	public Collection<Object> getWorkloadWaiting() {
		return workloadWaiting;
	}

	/**
	 * workloadProcessed的get方法
	 * @return
	 */
	public Collection<Object> getWorkloadProcessed() {
		return workloadProcessed;
	}

	/**
	 * workloadError的get方法
	 * @return 
	 */
	public Collection<Object> getWorkloadError() {
		return workloadError;
	}

	/**
	 * 清空所有队列
	 * 确保程序开始时URL列表为空
	 */
	public void clear() {
		getWorkloadError().clear();
		getWorkloadWaiting().clear();
		getWorkloadProcessed().clear();
	}

	/**
	 * cancel方法，将cancel标志设置为真
	 */
	public void cancel() {
		cancel = true;
		//关闭全部数据库连接
//		dao.closeAll();
		
	}

	/**
	 * 获得url链接
	 * @param url
	 */
	public void addURL(URL url) {
		
		//确保程序开始时url队列为空
		if (getWorkloadWaiting().contains(url))
			return;
		if (getWorkloadError().contains(url))
			return;
		if (getWorkloadProcessed().contains(url))
			return;
		
		log("已添加到数据库中: " + url);
		getWorkloadWaiting().add(url);
		Date now = new java.util.Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateformat.format(now);
		String urlStr = url.toString();
		String sqlstmt = "insert into test.link_info values(" + "0" + ",'"
				+ urlStr + "','" + date + "')";
       dao.saveData(stat, sqlstmt);
	}

	/**
	 * URL内容及解析HTML
	 * @param url
	 */
	public void processURL(URL url) {
		try {
			//调用log方法，控制台动态输出信息
			log("爬取中: " + url);
			//构造URLConnection对象
			/**
			 * html结构
			 * http-equiv="Content-Type" content="text/html; charset=gb2312"
			 */
			URLConnection connection = url.openConnection();
			if ((connection.getContentType() != null)&& !connection.getContentType().toLowerCase().startsWith("text/")) {
				getWorkloadWaiting().remove(url);
				getWorkloadProcessed().add(url);
				log("Not processing because content type is: "
						+ connection.getContentType());
				return;
			}
			//打开输入流
			InputStream is = connection.getInputStream();
			Reader r = new InputStreamReader(is);
			HTMLEditorKit.Parser parse = (new HtmlHandle()).getParser();
			parse.parse(r, new Parser(url), true);
		} catch (IOException e) {
			getWorkloadWaiting().remove(url);
			getWorkloadError().add(url);
			log("错误: " + url);
			report.spiderURLError(url);
			return;
		}
		getWorkloadWaiting().remove(url);
		getWorkloadProcessed().add(url);
		log("完成爬取: " + url);

	}

	/**
	 * 开始爬虫程序
	 */
	public void begin() {
		cancel = false;
		while (!getWorkloadWaiting().isEmpty() && !cancel) {
			Object list[] = getWorkloadWaiting().toArray();
			for (int i = 0; (i < list.length) && !cancel; i++)
				processURL((URL) list[i]);
		}
	}

	/**
	 * 判断链接类型
	 * 并通知spider
	 */
	protected class Parser extends ParserCallback {
		protected URL base;

		public Parser(URL base) {
			this.base = base;
		}

		public void handleStartTag(Tag tag, MutableAttributeSet attribute,
				int pos) {
			String href = (String) attribute.getAttribute(HTML.Attribute.HREF);

			if ((href == null) && (tag == HTML.Tag.FRAME))
				href = (String) attribute.getAttribute(HTML.Attribute.SRC);

			if (href == null)
				return;
			int i = href.indexOf('#');
			if (i != -1)
				href = href.substring(0, i);

			if (href.toLowerCase().startsWith("mailto:")) {
				report.spiderFoundEMail(href);
				return;
			}

			handleLink(base, href);

		}

		protected void handleLink(URL base, String str) {
			try {
				URL url = new URL(base, str);
				if (report.spiderFoundURL(base, url))
					addURL(url);
			} catch (MalformedURLException e) {
				log("Found malformed URL: " + str);
			}
		}
	}

	/**
	 * 控制台输出信息
	 * @param entry
	 *            The information to be written to the log.
	 */
	public void log(String entry) {
		System.out.println((new Date()) + ":" + entry);
	}

}
