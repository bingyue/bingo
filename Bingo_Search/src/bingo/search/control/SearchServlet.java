package bingo.search.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

/**
 * 提供搜索服务的Servlet
 * @author BingYue
 *
 */
public class SearchServlet extends HttpServlet {

	/**
	 * 默认的序列化ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 初始化方法
	 */
	public void init() throws ServletException{
		
	}
	
	/**
	 * Post方法
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		        
		        //重新定义请求和响应的编码,对中文进行正确的解码
				request.setCharacterEncoding("gbk");
				response.setContentType("text/html;charset=gbk");
				
				//设置默认的信息检索库
		        String searchPath = "D:\\Bingo_Index";
		        
		        //获取用户提取的欲搜索的关键字
				String keyWords = (String) request.getParameter("keyWords");
				//ServletContext实例
				ServletContext sc = getServletContext();
				/**
				 * 关键词为空，转到error页面
				 * 或者也可以添加javascript valadition验证
				 */
				if(keyWords==null){
					RequestDispatcher rd = sc.getRequestDispatcher("/error.jsp");
					rd.forward(request, response);
					return;
				}
				
				//对用户搜索的类别进行判断
				//针对不同的查询类型，去不同的目录下检索
				String searchType = request.getParameter("searchType");
				if(!searchType.equals("webPage") && (searchType != null)) {
					searchPath = SearchType.getSearchType(searchType);
				}
				
				//分页操作
				String startLocationTemp = request.getParameter("startLocation");
				if(startLocationTemp != null) {
			    //parseInt 返回整数
				int startLocation = Integer.parseInt(startLocationTemp);
				System.out.println(startLocation);
				}
		        
				//分词器
				Analyzer analyzer = null;
				//页面高亮
				Highlighter highlighter = null;
				/**
				 * Document是Lucene中重要的类
				 */
				List<Document> list = new ArrayList<Document>();
				Integer totalCount = null;

				try {
					IndexSearcher indexSearch = new IndexSearcher(searchPath);
					analyzer = new MMAnalyzer();
					String[] field = { "title", "content", "link"};
					//
					Map<String, Float> boosts = new HashMap<String, Float>();
					boosts.put("title", 3f);
					QueryParser queryParser = new MultiFieldQueryParser(field,
							analyzer, boosts);
					Filter filter = null;
					try {
						/**
						 * 以下是具体的查询实现
						 * queryParser是Lucene主要的查询类
						 */
						Query query = queryParser.parse(keyWords);
						TopDocs topDocs = indexSearch.search(query, filter, 1000,
								new Sort(new SortField("size")));
						totalCount = new Integer(topDocs.totalHits);
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
								"<font color='red'>", "</font>");
						//高亮操作
						highlighter = new Highlighter(simpleHTMLFormatter,
								new QueryScorer(query));
						
						highlighter.setTextFragmenter(new SimpleFragmenter(70));
						for (int i = 0; i < topDocs.totalHits; i++) {
							ScoreDoc scoreDoc = topDocs.scoreDocs[i];
							int docSn = scoreDoc.doc; // 文档内部编号
							Document doc = indexSearch.doc(docSn); // 根据编号取出相应的文档
							list.add(doc);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				RequestDispatcher rd = sc.getRequestDispatcher("/result.jsp");
				request.setAttribute("keyWords", keyWords);
				request.setAttribute("totalCount", totalCount);
				request.setAttribute("docList", list);
				request.setAttribute("analyzer", analyzer);
				request.setAttribute("highlighter", highlighter);
				/**
				 * RequestDispatcher提供两个方法，forward和include
				 */
				rd.forward(request, response);
	}
	/**
	 * Get方法
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				this.doPost(request, response);
			}
	
	/**
	 * 销毁方法
	 */
	public void destroy() {
		super.destroy();
	}


}
