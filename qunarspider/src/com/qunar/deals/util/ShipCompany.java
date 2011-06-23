package com.qunar.deals.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qunar.itineray.ie.ItinerayExtractor;
import com.qunar.itineray.ie.OptionSight;
import com.qunar.itineray.ie.Sight;



public class ShipCompany {

	protected static final Log logger = LogFactory.getLog(ShipCompany.class);
	static Map<String, String> shipMap = new HashMap();
	
	static {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(ShipCompany.class.getClassLoader().getResourceAsStream("shipcompany.txt")));
			String line = null;
			while( (line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				System.out.println(line);
				String[] voyageline = line.split("=");
				if (voyageline == null || voyageline.length != 2) continue;
				shipMap.put(voyageline[0], voyageline[1]);
			}
		} catch(Exception e) {
			logger.error("加载邮轮公司配置文件出错!");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getShipCompany(String voyage) {
		if (voyage == null) return "";
		for(String key : shipMap.keySet()) {
			if (voyage.contains(key)) return shipMap.get(key);
		}
		return voyage;
	}
	
	public static String extractShipCompany(String title) {
		if (title == null) return "";
		for(String key : shipMap.keySet()) {
			if (title.contains(key)) return shipMap.get(key);
		}
		
		return "";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getShipCompany("公主邮轮"));

	}

}
