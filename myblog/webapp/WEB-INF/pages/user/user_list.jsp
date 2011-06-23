<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MyBlog - 创建用户</title>
</head>
<body>
<table>
	<tr>	
		<td>序号</td>
		<td>用户名</td>
		<td>密码</td>
		<td>管理员身份</td>
		<td>创建时间</td>
	</tr>
	<c:forEach var="u" items="${userList }">
		<tr>
			<td>${u.id }</td>
			<td>${u.username }</td>
			<td>${u.password }</td>
			<td>${u.isAdmin }</td>
			<td>${u.createTime}</td>
		</tr>	
	</c:forEach>
</table>
<a href="/myblog/index.jsp">要不，回首页？</a>
</body>
</html>