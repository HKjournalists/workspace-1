<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.util.*,com.base.deals.service.*,java.text.*,com.qunar.deals.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df = new DecimalFormat("##.##");  
	if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
		startDate = format.format(new Date());
		endDate = format.format(new Date());
	} 
	List<BookingCheck.BookingCheckReport> units = BookingCheck.getInstance().getBookingCheckReport(startDate, endDate);
	
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

<%@page import="java.net.URLEncoder"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>度假Booking校验报表</title>
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
			<td>日期</td>
			<td>总booking数</td>
			<td>价格变化数</td>
			<td>价格缺失数</td>
			<td>线路下线数</td>
			<td>价格变化比率</td>
			<td>价格缺失比率</td>
			<td>线路下线比率</td>
			<td>booking健康度</td>
		</tr>
		<%
			int tTotal = 0;
			int tPriceChange = 0;
			int tEmptyPrice = 0;
			int tOffline = 0;
			for(BookingCheck.BookingCheckReport unit: units) {
				tTotal += unit.getBookingCount();
				tPriceChange += unit.getPriceChangeCount();
				tEmptyPrice += unit.getEmptyPriceCount();
				tOffline += unit.getOfflineCount();
				%>
					<tr>
						<td><%=unit.getWrapperId() %></td>
						<td><%=wrapperMap.get(unit.getWrapperId()) %></td>
						<td><%=unit.getTimestamp() %></td>
						<td><%=unit.getBookingCount() %></td>
						
						<td><A target="_blank" href="bookingcheckdetail.jsp?wrapperId=<%=unit.getWrapperId()%>&date=<%=format2.format(format.parse(unit.getTimestamp())) %>&otherChange=0"><%=unit.getPriceChangeCount() %></A></td>
						<td><A target="_blank" href="bookingcheckdetail.jsp?wrapperId=<%=unit.getWrapperId()%>&date=<%=format2.format(format.parse(unit.getTimestamp())) %>&otherChange=1"><%=unit.getEmptyPriceCount() %></A></td>
						<td><A target="_blank" href="bookingcheckdetail.jsp?wrapperId=<%=unit.getWrapperId()%>&date=<%=format2.format(format.parse(unit.getTimestamp())) %>&otherChange=2"><%=unit.getOfflineCount() %></A></td>
						<td><%= df.format( (unit.getPriceChangeCount() + 0D) / unit.getBookingCount() )   %></td>
						<td><%= df.format( (unit.getEmptyPriceCount() + 0D) / unit.getBookingCount() ) %></td>
						<td><%= df.format( (unit.getOfflineCount() + 0D) / unit.getBookingCount() ) %></td>
						
						<td><%= df.format( (unit.getBookingCount() - unit.getEmptyPriceCount() - unit.getPriceChangeCount() - unit.getOfflineCount() + 0D) / unit.getBookingCount() )  %></td>
					</tr>
				<% 
			}
		%>
		<tr>
			<td colspan = "3"><b>总计</b></td>
			<td><%=tTotal %></td>
			<td><%=tPriceChange %></td>
			<td><%=tEmptyPrice %></td>
			<td><%=tOffline %></td>
			
			<td><%= df.format( (tPriceChange + 0D) / tTotal )   %></td>
			<td><%= df.format( (tEmptyPrice + 0D) / tTotal ) %></td>
			<td><%= df.format( (tOffline + 0D) / tTotal ) %></td>
			
			<td><%= df.format( (tTotal - tPriceChange - tEmptyPrice - tOffline + 0D) / tTotal )  %></td>
			
		</tr>
	</table>
</body>
</html>