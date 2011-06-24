package com.qunar.deals.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	
	public static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	public static String getFileTimeStamp() {
		Date d = new Date();
		Date d2 = new Date(d.getTime() + 12*60*60*1000);
		String d1String = format.format(d);
		String d2String = format.format(d2);
		if (d1String.equals(d2String)) return d1String + " AM";
		return d1String + " PM";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getFileTimeStamp();

	}

}
