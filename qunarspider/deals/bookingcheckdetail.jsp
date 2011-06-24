<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.util.*,com.base.deals.service.*,java.text.*,com.qunar.deals.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	String wId = request.getParameter("wrapperId");
	String date = request.getParameter("date");
	String otherChange = request.getParameter("otherChange");
	if (otherChange == null) {
		otherChange = "0";
	} else if ("1".equals(otherChange)) {
		otherChange = "无法获得价格";
	} else if ("2".equals(otherChange)) {
		otherChange = "线路下线";
	} else if ("0".equals(otherChange)) {
		otherChange = "";
	}
	String pageIndex = request.getParameter("pageIndex");
	Integer iPageIndex = 1;
	if (pageIndex != null && !pageIndex.isEmpty()) {
		iPageIndex = Integer.parseInt(pageIndex);
	}
	int pageCount = 3;
	int start = (iPageIndex - 1) * pageCount;
	List<BookingCheck.BookingCheckUnit> units = new ArrayList();
	int total = BookingCheck.getInstance().getBookingCheckUnits(wId,date,start,pageCount,units,otherChange);
	
	
	Map<String,String> wrapperMap = new TreeMap<String, String>();
	ResourceBundle resource = ResourceBundle.getBundle("file");
	String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	ManageClassLoader cl = ManageClassLoader.getInstance();
	File f = new File(base);
	if (f.exists() && f.isDirectory()) {
		for(File wrapperDir : f.listFiles()) {
			if (wrapperDir.isDirectory()) {
				String wrapperId = wrapperDir.getName();
				File cfg = new File(base + wrapperId + File.separator + "wrapper.n3");
				String parser = LoaderUtil.getParser(cfg);
				if (parser == null) continue; 
				AbstractDealsParse parse;
				try {
					parse = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
					wrapperId = parse.getWrapperId();
					wrapperMap.put(wrapperId, parse.getWrapperName());		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>度假Booking校验报表 <%=otherChange %></title>
</head>
<body>
	<table align="center" width="95%" border="1" >
		<tr>
			<td>线路ID</td>
			<td>wrapperId</td>
			<td>wrapperName</td>
			<td>变化时间</td>
			<td>标题</td>
			<td>原价格</td>
			<td>变化后价格</td>
			<td>变化类别</td>
		</tr>
		<%
			for(BookingCheck.BookingCheckUnit unit: units) {
				%>
					<tr>
						<td><%=unit.getId() %></td>
						<td><%=unit.getWrapperId() %></td>
						<td><%=wrapperMap.get(unit.getWrapperId()) %></td>
						<td><%=unit.getChangeTime() %></td>
						<td><A href="<%=unit.getSourceUrl()%>" target="_blank" ><%=unit.getTitle() %></A></td>
						<td><%=unit.getPrice() %></td>
						<td><%=unit.getvPrice() %></td>
						<td><%=unit.getOtherChange() %></td>
					</tr>
				<% 
			}
		%>
		<tr>
			<td align="right" colspan="8">
				<%
					int totalPage = (int)Math.ceil( (total + 0D) / pageCount);
					String pageString = "共" + totalPage + "页&nbsp;";
					for(int i = 1; i <= totalPage; i++) {
						pageString += "&nbsp;<A href=\"bookingcheckdetail.jsp?wrapperId=" + wId + "&date=" + date + "&otherChange=" + otherChange + "&pageIndex=" + i + "\">&nbsp;" + i + "&nbsp;</A>&nbsp;";
					}
					out.println(pageString);
				%>
			</td>
		</tr>
	</table>
</body>
</html>