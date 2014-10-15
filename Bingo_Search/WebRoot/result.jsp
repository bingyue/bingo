<%@ page language="java" pageEncoding="GBK"
	contentType="text/html;charset=gbk"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.lucene.document.Document"%>
<%@ page import="org.apache.lucene.analysis.Analyzer"%>
<%@ page import="org.apache.lucene.search.highlight.Highlighter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link type="text/css" rel="stylesheet" href="./css/result.css" />
		<title>缤果搜索</title>
<script type="text/javascript">
function clrSrcMsg() {
	var srcMsg = document.getElementById("account");
	if(srcMsg != null) {
		srcMsg.value = "";
	}
}
</script>
	</head>
	<%  int totalNum = 0;
		int numPerPage = 10;
		int maxPageNum = 0;
		int curPage = 1;
		int startLocation = 0;

		List<Document> list = new ArrayList<Document>();
		list = (ArrayList<Document>) request.getAttribute("docList");
		totalNum = ((Integer) request.getAttribute("totalCount"))
				.intValue();

		String keyWords = (String) request.getAttribute("keyWords");
		totalNum = list.size();
		maxPageNum = ((totalNum % numPerPage) == 0) ? totalNum / numPerPage
				: (totalNum / numPerPage + 1);
		curPage = startLocation / numPerPage + 1;
		Analyzer analyzer = (Analyzer) request.getAttribute("analyzer");
		Highlighter highlighter = (Highlighter) request
				.getAttribute("highlighter");
	%>

	<%
		if (request.getParameter("startLocation") != null) {
			startLocation = Integer.parseInt(request
					.getParameter("startLocation")); //取当前页码
			System.out.println("startLocation =" + startLocation);
			curPage = ((startLocation % numPerPage) == 0) ? (startLocation / numPerPage): (startLocation / numPerPage + 1);
			curPage = curPage + 1;
			System.out.println("curPage =" + curPage);
		}%>
	<body>
		<div id="div1">
			<div class="div12">
				<form name="searchForm" action="servlet/SearchServlet" method="post">
					<table border="0">
						<tr>
							<td>
								<input id="account" class="s_input" type="text" size="70" name="keyWords"
									value="<%=keyWords%>" onfocus="clrSrcMsg()" />
							</td>
							<td>
								<input id="button" class="s_btn" type="submit" value="缤果搜索" />
							</td>
						</tr>
						<tr>
							<td>
								<font color="#000000" size="-1">找到约</font>
								<font color="red" size="-1"> <%
 	out.print(totalNum);
 %> </font><font color="#000000" size="-1">个相关网页</font>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<div id="div2">
			<div class="div22">
				<%
					if (curPage < maxPageNum) {
						for (int i = startLocation; i < startLocation + numPerPage; i++) {
				%>
				<div class="div">
					<table border="0">
						<tr>
							<td>
								<a class="a1" href="<%out.print(list.get(i).get("link")); %>">
									<%
										String titleStr = highlighter.getBestFragment(analyzer,
														"title", list.get(i).get("title").replaceAll(
																".text", ""));
												out.print("<font size=" + "+1"+ "color=" + "#0000CC" + ">");
												if (titleStr != null) {
													out.print(titleStr);
												} else {
													out.print(list.get(i).get("title").replaceAll(".text",
															""));
												}
												out.print("</font>");
									%> </a>
								<br />
								<%
									String bodyStr = highlighter.getBestFragment(analyzer,
													"content", list.get(i).get("content"));
											out.print("<font size=" + "-1" + ">");
											if (bodyStr != null) {
												out.print(bodyStr.replaceAll(" ","") + "..." + "<br/>");
											} else {
												out.print(list.get(i).get("content").replaceAll(" ",""));
											}
											out.print("</font>");
								%>
								<br/><font color="#008000"> <%
 	if (list.get(i).get("link").length() > 35)
 				out.print(list.get(i).get("link").substring(0, 35));
 			else
 				out.print(list.get(i).get("link"));
 				%></font>
 				<font size = "-1" color="black">
 				<%
 				out.print("-<a href=" + "#" + ">" + "网页快照" + "</a>");
 %> </font>
							</td>
						</tr>
					</table>
				</div>
				<%
					}
					} else {
						for (int i = startLocation; i < totalNum; i++) {
				%>
				<div class="div">
					<table border="0">
						<tr>
							<td>
								<a class="a1" href="<%out.print(list.get(i).get("link")); %>">
									<%
										String titleStr = highlighter.getBestFragment(analyzer,
														"title", list.get(i).get("title").replaceAll(
																".text", ""));
												out.print("<font color=" + "blue" + ">");

												if (titleStr != null) {
													out.print(titleStr);
												} else {
													out.print(list.get(i).get("title").replaceAll(".text",
															""));
												}
												out.print("</font>");
									%> </a>
								<br />
								<%
									String bodyStr = highlighter.getBestFragment(analyzer,
													"content", list.get(i).get("content"));
											if (bodyStr != null) {
												out.print(bodyStr + "..." + "<br/>");
											}
								%>
								<font color="#3C8B1D"> <%
 	out.print(list.get(i).get("link"));
 %> </font>
							</td>
						</tr>
					</table>
				</div>
				<%
					}
					}
				%>
				<div class="div23">
					<form method="post">
						<table>
							<tr>
								<td><%if (curPage > 1) {
											out.println("<a href='SearchServlet?startLocation="
															+ (startLocation - numPerPage)
															+ "&&keyWords="
															+ keyWords);
											out.println("'>上一页</a>");}
										if (maxPageNum > 10) {
											int totalNumShow = curPage + 2;
											int startLocationPage = 0;
											if (totalNumShow > maxPageNum) {
												totalNumShow = maxPageNum;
											}
											if (maxPageNum > 11 && curPage > 11) {
												startLocationPage = curPage - 11;
											}
											for (int i = startLocationPage; i < totalNumShow; i++) {
												if (i + 1 == curPage) {
									%>&nbsp;[<%=i + 1%>]&nbsp;<%
										} else {
									%><a
										href="SearchServlet?startLocation=<%=i * numPerPage%>&&keyWords=<%=keyWords%>">&nbsp;<%=i + 1%>&nbsp;</a>
									<%
										}
											}
										} else {
											for (int i=0;i<maxPageNum && maxPageNum>1;i++) {
												if (i + 1 == curPage) {
									%>&nbsp;[<%=i + 1%>]&nbsp;<%
										} else {
									%><a
										href="SearchServlet?startLocation=<%=i * numPerPage%>&&keyWords=<%=keyWords%>">&nbsp;<%=i + 1%>&nbsp;</a>
									<%
										}	}
										}
										if (curPage < maxPageNum) {
											out.println("<a href='SearchServlet?startLocation="+ (startLocation + numPerPage)+ "&&keyWords="+ keyWords);
											out.println("'>下一页</a>");
										}
									%>
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>

	</body>
</html>
