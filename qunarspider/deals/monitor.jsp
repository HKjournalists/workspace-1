<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.util.*,com.base.deals.service.*,java.text.SimpleDateFormat"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
		startDate = format.format(new Date());
		endDate = format.format(new Date());
	}
	List<MonitorUnit> units = Monitor.getInstance().getMonitorUnits(startDate, endDate);
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>度假wrapper监控</title>
</head>
<body>
	<form action="" method="post">
		开始时间(yyyy-MM-dd):<input type="text" name="startDate" value="<%=startDate %>" /><br/>
		结束时间(yyyy-MM-dd):<input type="text" name="endDate" value="<%=endDate%>" /><br/>
		<input type="submit"/>
	</form>
	<table align="center" width="95%" border="1" >
		<tr>
			<td>wrapperId</td>
			<td>wrapperName</td>
			<td>开始时间</td>
			<td>结束时间</td>
			<td>耗时(秒)</td>
			<td>获得数</td>
		</tr>
		<%
			for(MonitorUnit unit : units) {
				%>
					<tr>
						<td><%=unit.getWrapperId() %></td>
						<td><%=unit.getWrapperName() %></td>
						<td><%=unit.getStartTime() %></td>
						<td><%=unit.getEndTime() %></td>
						<td><%=unit.getDuration() %></td>
						<td><%=unit.getTotal()==0?"<font color='red'>0</font>":unit.getTotal()%></td>
					</tr>
				<% 
			}
		%>
	</table>
</body>
</html>