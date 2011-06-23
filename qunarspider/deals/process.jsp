<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.io.BufferedReader,java.io.InputStreamReader,java.io.OutputStream,java.io.FileInputStream,java.io.File;"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="data_grid.css" type="text/css" />
<title>wrapper管理</title>



</head>
<body>
<%
String fileName = "";
	if(request.getParameter("fileName")!=null)
		fileName = request.getParameter("fileName");
File t = new File("E:\\Program Files\\SecureCRT\\download\\"+fileName);
BufferedReader rd = new BufferedReader(new InputStreamReader(
                new FileInputStream(t), "UTF-8"));
try {
				response.reset();
                response.setContentType("application/x-msdownload");
                response.addHeader("Content-Disposition", "attachment; filename=" + t.getName());
                        FileInputStream fs = new FileInputStream(t);
                        OutputStream outStream = null;
                        response.flushBuffer();
                        outStream = response.getOutputStream();
                                byte[] buf = new byte[1024];
                                int c = fs.read(buf);
                                while (c != -1) {
                        outStream.write(buf, 0, (int) c);
                        c = fs.read(buf);
                                }
                                fs.close();
                                outStream.close();


                } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                } 
%>
</body>