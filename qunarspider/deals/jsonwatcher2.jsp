<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.*,com.qunar.deals.parse.*,java.io.*,com.base.classloader.*,com.qunar.deals.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	Map<String,String> wrapperMap = new TreeMap<String, String>();
	ResourceBundle resource = ResourceBundle.getBundle("file");
	String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	List<String> validWrapper = ScheduleManager.getInstance().getValidWrapper();
	ManageClassLoader cl = ManageClassLoader.getInstance();
	File f = new File(base);
	if (f.exists() && f.isDirectory()) {
		for(File wrapperDir : f.listFiles()) {
			if (!validWrapper.contains(wrapperDir.getName())) continue;
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
<title>查看线路JSON文件</title>
<script type='text/javascript' src='dwr/interface/JsonParse.js'></script>
<script type='text/javascript' src='dwr/engine.js'></script>
<script type='text/javascript' src='dwr/util.js'></script>

<script>
	var data;
	function bookingcheck(wrapperId, cUrl, obj) {
		obj.innerHTML = '正在解析。。。';
		JsonParse.bookingCheckResult(wrapperId, cUrl, function(result) {
			obj.innerHTML = result;
		});
	}
	function selectwrapper(v) {		
		var wrapperDateSelect = document.getElementById("wrapperDateSelect");		
		wrapperDateSelect.options.length = 0;
		var opt = new Option("请选择日期", "-1");
		wrapperDateSelect.options.add(opt);
		if (v == -1) return;
		JsonParse.getDateList(v, function(result) {
			for(var i = 0; i < result.length; i++) {
				opt = new Option(result[i], result[i]);
				wrapperDateSelect.options.add(opt);
			}
		});
	}
	function selectdate(v) {
		var wrapperSelect = document.getElementById("wrapperSelect");	
		var v2 = wrapperSelect.value;

		var jsonFileSelect = document.getElementById("jsonFileSelect");
		jsonFileSelect.options.length = 0;
		var opt = new Option("请选择文件", "-1");
		jsonFileSelect.options.add(opt);
		JsonParse.getFileList(v2, v, function(result){
			for(var i = 0; i < result.length; i++){
				opt = new Option(result[i], result[i]);
				jsonFileSelect.options.add(opt);
			}
		});
	}
	function offlinecheck(index, obj) {
		obj.innerHTML = '正在解析。。。';
		var d = data[index];
		JsonParse.offlineCheckResult(d.oOtherInfo, d.oFunction, d.oType, d.oUrl,  d.url, d.wrapperId, function(result) {
			obj.innerHTML = result;
		});
	}
	function doDetail(index) {
		var td = document.getElementById("td" + index);
		var d = data[index];
		var html = "<table width=\"95%\" align=\"center\">";
		html += "<tr>";
		html += "<td width=\"10%\"><b>标题</b></td>";
		if (d.miscellaneous.length != 0)
			html += "<td>" + d.title + "</td>";
		else html += "<td><font color='red'><b>" + d.title + "</b></font></td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>价格</b></td>";
		html += "<td>" + d.price + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>币种</b></td>";
		html += "<td>" + d.currency + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>线路图片</b></td>";
		html += "<td><img src=\"" + d.route_snapShot + "\" /></td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td ><b>星级</b></td>";
		html += "<td>" + d.starGrade + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>交通方式</b></td>";
		html += "<td>" + d.traffic + "</td>";
		html += "</tr>";

		if (d.ship != null) {
			html += "<tr>";
			html += "<td ><b><font color='red'>出发港口</font></b></td>";
			html += "<td>" + d.ship.fromHub + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td ><b><font color='red'>到达港口</font></b></td>";
			html += "<td>" + d.ship.toHub + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td ><b><font color='red'>邮轮名称</font></b></td>";
			html += "<td>" + d.ship.shipName + "</td>";
			html += "</tr>";


			html += "<tr>";
			html += "<td ><b><font color='red'>邮轮公司</font></b></td>";
			html += "<td>" + d.ship.company + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td ><b><font color='red'>航线区域</font></b></td>";
			html += "<td>" + d.ship.voyageRegion + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td ><b><font color='red'>途经城市</font></b></td>";
			html += "<td>" + d.ship.cities + "</td>";
			html += "</tr>";

			var dates = '';
			for(o in d.ship.dates) {
				dates += d.ship.dates[o] +",";
			}
			html += "<tr>";
			html += "<td ><b><font color='red'>出发日期</font></b></td>";
			html += "<td>" + dates + "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<td ><b><font color='red'>航线图片</font></b></td>";
			html += "<td><img src='" + d.ship.voyageImg + "' /></td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td ><b><font color='red'>价格表</font></b></td>";
			html += "<td>" + d.ship.priceHtml + "</td>";
			html += "</tr>";
			
		} 
		html += "<tr>";
		html += "<td ><b>线路主题</b></td>";
		var subject = d.subject;
		var subjectstr = "";
		for(i = 0; i < subject.length; i++) {
			subjectstr += subject[i] + "&nbsp;"
		}
		html += "<td>" + subjectstr + "</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td ><b>线路特色</b></td>";
		var feature = d.feature;
		var featurestr = "";
		for(i = 0; i < feature.length; i++) {
			featurestr += feature[i] + "<br/>";
		}
		html += "<td>" + featurestr + "</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td ><b>途经景点</b></td>";
		var sightSpot = d.sightSpot;
		var sightSpotstr = "";
		for(i = 0; i < sightSpot.length; i++) {
			sightSpotstr += sightSpot[i] + "&nbsp;"
		}
		html += "<td>" + sightSpotstr + "</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td ><b>出发时间</b></td>";
		html += "<td>" + d.dateOfDeparture + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>结束时间</b></td>";
		html += "<td>" + d.dateOfExpire + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>报名截止时间</b></td>";
		html += "<td>" + d.dateOfBookingExpire + "</td>";
		html += "</tr>";		

		html += "<tr>";
		html += "<td ><b>线路方式</b></td>";
		html += "<td>" + d["function"] + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>线路类别</b></td>";
		html += "<td>" + d.type + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>出发城市</b></td>";
		html += "<td>" + d.departure + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>到达城市</b></td>";
		html += "<td>" + d.arrive + "</td>";
		html += "</tr>";	
		
		html += "<tr>";
		html += "<td ><b>行程天数</b></td>";
		html += "<td>" + d.itineraryDay + "</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td ><b>行程描述</b></td>";
		html += "<td>";
		html += "<table>";
		var miscellaneous = d.miscellaneous;		
		
		for(i = 0; i < miscellaneous.length; i++) {
			var itinerary = miscellaneous[i];
			//html += "<tr>";
			//html += "<td>第" + (i + 1) + "天&nbsp;</td>";
			//html += "</tr>";
			html += "<tr>";			
			html += "<td>第" + (i + 1) + "天&nbsp;<b>" + itinerary.title + "</b>&nbsp;</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<td>to:<b>" + itinerary.toCity + "</b>&nbsp;</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<td>" + itinerary.description.replace("\r\n","<br>") + "</td>"; 
			html += "</tr>";
			html += "<tr><br/></tr>";
		}
		html += "</table>";
		html += "</td>";
		html += "</tr>";
		
		html += "</table>";
			
		td.innerHTML = html;
	}
	function doInsert() {
		var v1 = document.getElementById("wrapperSelect");
		var v2 = document.getElementById("wrapperDateSelect");
		var v3 = document.getElementById("jsonFileSelect");
		if (v1.value == -1 || v2.value == -1 || v2.value == "" || v3.value == -1 || v3.value == "") {
			alert("请选择wrapper、日期和文件!");
			return;
		}
		JsonParse.doInsert(v1.value, v2.value, v3.value, function(result){
			alert(result);
		}); 
	}
	function doClick() {
		var v1 = document.getElementById("wrapperSelect");
		var v2 = document.getElementById("wrapperDateSelect");
		var v3 = document.getElementById("jsonFileSelect");
		if (v1.value == -1 || v2.value == -1 || v2.value == "" || v3.value == -1 || v3.value == "") {
			alert("请选择wrapper、日期和文件!");
			return;
		}
		JsonParse.getRoutes(v1.value, v2.value, v3.value, function(result){
			data = result;
			var sp = document.getElementById("sp");
			var html = "<table width=\"100%\" align=\"center\" border=\"0\">";
			for(var i = 0; i < result.length; i++) {
				var o = result[i];
				html += "<tr>";
				html += "<td>" + (i + 1) + "</td>";
				if (o.miscellaneous.length != 0)
					html += "<td><A href=\"" + o.url + "\" target=\"_blank\">" + o.title + "</A>&nbsp;<input type=\"button\" value=\"展开\" onclick=\"doDetail(" + i + ")\" />";		
				else 	
					html += "<td><A href=\"" + o.url + "\" target=\"_blank\">" + o.title + "</A>&nbsp;<font color='red'><b>无行程</b></font><input type=\"button\" value=\"展开\" onclick=\"doDetail(" + i + ")\" />";
				html += "&nbsp;<A href=\"javascript:return false;\" onclick=\"bookingcheck('" + o.wrapperId + "','" + o.url + "',this)\">Booking校验</A>";
				html += "&nbsp;<A href=\"javascript:return false;\" onclick=\"offlinecheck(" + i + ",this)\">下线逻辑</A></td>";
				html += "</tr>";
				html += "<tr>";
				html += "<td ><b>途经景点</b></td>";
				var sightSpot = o.sightSpot;
				var sightSpotstr = "";
				for(j = 0; j < sightSpot.length; j++) {
					sightSpotstr += sightSpot[j] + "&nbsp;";
				}
				html += "<td>" + sightSpotstr + "</td>";
				html += "</tr>";
				html += "<tr>";
				for(j = 0; j < o.miscellaneous.length; j++) {
					var itinerary = o.miscellaneous[j];
					html += "<tr>";
					html += "<td colspan=2>第" + (j + 1) + "天&nbsp;";			
					html += "to:<b>" + itinerary.toCity + "</b></td>";
					html += "</tr>";
				}					
					
				html += "<tr>";
				html += "<td colspan=\"2\" id=\"td" + i + "\">";
				
				html += "</td>";
				html += "</tr>";
			}
			html += "</table>";
			sp.innerHTML = html;
		});
		
	}
</script>
</head>
<body>
	<select id="wrapperSelect" onchange="selectwrapper(this.value)" style="width:200px">
		<option value="-1">请选择wrapper</option>
		<%
			for(String key : wrapperMap.keySet()) {
				out.println("<option value=\"" + key + "\" >" + wrapperMap.get(key) + "</option>");
			}
		%>
	</select>
	&nbsp;&nbsp;
	<select id="wrapperDateSelect" style="width:200px" onchange="selectdate(this.value)" >		
	</select>
	
	<select id="jsonFileSelect" style="width:200px" >		
	</select>
	
	<input type="button" value="确定" onclick="doClick()" />
	<input type="button" value="入库" onclick="doInsert()" style="display:none"/>
	<br/>
	<span id="sp">	
	</span> 
</body>
</html>