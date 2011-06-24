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



public class VoyageRegion {

	protected static final Log logger = LogFactory.getLog(VoyageRegion.class);
	static Map<String, Set<String>> voyageRegionMap = new HashMap();
	static Set<String> voyageLines = new HashSet();
	static {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(VoyageRegion.class.getClassLoader().getResourceAsStream("voyageregion.txt")));
			String line = null;
			while( (line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				System.out.println(line);
				String[] voyageline = line.split("=");
				if (voyageline == null || voyageline.length != 2) continue;
				String route = voyageline[0];
				voyageLines.add(route);
				route = route.replaceAll("^\uFEFF", "");
				String tos = voyageline[1];
				String[] toss = tos.split(",");
				for(String country : toss) {
					if (voyageRegionMap.containsKey(country)){
						voyageRegionMap.get(country).add(route);
					} else {
						Set<String> set = new HashSet();
						set.add(route);
						voyageRegionMap.put(country, set);
					}
				}
			}
		} catch(Exception e) {
			logger.error("加载航线配置文件出错!");
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
	
	public static String getVoyageLine(String voyage) {
		if (voyage == null) return "";
		List<OptionSight> os = ItinerayExtractor.getInstance().dict.extractOptionSights(voyage);
		Set<String> result = new HashSet();
		for(OptionSight sight : os) {
			if (sight.getOptionSights().size() > 1) continue;
			for(Sight s : sight.getOptionSights()) {
				Sight p = s;
				while( p != null) {					
					if (voyageRegionMap.containsKey(p.getName())) {
						result.addAll(voyageRegionMap.get(p.getName()));
					}
					
					p = p.getParentSight();
				}
			}
		}
		for(String line : voyageLines) {
			if (line == null) continue;
			String rLine = line.replace("航线", "");
			if (voyage.contains(rLine)) {
				result.add(line);
			}
		}
		String ss = "";
		for(String s : result) {
			ss += s + ",";
		}
		if (ss.endsWith(",")) {
			ss = ss.substring(0, ss.length() - 1);
		}
		return ss;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getVoyageLine("香港,日韩");

	}

}
