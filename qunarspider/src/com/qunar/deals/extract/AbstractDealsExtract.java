package com.qunar.deals.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import com.qunar.deals.util.DateUtil;
import com.qunar.deals.util.FileUtil;
import com.qunar.deals.util.RouteList;

public abstract class AbstractDealsExtract implements DealsExtractInterface {

	public static SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat format2= new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	protected String xmlPath;
	
	private String URL_PATH;
	protected String fileTimeStamp = DateUtil.getFileTimeStamp();
	protected String wrapperId = "noId";
	protected String charset = "";
	protected long startTime;
	public AbstractDealsExtract(String wrapperId) {
		this.wrapperId = wrapperId;
		URL_PATH = ResourceBundle.getBundle("file").getString("BasePath") + "HTML" + File.separator + wrapperId + File.separator;	
		//xmlPath = ResourceBundle.getBundle("file").getString("BasePath") + "HTML" + File.separator + wrapperId + File.separator + DateUtil.getFileTimeStamp() + "/sss.xml";
		
	}
	
	public AbstractDealsExtract(String wrapperId, String charset) {
		this.wrapperId = wrapperId;
		this.charset = charset;
		URL_PATH = ResourceBundle.getBundle("file").getString("BasePath") + "HTML" + File.separator + wrapperId + File.separator;	
		xmlPath = ResourceBundle.getBundle("file").getString("BasePath") + "HTML" + File.separator + wrapperId + File.separator + format2.format(new Date(0L)) + "/sss.xml";
	}
	
	public String getXmlPath(long startTime) {
		this.startTime = startTime;
		if (xmlPath == null) {
			process();
		}
		return xmlPath;
	}
	public String getXmlPath() {
		if (xmlPath == null) {
			process();
		}
		return xmlPath;
	}
	
	protected void appendString(String path, String content) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(path, "rw");
			raf.seek(raf.length());
			raf.write((content + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getContent(Collection<RouteList> routeList) {
		StringBuilder sb = new StringBuilder();
		for (RouteList routeObject : routeList) {
			sb.append(routeObject.toString() + "\n");
		}
		String content = sb.toString();
		content = FileUtil.addXml(content);
		
		return content;
	}
	
	protected void produceXml(Collection<RouteList> routeList) {
		String content = getContent(routeList);
		xmlPath = URL_PATH + format2.format(new Date(startTime)) + "/sss.xml";
		FileUtil.writeFile(xmlPath, content);
	}
	
}
