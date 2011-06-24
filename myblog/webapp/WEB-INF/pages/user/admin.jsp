<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MyBlog - 管理页面</title>
</head>
<body>
只有管理员才能到这里。
<hr>
<a href="<c:url value="/user/listUser.action"/>">查看用户列表</a><br>
<a href="<c:url value="/article/renderToCreateArticle.action"/>">写一篇博文</a><br>
</body>
</html>