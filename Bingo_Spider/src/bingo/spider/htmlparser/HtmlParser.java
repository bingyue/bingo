package bingo.spider.htmlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bingo.spider.file.ContentSave;


/**
 * 
 * 读取url，解析html网页内容,提取有效信息
 * @author BingYue
 *
 */
public class HtmlParser {

	/**
	 * 读取指定url的网页内容
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getWebPage(String urlStr) throws IOException {
		URL url;
		String temp;
		String charset;
		final StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlStr);
			charset = Decoder.parseByResHeader(urlStr);
			//如果网页未声明编码格式，默认采用utf-8
			if(charset== null){
				charset="utf-8";
			}
			final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (MalformedURLException e) {
			ContentSave.SaveLog(urlStr, "MalformedURLException");
		} catch (final IOException e) {
			ContentSave.SaveLog(urlStr, "IOException");
		}
		return sb.toString();

	}
	
	/**
	 * 使用正则表达式从htmlContent中解析出网页<title></title>标签对中的内容
	 * @param htmlCotent
	 * @return
	 */
	public static String getTitle(final String htmlCotent) {
		String regex_title;
		String title = "标题为空页面";
		regex_title = "<[Tt][Ii][Tt][Ll][Ee]>(.*?)</[Tt][Ii][Tt][Ll][Ee]>";
		final Pattern pat = Pattern.compile(regex_title, Pattern.CANON_EQ);
		final Matcher mat = pat.matcher(htmlCotent);
		while (mat.find()) {
			title = mat.group(1);
		}
		//取得标题内容后进一步去除无用信息
		return removeTitleTag(title);
	}
	/**
	 * 这里使用<meta></meta>查找关键词
	 * 为了方便网页被搜索引擎抓取并正确分类，通常网页的<meta></meta>会添加以下关键信息
	 * 以下来自w3c:
	 * <meta> 元素可提供有关某个 HTML 元素的元信息 (meta-information)，比如描述、针对搜索引擎的关键词以及刷新频率
	 * @param htmlContent
	 * @return
	 */
	public static String getKeywords(final String htmlContent) {
		String keywords = "null";
		String regex_keywords = "<meta name=[\"]{0,1}[Kk]eywords[\"]{0,1} content=[\"]{0,1}(.*?)[ ]{0,1}[\"]{0,1}[ ]{0,1}[/]{0,1}[ ]{0,1}>";

		Pattern p_keywords = Pattern.compile(regex_keywords);
		Matcher m_keywords = p_keywords.matcher(htmlContent);
		while (m_keywords.find()) {
			keywords = m_keywords.group(1);
		}
		return keywords;
	}
	/**
	 * 
	 * @param htmlContent
	 * @return
	 */
	public static String getDes(final String htmlContent) {
		String des = null;
		String regex_des = "<meta name=[\"]{0,1}[Dd]escription[\"]{0,1} content=[\"]{0,1}(.*?)[ ]{0,1}[\"]{0,1}[ ]{0,1}[/]{0,1}[ ]{0,1}>";
		Pattern p_des = Pattern.compile(regex_des, Pattern.DOTALL);
		Matcher m_des = p_des.matcher(htmlContent);
		while (m_des.find()) {
			des = m_des.group(1);
		}
		if (des == null) {
			des = HtmlParser.getContent(htmlContent);
		}
		return des;
	}
	/**
	 * 
	 * @param htmlContent
	 * @return
	 */
	public static String getContent(final String htmlContent) {
		String regex;
		String body = "";
		final StringBuffer sb = new StringBuffer();
		regex = "<body.*?</body>";

		final Pattern pat = Pattern.compile(regex, Pattern.DOTALL);
		final Matcher mat = pat.matcher(htmlContent);
		while (mat.find()) {
			sb.append(mat.group());
		}

		body = sb.toString();
		return removePageTag(body);
	}
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public static String removeTitleTag(final String title) {
		
		return title.replaceAll("\\s|\\|\\/|\\｜|:|\\*|\\?", "");
	}
	/**
	 * 
	 * @param htmlCode
	 * @return
	 */
	public static String removePageTag(final String htmlCode) {
		
		return htmlCode.replaceAll(
				"<.*?>|\\pP|\\pS|[a-zA-z0-9]| {10,}|[\\s\\p{Zs}]", "");
		
	}
}
