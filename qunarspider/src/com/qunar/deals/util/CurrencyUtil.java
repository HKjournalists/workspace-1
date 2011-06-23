package com.qunar.deals.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qunar.itineray.ie.ItinerayExtractor;
import com.qunar.itineray.ie.OptionSight;
import com.qunar.itineray.ie.Sight;



public class CurrencyUtil {

	protected static final Log logger = LogFactory.getLog(CurrencyUtil.class);
	static Map<String, String> currencyMap = new HashMap();
	
	static Map<String, Double> currencyRationMap;
	
	static java.text.DecimalFormat df = new java.text.DecimalFormat("##");
	public static double transCurrency(double input, String currencyType) {
		if (currencyMap.containsKey(currencyType)) {
			String s = df.format(input * currencyRationMap.get(currencyType));
			return Double.parseDouble(s);
		} else {
			return -1;
		}
	}
	static {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(CurrencyUtil.class.getClassLoader().getResourceAsStream("currency.txt")));
			String line = null;
			while( (line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				System.out.println(line);
				String[] currencyLine = line.split("=");
				if (currencyLine == null || currencyLine.length != 2) continue;
				String route = currencyLine[0];
				route = route.replaceAll("^\uFEFF", "");
				String tos = currencyLine[1];
				String[] toss = tos.split(",");
				for(String alias : toss) {
					currencyMap.put(alias, route);
				}
			}
		} catch(Exception e) {
			logger.error("加载货币配置文件出错!");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		loadProps();
	}
	
	public static  void loadProps() {
		Map<String, Double> map = new HashMap();
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(CurrencyUtil.class.getClassLoader().getResource("currencyratio.txt").getFile());
			p.load(is);
			
			for(Object key : p.keySet()) {
				map.put(key.toString(), new Double( p.getProperty(key.toString())));
			}
			currencyRationMap = map;
			for(String key : currencyMap.keySet()) {
				System.out.println(key + "->" + currencyMap.get(key));
			}
			
		} catch (IOException e) {
			logger.error("加载货币配置文件出错!");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getCurrency(String currency) {
		String result = currencyMap.get(currency);
		if (result == null) return "CNY";
		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(df.format(3434.6));

	}

}
