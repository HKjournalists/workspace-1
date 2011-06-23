package com.qunar.deals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.base.classloader.LoaderUtil;
import com.base.classloader.ManageClassLoader;
import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.DateUtil;
import com.qunar.deals.util.MailSender;


public class BookingCheck {
	static final Log logger = LogFactory.getLog(BookingCheck.class);
	int handleDatas = 100;
	private List<Integer> needChecking = new ArrayList<Integer>();
	
	private static BookingCheck instance = new BookingCheck();
	
	final String WRAPPER_CLASSPATH = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	
	Object lock = new Object();
	
	Set<String> handleWrappers = new HashSet<String>();
	
	public static  BookingCheck getInstance() {
		return instance;
	}
	
	private BookingCheck() {
	}
	
	public synchronized void addId(Integer id) {
		needChecking.add(id);
		for(Integer i : needChecking) {
			System.out.print(i + ",");
		}
		System.out.println("add over");
	}
	
	private synchronized  List<Integer> getTasks() {
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> list = new ArrayList<Integer>();
		
		list.addAll(needChecking);
		for(int i = 0; i < Math.min(handleDatas, list.size()); i++) {
			result.add(list.get(i));
			needChecking.remove(list.get(i));
		}
		for(Integer i : needChecking) {
			System.out.print(i + ",");
		}
		System.out.println("get over");
		return result;
	}
	
	public int  getBookingCheckUnits(String wrapperId, String date, int startRecord, int pageCount, List<BookingCheckUnit> result,String otherChange) {		
		int total = 0;
		Connection conn = null;
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			String sql = "select count(1) from bookingcheckdetail where wrapperId = ? and changeTime >= ? and changeTime <= ? and otherChange = ?";
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, wrapperId);
			pstat.setString(2, date + " 00:00:00");
			pstat.setString(3, date + " 23:59:59");
			pstat.setString(4, otherChange);
			ResultSet rs = pstat.executeQuery();
			while (rs.next()) {
				total = rs.getInt(1);
			}
			pstat.close();
			
			sql = "select routeId,wrapperId,beforeChangePrice,afterChangePrice,changeTime,sourceUrl,title,otherChange from bookingcheckdetail where wrapperId = ? and changeTime >= ? and changeTime <= ? and otherChange = ? order by changeTime limit " + startRecord + "," + pageCount;
			
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, wrapperId);
			pstat.setString(2, date + " 00:00:00");
			pstat.setString(3, date + " 23:59:59");
			pstat.setString(4, otherChange);
			rs = pstat.executeQuery();
			while (rs.next()) {
				BookingCheckUnit unit = new BookingCheckUnit();
				unit.wrapperId = rs.getString("wrapperId");
				unit.id = rs.getInt("routeId");
				unit.price = rs.getDouble("beforeChangePrice");
				unit.vPrice = rs.getDouble("afterChangePrice");
				unit.title = rs.getString("title");
				if (unit.title == null || unit.title.isEmpty()) {
					unit.title = "无标题";
				}
				unit.changeTime = rs.getString("changeTime");
				unit.sourceUrl = rs.getString("sourceUrl");
				unit.otherChange = rs.getString("otherChange");
				if ( unit.otherChange==null ||unit.otherChange.isEmpty() ) {
					unit.otherChange = "价格变化";
				}
				result.add(unit);
				
			}
			pstat.close();		
			
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
		
		return total;
	}
	public List<BookingCheckReport> getBookingCheckReport(String startDate, String endDate) {
		startDate = startDate.replace("-", "");
		endDate = endDate.replace("-", "");
		List<BookingCheckReport> result = new ArrayList<BookingCheckReport>();
		Connection conn = null;
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			
			String sql = "select wrapperId,count, timestamp from bookingchecklog where timestamp >= ? and timestamp <= ? order by wrapperId, timestamp";
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, startDate);
			pstat.setString(2, endDate);
			ResultSet rs = pstat.executeQuery();
			Map<String, BookingCheckReport> tMap = new HashMap<String, BookingCheckReport> ();
			while (rs.next()) {
				BookingCheckReport unit = new BookingCheckReport();
				result.add(unit);
				unit.wrapperId = rs.getString("wrapperId");
				unit.bookingCount = rs.getInt("count");
				unit.timestamp = rs.getString("timestamp");
				tMap.put(unit.wrapperId + "-" + unit.timestamp, unit);
			}
			pstat.close();
			
			try {
				startDate = DateUtil.format.format(DateUtil.timestampFormat.parse(startDate)) + " 00:00:00";
				endDate =  DateUtil.format.format(DateUtil.timestampFormat.parse(endDate)) + " 23:23:59";
			} catch (ParseException e) {
				e.printStackTrace();
			}

			sql = "select wrapperId,otherChange,changeTime from bookingcheckdetail where changeTime >= ? and changeTime <= ?";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, startDate);
			pstat.setString(2, endDate);
			rs = pstat.executeQuery();
			while (rs.next()) {
				String otherChange = rs.getString("otherChange");
				String datetime = rs.getString("changeTime");
				String wrapperId = rs.getString("wrapperId");
				try {
					datetime = DateUtil.timestampFormat.format(DateUtil.fullDateTimeFormat.parse(datetime));
				} catch (ParseException e) {
					continue;
				}
				BookingCheckReport unit = tMap.get(wrapperId + "-" + datetime);
				if (unit == null) continue;
				if ("无法获得价格".equals(otherChange)) {
					unit.emptyPriceCount++;
				} else if ("线路下线".equals(otherChange)) {
					unit.offlineCount++;
				} else {
					unit.priceChangeCount++;
				}
			}
			pstat.close();
			
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
	public void handleChecking(String wrapperId, List<BookingCheckUnit> units) {
		logger.info("开始执行BookingCheck:" + wrapperId);
		ManageClassLoader cl = ManageClassLoader.getInstance();
		final File config = new File(WRAPPER_CLASSPATH + wrapperId + File.separator + "wrapper.n3");
		String parser = LoaderUtil.getParser(config);
		if (parser == null) return;
		final boolean isInterface = LoaderUtil.isInterface(config);
		try {
			AbstractDealsParse task = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
			task.setStartTime(System.currentTimeMillis());
			task.initCachedValue();
			task.initRouteOffline();
			task.bookingCheck(units, isInterface);
			task.flushCachedValue();
			task.flushRouteOffline();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void notifyLock(String wrapperId) {
		handleWrappers.remove(wrapperId);
		System.out.println(wrapperId + ":" + handleWrappers.size());
		if (handleWrappers.size() == 0) {
			synchronized(lock) {
				lock.notify();
			}
			
		}
	}
	public void handleChecking() {
		logger.info("开始执行一个BookingCheck周期");
		List<Integer> ids = getTasks();
		if (ids != null && ids.size() > 0) {
			Map<String, List<BookingCheckUnit>> needBookingCheck = getCheckUnit(ids);
			for(String wrapperId : needBookingCheck.keySet()) {
				handleWrappers.add(wrapperId);
			}
			for(final Map.Entry<String, List<BookingCheckUnit>> entry : needBookingCheck.entrySet()) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Thread.sleep((long)(Math.random() * 4000L));  //在4秒钟内，任务先后开始运行
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handleChecking(entry.getKey(), entry.getValue());
						
//						notifyLock(entry.getKey());
						
					}}).start();
				
			}
//			synchronized(lock) {
//				try {
//					logger.info("开始等待BookingCheck结束!");
//					lock.wait();
//					logger.info("BookingCheck结束!");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}
	public Map<String, List<BookingCheckUnit>> getCheckUnit(List<Integer> ids) {
		String idsSql = ids.toString().replace("[", "(").replace("]", ")");
		
		String sql = "Select id, wrapperId, listInfo, status, sourceUrl,price,title from route where id in " + idsSql;
		logger.info("BookingCheck sql:" + sql);
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;	
		Map<String, List<BookingCheckUnit>> result = new HashMap<String, List<BookingCheckUnit>>();
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			stat = conn.createStatement();
		
			rs = conn.createStatement().executeQuery(sql);
			while(rs.next()) {
				int id = rs.getInt(1);
				String listInfo = rs.getString(3);
				String wrapperId = rs.getString(2);
				int status = rs.getInt(4);
				String surl = rs.getString(5);				
				Double price = rs.getDouble(6);
				String title = rs.getString(7);
				if (status != 1 && status != 3) continue;
				BookingCheckUnit unit = new BookingCheckUnit();
				unit.id = id;
				unit.listInfo = listInfo;
				unit.wrapperId = wrapperId;
				unit.status = status;
				unit.sourceUrl = surl;
				unit.price = price;
				unit.title = title;
				List<BookingCheckUnit> tList = result.get(wrapperId);
				if (tList == null) {
					tList = new ArrayList<BookingCheckUnit>();
					result.put(wrapperId, tList);
				}
				tList.add(unit);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} 
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
	
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){ //每隔分钟获取一次需处理的数据

			@Override
			public void run() {
				handleChecking();
			}}, 
				new Date(),
				60*1000L
				);
	}

	public static class BookingCheckReport {
		String wrapperId;
		
		String timestamp;
		
		int bookingCount;
		
		int priceChangeCount;
		
		int emptyPriceCount;

		int offlineCount;
		
		
		
		public int getOfflineCount() {
			return offlineCount;
		}

		public void setOfflineCount(int offlineCount) {
			this.offlineCount = offlineCount;
		}

		public String getWrapperId() {
			return wrapperId;
		}

		public void setWrapperId(String wrapperId) {
			this.wrapperId = wrapperId;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public int getBookingCount() {
			return bookingCount;
		}

		public void setBookingCount(int bookingCount) {
			this.bookingCount = bookingCount;
		}

		public int getPriceChangeCount() {
			return priceChangeCount;
		}

		public void setPriceChangeCount(int priceChangeCount) {
			this.priceChangeCount = priceChangeCount;
		}

		public int getEmptyPriceCount() {
			return emptyPriceCount;
		}

		public void setEmptyPriceCount(int emptyPriceCount) {
			this.emptyPriceCount = emptyPriceCount;
		}

		
		
		
	}
	public static class BookingCheckUnit {
		
		String html;
		
		public String getHtml() {
			return html;
		}

		public void setHtml(String html) {
			this.html = html;
		}

		String wrapperId;
		
		Integer id;
		
		String listInfo;
		
		Integer status;
		
		Double price;
		
		Double vPrice;
		
		String title;
		
		String changeTime = "";
		
		String otherChange = "";
		
		
		

		public String getOtherChange() {
			return otherChange;
		}

		public void setOtherChange(String otherChange) {
			this.otherChange = otherChange;
		}

		public String getChangeTime() {
			return changeTime;
		}

		public void setChangeTime(String changeTime) {
			this.changeTime = changeTime;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Double getvPrice() {
			return vPrice;
		}

		public void setvPrice(Double vPrice) {
			this.vPrice = vPrice;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public String getWrapperId() {
			return wrapperId;
		}

		public void setWrapperId(String wrapperId) {
			this.wrapperId = wrapperId;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getListInfo() {
			return listInfo;
		}

		public void setListInfo(String listInfo) {
			this.listInfo = listInfo;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getSourceUrl() {
			return sourceUrl;
		}

		public void setSourceUrl(String sourceUrl) {
			this.sourceUrl = sourceUrl;
		}

		String sourceUrl;
	}
	public void monitor(String timestamp) {
		System.out.println("start monitor");
		List<BookingCheckReport> result = new ArrayList<BookingCheckReport>();
		Connection conn = null;
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			
			String sql = "select wrapperId,count, timestamp from bookingchecklog where timestamp = ? order by wrapperId, timestamp";
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, timestamp);
			ResultSet rs = pstat.executeQuery();
			Map<String, BookingCheckReport> tMap = new HashMap<String, BookingCheckReport> ();
			while (rs.next()) {
				BookingCheckReport unit = new BookingCheckReport();
				result.add(unit);
				unit.wrapperId = rs.getString("wrapperId");
				unit.bookingCount = rs.getInt("count");
				unit.timestamp = rs.getString("timestamp");
				tMap.put(unit.wrapperId + "-" + unit.timestamp, unit);
			}
			pstat.close();
			
			String startDate = "";
			String endDate = "";
			try {
				startDate = DateUtil.format.format(DateUtil.timestampFormat.parse(timestamp)) + " 00:00:00";
				endDate =  DateUtil.format.format(DateUtil.timestampFormat.parse(timestamp)) + " 23:23:59";
			} catch (ParseException e) {
				e.printStackTrace();
			}

			sql = "select wrapperId,otherChange,changeTime from bookingcheckdetail where changeTime >= ? and changeTime <= ?";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, startDate);
			pstat.setString(2, endDate);
			rs = pstat.executeQuery();
			while (rs.next()) {
				String otherChange = rs.getString("otherChange");
				String datetime = rs.getString("changeTime");
				String wrapperId = rs.getString("wrapperId");
				try {
					datetime = DateUtil.timestampFormat.format(DateUtil.fullDateTimeFormat.parse(datetime));
				} catch (ParseException e) {
					continue;
				}
				BookingCheckReport unit = tMap.get(wrapperId + "-" + datetime);
				if (unit == null) continue;
				if ("无法获得价格".equals(otherChange)) {
					unit.emptyPriceCount++;
				} else {
					unit.priceChangeCount++;
				}
			}
			pstat.close();
			
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
		
		Map<String,String> wrapperMap = new TreeMap<String, String>();
		String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
		
		ManageClassLoader cl = ManageClassLoader.getInstance();
		File f = new File(base);
		if (f.exists() && f.isDirectory()) {
			for(File wrapperDir : f.listFiles()) {
				
				if (wrapperDir.isDirectory()) {
					String wrapperId = wrapperDir.getName();
					File cfg = new File(base + wrapperId + File.separator + "wrapper.n3");
					String parser = LoaderUtil.getParser(cfg);
					if (parser == null) continue; 
					AbstractDealsParse parse;
					try {
						parse = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
						wrapperId = parse.getWrapperId();
						wrapperMap.put(wrapperId, parse.getWrapperName());		
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		for(BookingCheckReport unit : result) {
			int total = unit.getBookingCount();
			if (unit.getEmptyPriceCount() > total /2) {
				MailSender.send("度假wrapper[" + unit.getWrapperId() + "(" + wrapperMap.get(unit.getWrapperId()) + ")]booking校验获得价格失败率超50%" , "");
			}
			if (unit.getPriceChangeCount() > total /2) {
				MailSender.send("度假wrapper[" + unit.getWrapperId() + "(" + wrapperMap.get(unit.getWrapperId()) + ")]booking校验获得价格变化率超50%" , "");
			}
			if (unit.getOfflineCount() > total /2) {
				MailSender.send("度假wrapper[" + unit.getWrapperId() + "(" + wrapperMap.get(unit.getWrapperId()) + ")]booking线路下线率超50%" , "");
			}
		}
	}
	public void monitor() {
		Date now = new Date();		
		
		Date startDate = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		String tryTime = DateUtil.format.format(now) + " 01:00:00";
		try {
			Date tryDate = DateUtil.fullDateTimeFormat.parse(tryTime);
			if (now.getTime() <= tryDate.getTime()) {
				 startDate = tryDate;
			} else {
				 cal.add(Calendar.DATE, 1);
				 startDate = DateUtil.fullDateTimeFormat.parse( DateUtil.format.format(cal.getTime()) + " 01:00:00");
			} 			 
		} catch(Exception e) {
			startDate = now;
		}
		
		Timer timer = new Timer(); 
		timer.schedule( new TimerTask() {

			@Override
			public void run() {
				try {
					Date tNow = new Date();
					Calendar cal = Calendar.getInstance();
					cal.setTime(tNow);
					cal.add(Calendar.DATE, -1);
					String timestamp = DateUtil.timestampFormat.format(cal.getTime());
					monitor(timestamp);
				} catch(Exception e) {
					
				}
				
			}
			
		} , startDate, 24*60*60*1000L);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println(int(Math.random() * 4000L));
		
//		instance.getBookingCheckReport("20100902", "20100929");
//		instance.getBookingCheckUnits("jingchunwang", "2010-09-25", 0, 1, new ArrayList(), "");
		instance.monitor();
	}
	
	

}
