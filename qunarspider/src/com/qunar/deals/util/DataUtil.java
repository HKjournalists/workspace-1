package com.qunar.deals.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.qunar.database.DataSet;

public class DataUtil {

	private static Set<String> whiteList = new HashSet();
	private static Set<String> blackList = new HashSet();
	
	private List<WhiteBlackList> getWhiteBlackList() {
		List<WhiteBlackList> lst = new ArrayList();
		String[][] rows = DataSet.query("route", "select city, sight,filter from s1", 0, 0);
		for(int i = 0; i < rows.length; i++) {
			String[] row = rows[i];
			WhiteBlackList wb = new WhiteBlackList();
			wb.setCity(row[0]);
			wb.setSight(row[1]);
			wb.setFlag(row[2]);
			lst.add(wb);
		}
		return lst;
	}
	public void loadData() {
		List<WhiteBlackList> wbLst = getWhiteBlackList();
		Set<String> wList = new HashSet();
		Set<String> bList = new HashSet();
		for(WhiteBlackList wb : wbLst) {
			if (wb.isWhiteList()) {
				wList.add(wb.getKey());
			} else {
				bList.add(wb.getKey());
			}
		}
		whiteList = wList;
		blackList = bList;
	}
	
	public static Set<String> getWhiteList() {
		return whiteList;
	}
	public static Set<String> getBlackList() {
		return blackList;
	}
	
	
	
}
