<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="java.io.*"%>
<html>
<%
	String name = (String) request.getSession().getAttribute("username");
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MyBlog</title>
</head>
<body>
<hr>
<b>Welcome to My Blog, and happy to be your luck! </b>
<br>
<hr>
<%
	if (name != null && !name.isEmpty())
		out.println("<b>欢迎，" + name + "!</b><p>");
	else {
		out.println("<b>你还未登录!</b><p>");
	}
%>

<a href="<c:url value="/user/logOnUser.action"/>">这里登录</a>
<br>
<a href="<c:url value="/user/renderCreateUser.action"/>">注册账号</a>
<br>
<a href="<c:url value="/article/displayArticle.action"/>">查看我的文章</a>
<br>
<a href="<c:url value="/user/admin.action"/>">进入管理页面</a>
<br>
</body>
</html>