<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection,java.util.Iterator;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>wrapper管理</title>
<%
	String result = (String) request.getAttribute("exception");
%>
<script type="text/javascript">

function timerPerActive(timer,comfunc,obj)  
{ 
obj.disabled=true;
this.times=timer;
var name = obj.value;
this.change=function() 
 { 
 this.times--; 
 obj.value = name+"("+this.times+")";
 if(this.times==0) 
  { 
  obj.value = name
  clearInterval(times); 
  this.complete();    
  } 
 } 
this.callback=function()  
 { 
 var css=this; 
 times=setInterval(function(){css.change();},1000); 
 } 
this.complete=function()
 {document.getElementById("exacting").style.display="none";
    comfunc();
    }
}

function extr(){
document.getElementById("p").disabled=true;
document.getElementById("s").disabled=true;
document.getElementById("x").disabled=true;
document.getElementById("exacting").style.display="";
window.open('extract.action','rightFrame');
//document.forms[0].submit();
var cs1 = new timerPerActive(2400,par,document.getElementById("e"));
cs1.callback();
}

function par(){
document.getElementById("e").disabled=true;
document.getElementById("s").disabled=true;
document.getElementById("x").disabled=true;
document.getElementById("exacting").style.display="";
window.open('parse.action','rightFrame');
//document.forms[0].submit();
var cs2 = new timerPerActive(100,checkXML,document.getElementById("p"));
cs2.callback();
}
function subm(){
window.open('watchXML.action','rightFrame');
if(confirm("确定提交吗")){

document.getElementById("p").disabled=true;
document.getElementById("e").disabled=true;
document.getElementById("x").disabled=true;
document.getElementById("exacting").style.display="";
window.open('submit.action','rightFrame');
var cs3 = new timerPerActive(50,success,document.getElementById("s"));
cs3.callback();
}
else{
document.getElementById("exacting").style.display="none";
document.getElementById("s").disabled=false;
return;
}
}

function success(){
document.getElementById("exacting").style.display="none";
document.getElementById("p").disabled=false;
document.getElementById("e").disabled=false;
document.getElementById("x").disabled=false;
var cs5 = new timerPerActive(1,watchSubmit,document.getElementById("s"));
}

function checkXML(){
document.getElementById("p").disabled=true;
document.getElementById("e").disabled=true;
document.getElementById("s").disabled=true;
document.getElementById("exacting").style.display="";
window.open('checkXML.action','rightFrame');
var cs4 = new timerPerActive(5,subm,document.getElementById("x"));
cs4.callback();
}
function watchException(){
window.open('exception.action','rightFrame');
//document.forms[0].action="exception.action";
//document.forms[0].submit();

}

function watchSubmit(){
window.open('submitStatis.action','rightFrame');
//document.forms[0].action="submitStatis.action";
//document.forms[0].submit();

}
function watchXML(){
window.open('watchXML.action','rightFrame');
//document.forms[0].action="watchXML.action";
//document.forms[0].submit();

}
</script>
<%
	String flag4 = "";
	String flag1 = "";
	String flag2 = "";
	String flag3 = "";
	String flag = (String) request.getAttribute("flag");
	if (flag != null) {
		if (flag.equals("exact")) {
			flag2 = "disabled=true";
			flag1 = "";
			flag3 = "disabled=true";
			flag4 = "disabled=true";
		} else if (flag.equals("parse")) {
			flag2 = "";
			flag1 = "disabled=true";
			flag3 = "disabled=true";
			flag4 = "disabled=true";
		} else if (flag.equals("submit")) {
			flag2 = "disabled=true";
			flag1 = "disabled=true";
			flag3 = "";
			flag4 = "disabled=true";
		} else if (flag.equals("all")) {
			flag2 = "disabled=true";
			flag1 = "disabled=true";
			flag3 = "disabled=true";
			flag4 = "";
		}
	}
	String errStr = "";
	if (request.getAttribute("erro") != null)
		errStr = (String) request.getAttribute("erro");
%>
</head>
<body>
<form action="" name="extract"><font color=red><%=errStr%></font>
<hr>
执行过程将自动化，哪个按钮可用说明执行哪个状态中。 <br>
<br>
<table  cellspacing=0 cellpadding=0
	bordercolorlight='#C7C8C3' bordercolordark='#ffffff'>
	<tr>
		<th><a href="<%=request.getContextPath()%>/index.html">首页</a></th>

	</tr>
</table>
<table  cellspacing=0 cellpadding=0
	bordercolorlight='#C7C8C3' bordercolordark='#ffffff'>
	<tr>
		<th><input type="button" class="button" value="开始抽取" <%=flag1 %>
			onclick="extr()" id="e"></th>

	</tr>
</table>
<table style="display: none" id="exacting">
	<tr>
		<td>处理中...<img src="<%=request.getContextPath()%>\exacting.gif" /></td>
	</tr>
</table>
<table class="data_grid" cellspacing=0 cellpadding=0
	bordercolorlight='#C7C8C3' bordercolordark='#ffffff'>
	<tr>
		<th><input type="button" class="button" id="p" value="开始解析"
			onclick="par()"></th>
	</tr>
</table>
<table class="data_grid" cellspacing=0 cellpadding=0
	bordercolorlight='#C7C8C3' bordercolordark='#ffffff'>
	<tr>
		<th><input type="button" class="button" id="x" value="验证xml"
			onclick="checkXML()"></th>
	</tr>
</table>
<table class="data_grid" cellspacing=0 cellpadding=0
	bordercolorlight='#C7C8C3' bordercolordark='#ffffff'>
	<tr>
		<th><input type="button" class="button" id="s" value="提交"
			onclick="subm()"></th>
	</tr>
</table>
<hr>
<br>
<br>
<input type="button" value="查看异常" onclick="watchException()">
<hr>
<br>
<br>
查看提交统计情况:<br>
<%
if(request.getAttribute("fileListBeans")!=null){
Collection submitReportFileList = (Collection)request.getAttribute("fileListBeans");
for(Iterator it = submitReportFileList.iterator();it.hasNext();){
	String temFileName = (String)it.next();

%>
<a href ="process.jsp?fileName=<%=temFileName%>"><%=temFileName%></a>
<br>
<%}
} %>
<hr>
<br>
<br>
<input type="button" value="验证xml的有效性（提交前验证）" onclick="watchXML()">
</form>

<div id="exception">说明：使用过程中，直接点第一步后会自动执行一直到提交时会等待确认，确认主要是通过查看日志，看是否有问题出现。以及需要修改后重新生成。<br>
如果添加抽取程序，只需把对应的抽取类放到com.qunar.deals.extract
包下即可，新写的抽取类要继承ExtractInterface接口，在process方法中写运行此类的程序即可<br>
如果添加解析程序，只需把对应的解析类放到com.qunar.deals.parse
包下即可，新写的抽取类要继承ParseInterface接口，在process方法中写运行此类的程序即可。<br>
如果某些抽取类和对应的解析类不使用，可以把这些类在nouseclass.properties中添加。</div>