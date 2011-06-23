package com.qunar.deals.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractUtil {

	public static void main(String[] args) {
		System.out.println(isHKRoute("毛里求斯ClubMed度假村7天5晚特惠自由行（毛里求斯航空/香港往返）"));
	}
	public static String extractTraffic(String desc) {
		String regex = "双飞往返|飞往|双飞|航班|航空公司|机场|接机|直飞|国航|南航|新航|马航";
		Matcher matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "双飞往返";
		}
		regex = "三飞";
		matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "三飞往返";
		}
		regex = "卧飞";
		matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "卧飞往返";
		}
		regex = "双卧";
		matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "双卧往返";
		}
		regex = "三卧";
		matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "三卧往返";
		}
		
		regex = "汽车往返|双汽";
		matcher = Pattern.compile(regex).matcher(desc);
		if (matcher.find()) {
			return "汽车往返";
		}
		return null;
	}	
	public static boolean isHKRoute(String title) {
		title = title.replaceAll("\\(.*?\\)|（.*?）", "");
		Matcher m1 = Pattern.compile("香港|台[湾中南北]|澳门|[港澳台]{2,}").matcher(title);
		if (m1.find()) {
			String s = m1.group();
			m1 = Pattern.compile(s + "出发|" + s + "往返").matcher(title);
			if (m1.find()) return false;
			else return true;
		}
		return false;
	}
	public static String extractStarGrade(String desc) {		
		if (desc.indexOf("3星") != -1 || desc.indexOf("三星")!=-1) {
			return "三星";
		}
		if (desc.indexOf("4星") != -1 || desc.indexOf("四星")!=-1) {
			return "四星";
		}
		if (desc.indexOf("5星") != -1 || desc.indexOf("五星")!=-1) {
			return "五星";
		}
		int star = 0;
		Matcher matcher = Pattern.compile("★").matcher(desc);
		while(matcher.find()) {
			star ++;
		}
		switch(star) {
		case 0: return null;
		case 1: return null;
		case 2: return null;
		case 3: return "三星";
		case 4: return "四星";
		case 5: return "五星";
		}
		return null;
	}
}
