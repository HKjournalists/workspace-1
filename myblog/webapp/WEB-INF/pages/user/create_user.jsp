<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MyBlog - 创建用户</title>
</head>
<body>
<h4>创建你自己的账户。</h4>
<s:form action="/user/createUser.action">
	<s:textfield name="user.username" label="Username" />
	<s:password name="user.password" label="Password" />
	<s:submit />
</s:form>
<a href="/myblog/index.jsp">要不，回首页？</a>
</body>
</html>