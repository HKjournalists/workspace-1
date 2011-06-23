package com.qunar.deals;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.base.classloader.LoaderUtil;
import com.base.classloader.ManageClassLoader;
import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.CheckUnit;
import com.qunar.deals.util.MyClient;

/**
 * 
 * @author jinfeng.zhang
 * 
 */
public class RouteOfflineThreadPool {
	static final Log logger = LogFactory.getLog(RouteOfflineThreadPool.class);
	public static final int DEFAULT_MINIMIUMPOOLSIZE = 20;
	public static final int DEFAULT_MAXIMUMPOOLSIZE = 40;
	public static final long DEFAULT_KEEPALIVETIME = 1 * 10000;
	public static final long DEFAULT_THREADTIMEOUT = 4 * 60000;
	public static final int DEFAULT_QUEUESIZE = 30;
	private int corePoolSize = DEFAULT_MINIMIUMPOOLSIZE;
	private int maxPoolSize = DEFAULT_MAXIMUMPOOLSIZE;
	private int blockingQueueSize = DEFAULT_QUEUESIZE;
	public static SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	// Thread keep alive time in milliseconds
	private long keepAliveTime = DEFAULT_KEEPALIVETIME;
	// Thread time out in milliseconds.
	private ThreadPoolExecutor threadPool = null;
	private static int produceTaskSleepTime = 2000;
	public final static String EXTRACT_BASE_PATH = ResourceBundle.getBundle(
	"file").getString("htmlPath");
	public final static String PARSE_BASE_PATH = ResourceBundle.getBundle(
	"file").getString("xmlpath");

	public RouteOfflineThreadPool() {
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS, workQueue, handler);
	}

	private List<RouteOfflineThreadPoolTask> tasks = new ArrayList<RouteOfflineThreadPoolTask>();
	
	public static void main(String[] args) {
		RouteOfflineThreadPool pool = new RouteOfflineThreadPool();
		pool.parseExecute();
	}


	public void notifyPool(RouteOfflineThreadPoolTask task) {
		task.setFinished();
		boolean allFinished = true;
		logger.info("RouteOffline总任务数:" + tasks.size());
		for(int i = 0; i < tasks.size(); i++) {
			logger.info(tasks.get(i).isFinished());
			if (!tasks.get(i).isFinished()) {
				allFinished = false;
				break;
			}
		}		
		if (allFinished) {			
			logger.info("结束一次调度RouteOffline!");
			//AbstractDealsParse.flushRouteOffline();
			
		}
	}
	
	public static Map<String, List<CheckUnit>> getCheckUnits() {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;	
		Map<String, List<CheckUnit>> result = new HashMap();
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			stat = conn.createStatement();
			String sql = "Select id, wrapperId, listInfo from route where listInfo is not null and listInfo <> '' and status in (0,1,3) and dateOfLoad not like '" + format.format(new Date()) + "%'";
			System.out.println(sql);
			rs = conn.createStatement().executeQuery(sql);
			while(rs.next()) {
				int id = rs.getInt(1);
				String listInfo = rs.getString(3);
				String wrapperId = rs.getString(2);
				CheckUnit unit = new CheckUnit();
				unit.setId(id);
				unit.setListInfo(listInfo);
				unit.setWrapperId(wrapperId);
				if (result.containsKey(wrapperId)) {
					result.get(wrapperId).add(unit);
				} else {
					List<CheckUnit> units = new ArrayList();
					units.add(unit);
					result.put(wrapperId, units);
				}
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
	
	public void parseExecute() {
		logger.info("开始一次调度RouteOffline!");
		tasks.clear();
		Map<String, List<CheckUnit>> checkUnits = getCheckUnits();
		ManageClassLoader cl = ManageClassLoader.getInstance();
		String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
		File f = new File(base);
		String noUseParseRoutes = ResourceBundle.getBundle("nouseclass").getString("no_use_extract_class");
		String[] noRoutes = noUseParseRoutes.split(",");
		Set<String> noUseParseRoutesSet = new HashSet();
		for(String s : noRoutes) {
			noUseParseRoutesSet.add(s);
		}
		if (f.exists() && f.isDirectory()) {
			for(String wrapperId : checkUnits.keySet()) {
				if (noUseParseRoutesSet.contains(wrapperId)) continue;
				File config = new File(base + wrapperId + File.separator + "wrapper.n3");
				String parser = LoaderUtil.getParser(config);
				if (parser == null) continue; 
				AbstractDealsParse parse;
				try {
					parse = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
					RouteOfflineThreadPoolTask parseTask = new RouteOfflineThreadPoolTask(parse, checkUnits.get(wrapperId));
					tasks.add(parseTask);
					parseTask.setPool(this);
					threadPool.execute(parseTask);				
					Thread.sleep(produceTaskSleepTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (tasks.size() == 0) {
			logger.info("结束一次调度Recheck!");
			//AbstractDealsParse.flushCachedValue();
		}
	}

	private ThreadPoolExecutor getThreadPool() {
		return threadPool;

	}

	BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
			blockingQueueSize);

	RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();  
//	{
//		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
//			logger.info("1 task is rejected: "
//					+ " total rejected: . Current thread count: "
//					+ executor.getActiveCount() + " queue count: "
//					+ executor.getQueue().size());
//			// Only record spec wrappers
//
//		}
//	};


}
