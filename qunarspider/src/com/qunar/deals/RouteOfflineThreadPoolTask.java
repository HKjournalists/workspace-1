package com.qunar.deals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.CheckUnit;


/**
 * 线程池执行的任务
 * 
 * @author jinfeng.zhang
 * 
 */
public class RouteOfflineThreadPoolTask implements Runnable, Serializable {
	static final Log logger = LogFactory.getLog(RouteOfflineThreadPoolTask.class);
	private static final long serialVersionUID = 0;
	// 保存任务所需要的数据
	private String threadPoolTaskData;
	private List<CheckUnit> units;
	
	private AbstractDealsParse parse;
	RouteOfflineThreadPoolTask(AbstractDealsParse parse, List<CheckUnit> units) {
		this.parse = parse;
		threadPoolTaskData = parse.getWrapperId();
		this.units = units;
	}
	private boolean finished = false;
	private static String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	private RouteOfflineThreadPool pool = null;
	public void run() {
		try {
			logger.info("开始调度RouteOffline" + threadPoolTaskData);
			InputStream in = null;
			Properties props = new Properties();
			try {
				in = new FileInputStream(base + parse.getWrapperId() + File.separator + "wrapper.n3");//parse.getClass().getClassLoader().getResourceAsStream("wrapper.n3");
				props.load(in);
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if ("interface".equals(props.getProperty("type"))) { //接口类型不recheck
	
			} else {
				parse.process4(units);
			}			
			if (pool != null) {
				logger.info("结束调度RouteOffline" + threadPoolTaskData);
				pool.notifyPool(this);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished() {
		finished = true;
	}

	


	public void setPool(RouteOfflineThreadPool pool) {
		this.pool = pool;
	}


	public Object getTask() {
		return this.threadPoolTaskData;
	}
	
	 
	
	public static void main(String args[]){
	
	}
}
