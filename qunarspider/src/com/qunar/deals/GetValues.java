package com.qunar.deals;
//package com.luyao.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class GetValues {
	public String getContent(String url,String charSet){
		String content = "";
		HttpClient client = new HttpClient();
		GetMethod getMethod =null;
		try {
			getMethod = new GetMethod(url);
			
			client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
			client.getParams().setSoTimeout(60000);
			
			client.executeMethod(getMethod);
			InputStream is = getMethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,charSet));
			String temp = "";
			StringBuffer sb = new StringBuffer();
			while((temp = br.readLine())!=null){
				sb.append(temp+"\n");
			}
			br.close();
			is.close();
			content = sb.toString();
			temp = "";
			sb.delete(0,content.length());
		} catch (Exception e) {
//			e.printStackTrace();
			return "";
		}finally{
			try {
				if(getMethod!=null){
					getMethod.releaseConnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	public String getContent(File file){
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getCanonicalPath()),"UTF-8"));
			String temp = "";
			StringBuffer sb = new StringBuffer();
			while((temp = br.readLine())!=null){
				sb.append(temp+"\n");
			}
			br.close();
			content = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return content;
	}
	
	public String subString(String content,String startChar,String endChar){
		String value = "";
		int startIndex = 0;
		if(!startChar.equals("")){
			startIndex = content.indexOf(startChar);
		}
		
		int endIndex = 0;
		if(endChar.equals("")){
			endIndex = content.length();
		}else{
			endIndex = content.indexOf(endChar,startIndex);
		}
		if(startIndex == -1 || endIndex==-1){
			return value;
		}
		if(startIndex+startChar.length()<=endIndex){
			value = content.substring(startIndex+startChar.length(),endIndex);
		}
		return value;
	}
	public String getValue(String source, String st, String end,boolean bool) {
		int a = 0;
		if (bool == true) {
			a = source.indexOf(st);
		} else {
			a = source.lastIndexOf(st);
		}
		
		if (a == -1){
			return "";
		}
		if (end.equals("")) {
			return source.substring(a + st.length());
		}
		int b = source.indexOf(end, a + st.length());
		if (b == -1){
			return "";
		}
		return source.substring(a + st.length(), b);
	}
}
