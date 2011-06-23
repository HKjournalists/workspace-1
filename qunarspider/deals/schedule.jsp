<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	Map<String,AbstractDealsParse> wrapperMap = new TreeMap();
	Map<String,AbstractDealsParse> offlineMap = new TreeMap();
	ResourceBundle resource = ResourceBundle.getBundle("file");
	String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	List<String> validWrapper = ScheduleManager.getInstance().getValidWrapper();
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
					if (!validWrapper.contains(wrapperDir.getName())) {
						offlineMap.put(wrapperId, parse);
					} else {
						wrapperMap.put(wrapperId, parse);	
					}
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	ScheduleManager manager = ScheduleManager.getInstance();
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>度假wrapper调度</title>
<script type='text/javascript' src='dwr/interface/Schedule.js'></script>
<script type='text/javascript' src='dwr/engine.js'></script>
<script type='text/javascript' src='dwr/util.js'></script>
<script>
	function schedulecrawler(wrapperId,type) {
		Schedule.scheduleCrawler(wrapperId, type,function(result){
				if (''==result) {
					alert("调度成功!");
				} else {
					alert(result);
				}
				location.reload();
		});
	}

	function offlinecrawler(wrapperId) {
		Schedule.offlineCrawler(wrapperId,function(result){
				if (''==result) {
					alert("下线成功!");
				} else {
					alert(result);
				}
				location.reload();
		});
	}

	function onlinecrawler(wrapperId) {
		Schedule.onlineCrawler(wrapperId,function(result){
				if (''==result) {
					alert("上线成功!");
				} else {
					alert(result);
				}
				location.reload();
		});
	}

	function setduration(wrapperId, type) {
		var obj = document.getElementById(type+"duration_" + wrapperId);
		Schedule.setDuration(wrapperId, type, obj.value, function(result) {
			if (''==result) {
				alert("设置成功!");
			} else {
				alert(result);
			}
			location.reload();
		});
	}
</script>
</head>
<body>
	<table align="center" width="90%" border="1">
		<td align="left">WrapperId</td>
		<td align="left">WrapperName</td>
		<td align="left">抓取周期(分)</td>
		<td align="left">Recheck周期(分)</td>
		<td align="left">状态</td>
		<td align="left">调度类型</td>
		<td align="left">开始时间</td>
		<td align="left">结束时间</td>
		<td align="left">操作</td>
		<td align="left">操作</td>
		<td align="left">操作</td>
		<td align="left">操作</td>
		<%
			String html = "";
			for(String wrapperId : wrapperMap.keySet()) {
				AbstractDealsParse parse = wrapperMap.get(wrapperId);
				%>
					<tr>
						<td align="left"><%=wrapperId%></td>
						<td align="left"><%=parse.getWrapperName()%></td>
						<td align="left"><input style="width:60%" type="text" id="crawlerduration_<%=wrapperId %>" value="<%=manager.getCrawlerDuration(wrapperId)%>" /><input type="button" value="设置" onclick="setduration('<%=wrapperId%>','crawler')" /></td>
						<td align="left"><input style="width:60%" type="text" id="recheckduration_<%=wrapperId %>" value="<%=manager.getRecheckDuration(wrapperId)%>" /><input type="button" value="设置" onclick="setduration('<%=wrapperId%>','recheck')" /></td>
						<td align="left"><%=manager.getStatus(wrapperId)%></td>
						<td align="left"><%=manager.getType(wrapperId)%></td>
						<td align="left"><%=manager.getStartTime(wrapperId)%></td>
						<td align="left"><%=manager.getEndTime(wrapperId)%></td>
						<td align="left"><input type="button" value="抓取" onclick="schedulecrawler('<%=wrapperId%>', 'crawler')"/></td>
						<td align="left"><input type="button" value="Recheck" onclick="schedulecrawler('<%=wrapperId%>', 'recheck')"/></td>
						<td align="left"><input type="button" value="下线" onclick="offlinecrawler('<%=wrapperId%>')"/></td>
						<td align="left"><input type="button" value="上线" onclick="onlinecrawler('<%=wrapperId%>')"/></td>
					</tr>
				<%
				
			}
			
			for(String wrapperId : offlineMap.keySet()) {
				AbstractDealsParse parse = offlineMap.get(wrapperId);
				%>
					<tr>
						<td align="left"><%=wrapperId%></td>
						<td align="left"><%=parse.getWrapperName()%></td>
						<td align="left"><input style="width:60%" type="text" id="crawlerduration_<%=wrapperId %>" value="<%=manager.getCrawlerDuration(wrapperId)%>" /><input type="button" value="设置" onclick="setduration('<%=wrapperId%>','crawler')" /></td>
						<td align="left"><input style="width:60%" type="text" id="recheckduration_<%=wrapperId %>" value="<%=manager.getRecheckDuration(wrapperId)%>" /><input type="button" value="设置" onclick="setduration('<%=wrapperId%>','recheck')" /></td>
						<td align="left"><%=manager.getStatus(wrapperId)%></td>
						<td align="left"><%=manager.getType(wrapperId)%></td>
						<td align="left"><%=manager.getStartTime(wrapperId)%></td>
						<td align="left"><%=manager.getEndTime(wrapperId)%></td>
						<td align="left"><input type="button" value="抓取" onclick="schedulecrawler('<%=wrapperId%>', 'crawler')"/></td>
						<td align="left"><input type="button" value="Recheck" onclick="schedulecrawler('<%=wrapperId%>', 'recheck')"/></td>
						<td align="left"><input type="button" value="下线" onclick="offlinecrawler('<%=wrapperId%>')"/></td>
						<td align="left"><input type="button" value="上线" onclick="onlinecrawler('<%=wrapperId%>')"/></td>
					</tr>
				<%
				
			}
		%>
	</table>
</body>
</html>