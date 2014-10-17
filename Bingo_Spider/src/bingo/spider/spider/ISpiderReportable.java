package bingo.spider.spider;

import java.net.URL;
/**
 * 爬虫接口，在Main类中实现
 * @author BingYue
 *
 */
public interface ISpiderReportable {

	/**
	 * 程序定位一个url时被调用，如果返回true
	 * 则继续执行下去并继续查找链接
	 */
	public boolean spiderFoundURL(URL base, URL url);
	/**
	 * 链接失效的情形
	 * 如服务器未响应
	 * @param url
	 */
	public void spiderURLError(URL url);
	
	/**
	 * 发现邮件地址
	 * @param email
	 */
	public void spiderFoundEMail(String email);
}
