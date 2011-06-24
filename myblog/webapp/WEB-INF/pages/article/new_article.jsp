<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MyBlog - 新建文章</title>
</head>
<body>
<hr>
在这里写一篇新文章。
<hr>
<form action="<c:url value="/article/writeArticle.action"/>" method="post">
<label>文章题目</label>
<input type="text" name="article.title" value="${ article.title}"/><br>
<label>分类主题</label>
<input type="text" name="article.subject" />
<label>作者</label>
<input type="text" name="article.author" /><br>
<textarea rows="12" cols="80" name="article.content"></textarea><br>
<input type="submit" value="发布"/>
</form>
<a href="/myblog/index.jsp">要不，回首页？</a>
</body>
</html>