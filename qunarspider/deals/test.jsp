
<%@page import="com.base.test.TestClient"%><%@ page language="java" import="common.Logger,org.apache.commons.logging.Log,org.apache.commons.logging.LogFactory" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>

<html>
<head>
</head>

<body>
<%
	String url = request.getParameter("url");
	if (url == null) {
%>
<form method="post" action="">
host:	<input type="text" name="host" /> <br/>
ip:	<input type="text" name="ip" /> <br/>
url: <input type="text" name="url" />
<input type="submit" />
</form>

<%
	}
	else {
		String host = request.getParameter("host");
		String ip = request.getParameter("ip");
		TestClient client = null;
		if (host != null && ip != null && !host.isEmpty() && !ip.isEmpty()) {
			client = new TestClient(host, Integer.parseInt(ip));
		} else {
			client = new TestClient();
		}
		out.println(client.getHtml(url, "gbk"));
	}
%>
</body>
</html>