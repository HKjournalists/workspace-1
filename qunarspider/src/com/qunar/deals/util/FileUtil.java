package com.qunar.deals.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	//添加xml文件头和尾
	public static String addXml(String content){
		String xmlHead = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<travelRoute>\n";
		String xmlTail = "</travelRoute>";
		return xmlHead + content + xmlTail;
	}
	//读xml内容
	public static List<String> getContent(String fileName,String encode){
		List<String> lst = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),encode));
			String line = "";
			while ((line = reader.readLine()) != null) {
				lst.add(line);
			}
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (java.io.IOException e) {
			e.printStackTrace();
		}
		 finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {
				}
			}
		}
		 return  lst;
	}
	
	//写文件
	public static void writeFile(String fileName,String content){
		try {
			File file = new File(fileName);
			file.getParentFile().mkdirs();
			PrintWriter fw = new PrintWriter(fileName,"UTF-8");
			fw.write(content.replaceAll("&", "&amp;"));
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		writeFile("d://test.txt","asdfasdfadf\nmvmvmvmvmvmv");
	}
}
