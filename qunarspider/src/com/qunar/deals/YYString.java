package com.qunar.deals;

import java.util.HashMap;
import java.util.Map;

public class YYString {	
	public static String getValue(String source, String st, String end,boolean bool) {
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
	
	public static Map<String,String> getUrlParams(String url,boolean debug){
		Map<String,String> map = new HashMap<String,String>();
		
		String[] temp = url.split("&");
		for(String str : temp){
			if(str.indexOf("?")>-1){
				str = str.substring(str.indexOf("?")+1);
			}
			String[] values = str.split("=");
			if(values.length>1){
				map.put(values[0], values[1]);
			}else{
				map.put(values[0], "");
			}
		}
		
		if(debug){
			for(Map.Entry<String, String> entry : map.entrySet()){
				System.out.println(entry.getKey()+":"+entry.getValue());
			}
		}
		
		return map;
	}

	//roomType && roomPrice
	public static String[] getValueInArray(String initialStr,String split){
		return initialStr.split(split);		
	}
	
	//hotelName && hotelUrl
	public static String[] getNameAndUrl(String content,String key,boolean debug){
		//统一相关字母大小写
		content = content.replaceAll("<A", "<a");
		content = content.replaceAll("</A", "</a");
		content = content.replaceAll("HREF", "href");
		
		String[] links = content.split("<a");
		if(links.length>1){//如果有多个href,则根据key过滤出要选择的href
			content = "";
			for(String link : links){
				if(link.indexOf(key)>-1){
					content = "<a" + link;
					break;
				}
			}
			if(content.isEmpty()){
				return null;
			}
		}
		
		content = content.substring(content.indexOf("<a"),content.indexOf("</a"));
		if(debug){
			System.out.println("content:"+content);
		}
		
		int start = content.indexOf(">");
		if(start == -1){
			return null;
		}
		//过滤出hotelName
		String name = content.substring(start+1).trim();
		if(debug){
			System.out.println("name:"+name);
		}
		
		int ss = -1;
		ss = content.indexOf("href=");		
		if(ss == -1){
			return new String[]{name,""};
		}
		content = content.substring(ss+5);
		
		int e1 = content.indexOf(" ");
		int e2 = content.indexOf(">");
		int ee = e2;
		if(e1<e2 && e1>-1){
			ee = e1;
		}
		if(ee == -1){
			return new String[]{name,""};
		}
		//过滤出hotelUrl
		String url = content.substring(0,ee).replaceAll("\"", "").replaceAll("'", "").replaceAll("target=_blank", "").trim();
		
		if(debug){
			System.out.println("url:"+url);
		}
		return new String[]{name,url};
	}
	
	public static void main(String[] args) {
		String url = "http://www.bjyoule.com/hotel/hotelSearch.aspx?time1=2008-12-10&time2=2008-12-13&jdname=&jiage1=&jiage2=";
		getUrlParams(url,true);
	}
}
