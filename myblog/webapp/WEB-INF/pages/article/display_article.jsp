<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>My Articles</title>
</head>
<body>
<b>这里展示全部文章的视图。</b>
<hr>
<c:forEach var="a" items="${articleList }">
	<form action="<c:url value="/article/displayOne.action"/>" method="post">
	<td>${a.title}</td><br>
	<label>发布时间：</label>
	<td>${a.createTime}</td><br>
	<label>分类：</label>
	<td>${a.subject }</td>
	<label>发布者：</label>
	<td>${a.author }</td><br>
	<input type="hidden" value="${a.id }" name="article.id" />
	<td>${a.content }</td><br>
	<input type="submit" value="转到详细页面"/>
	</form>
	<hr>
</c:forEach>
<a href="/myblog/index.jsp">要不，回首页？</a>
</body>
</html>