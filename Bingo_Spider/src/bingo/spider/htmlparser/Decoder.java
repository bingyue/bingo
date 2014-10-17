package bingo.spider.htmlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取url地址内容并且解码
 * @author BingYue
 *
 */
public class Decoder {

	/**
	 * 根据HTTP协议的响应头字段Content-Type获取网页编码
	 * @param urlStr
	 * @return
	 */
	public static String parseByResHeader(String urlStr) {
		
		String charset = null;
		//获取网页编码的正则表达式
		String regex_charset = "text/html;[ ]{0,1}[Cc]harset=(.*)";
		//Java网络编程基础
		URL url;
		try {
			url = new URL(urlStr);
			HttpURLConnection httpURLConn = (HttpURLConnection) url.openConnection();
			charset = httpURLConn.getContentType();
			System.out.println(charset);
			if (charset != null) {
				if (!charset.equals("text/html")) {
					Pattern p_charset = Pattern.compile(regex_charset,
							Pattern.DOTALL);
					Matcher m_charset = p_charset.matcher(charset);
					while (m_charset.find()) {
						charset = m_charset.group(1).toLowerCase();
					}
				} else {
					return Decoder.parseByContent(urlStr);
				}
			} else {
				return Decoder.parseByContent(urlStr);
			}
	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据HTML<meta></meta>标签,对http-equive值为Content-Type的行解析网页编码
	 * @param urlStr 网页的URL
	 * @return 网页使用的字符编码 如utf-8 gbk等
	 */
	public static String parseByContent(String urlStr) {
		
		String temp;
		String charset = null;
		String regex_charset = "<meta http-equiv=\"content-type\" content=\"text/html;[ ]{0,1}charset=(.*?)\"[ ]{0,1}[/]{0,1}>";
		String regex_charset_spc = "<meta content=\"text/html;[ ]{0,1}charset=(.*)\" http-equiv=\"content-type\"[ ]{0,1}[/]{0,1}>";
		try {
			URL url = new URL(urlStr);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((temp = br.readLine()) != null) {
				temp = temp.toLowerCase();
				Pattern p_charset = Pattern.compile(regex_charset);
				Matcher m_charset = p_charset.matcher(temp);
				if (m_charset.find()) {
					System.out.println("content-type" + temp);
					charset = m_charset.group(1);
					break;
				}

				Pattern p_charset_spc = Pattern.compile(regex_charset_spc);
				Matcher m_charset_spc = p_charset_spc.matcher(temp);
				if (m_charset_spc.find()) {
					System.out.println(2);
					charset = m_charset_spc.group(1);
					break;
				}
			}
			if (charset == null) {
				charset = "utf-8";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return charset;
	}
		
}
