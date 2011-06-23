<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>One Article</title>
</head>
<body>
Details of one article.
<hr>
<h1><c:out value="${article.title }"></c:out></h1>
<b>作者：<c:out value="${article.author }"></c:out></b>
<b>分类：<c:out value="${article.subject }"></c:out></b>
发布时间：
<c:out value="${article.createTime }"></c:out>
<p>
<h3><c:out value="${article.content }"></c:out></h3>
<p>
<hr>
<s:iterator value="commentList">
	[<s:property value="username" />]说：<s:property value="content" />
	<p>
</s:iterator>
<form action="<c:url value="/article/displayOne.action"/>" method="post">
	<input type="hidden" value="${article.id }" name="article.id" />
	<input type="hidden" value="${article.id }" name="comment.articleId" />
	<label>你的大名</label>
	<input type="text" name="comment.username" /><br>
	<label>你的邮箱</label>
	<input type="text" name="comment.email" /><br>
	<label>评论内容</label>
	<textarea rows="10" cols="60" name="comment.content"></textarea><br>
	<input type="submit" value="评论"/>
</form>
<p>
<a href="/myblog/index.jsp">要不，回首页？</a>
</body>
</html>