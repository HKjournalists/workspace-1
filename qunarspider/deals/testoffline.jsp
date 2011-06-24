<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	Map<String,String> wrapperMap = new HashMap<String, String>();
	Map<String, AbstractDealsParse> parseMap = new HashMap<String, AbstractDealsParse>();
	ResourceBundle resource = ResourceBundle.getBundle("file");
	String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	String noUseParseRoutes = ResourceBundle.getBundle("nouseclass").getString("no_use_extract_class");
	String[] noRoutes = noUseParseRoutes.split(",");
	Set<String> noUseParseRoutesSet = new HashSet();
	for(String s : noRoutes) {
		noUseParseRoutesSet.add(s);
	}
	ManageClassLoader cl = ManageClassLoader.getInstance();
	File f = new File(base);
	if (f.exists() && f.isDirectory()) {
		for(File wrapperDir : f.listFiles()) {
			if (noUseParseRoutesSet.contains(wrapperDir.getName())) continue;
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
					parseMap.put(wrapperId, parse);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	Map<Integer, String> typeMap = new HashMap();
	typeMap.put(0, "国内游");
	typeMap.put(1, "出境游");
	typeMap.put(2, "周边游");
	typeMap.put(3, "港澳游");
	
	Map<Integer, String> functionMap = new HashMap();
	functionMap.put(0, "跟团游");
	functionMap.put(1, "自由行");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>测试下线逻辑</title>
</head>
<body>
<form method="post">
	wrapper:<select id="wrapperSelect" name="wrapperId" style="width:200px">
		<option value="-1">请选择wrapper</option>
		<%
			for(String key : wrapperMap.keySet()) {
				out.println("<option value=\"" + key + "\" >" + wrapperMap.get(key) + "</option>");
			}
		%>
	</select>
	<br/>
	url:<input type="text" style="width:200px" id="url" name="url" />
	<br/>
	类别:<select style="width:200px" id="type" name="type" >
			<option value="0">国内游</option>
			<option value="1">出境游</option>
			<option value="2">周边游</option>
			<option value="3">港澳游</option>
		</select>
	<br/>
	方式:<select style="width:200px" id="function" name="function" >
			<option value="0">跟团游</option>
			<option value="1">自由行</option>
		</select>
	otherINfo:<input type="text" id="otherinfo" name="otherinfo" style="width:200px"/>
	<input type="submit"  />
	<br/>
</form>
<%
	String typeKey = request.getParameter("type");
	if (typeKey != null) {
		String functionKey = request.getParameter("function");
		String type = typeMap.get(Integer.parseInt(typeKey));
		String function = functionMap.get(Integer.parseInt(functionKey));
		String wrapperId = request.getParameter("wrapperId");
		String url = request.getParameter("url");
		String otherinfo = request.getParameter("otherinfo");
		out.println(parseMap.get(wrapperId).testOffline(url,type,function, otherinfo));
	}
%>
</body>
</html>