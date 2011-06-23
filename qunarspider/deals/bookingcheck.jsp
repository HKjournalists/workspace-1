<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.*"
%>

<%
	
	String id = request.getParameter("id");

	if (id != null && id.matches("\\d+")) {
		BookingCheck.getInstance().addId(Integer.parseInt(id));
	}
%>
