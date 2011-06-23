package com.base.deals.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qunar.deals.util.MonitorUnit;

public class Monitor {

	static final Log logger = LogFactory.getLog(Monitor.class);
	
	private static Monitor instance = new Monitor();
	
	private Monitor() {}
	
	public static Monitor getInstance() {
		return instance;
	}
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	long getDuration(String start, String end) {
		try {
			long starttime = format2.parse(start).getTime() / 1000;
			long endtime = format2.parse(end).getTime() / 1000;
			return endtime - starttime;
		} catch(Exception e) {
			return 0;
		}
	}
	public List<MonitorUnit> getMonitorUnits(String startTime, String endTime) {
		List<MonitorUnit> result = new ArrayList();
		Connection conn = null;
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			
			String sql = "select wrapperId, wrapperName, starttime, endtime, total from crawler_log where starttime > ? and endtime < ? order by wrapperId, starttime";
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, startTime + " 00:00:00");
			pstat.setString(2, endTime + " 23:59:59");
			ResultSet rs = pstat.executeQuery();
			while (rs.next()) {
				MonitorUnit unit = new MonitorUnit();
				result.add(unit);
				unit.setWrapperId(rs.getString("wrapperId"));
				unit.setWrapperName(rs.getString("wrapperName"));
				unit.setStartTime(rs.getString("starttime"));
				unit.setEndTime(rs.getString("endtime"));
				unit.setTotal(rs.getInt("total"));
				unit.setDuration(getDuration(unit.getStartTime(), unit.getEndTime()));
			}
			//conn.prepareStatement()
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
		return result;
	}
}
