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
public class RecheckThreadPoolTask implements Runnable, Serializable {
	static final Log logger = LogFactory.getLog(RecheckThreadPoolTask.class);
	private static final long serialVersionUID = 0;
	// 保存任务所需要的数据
	private String threadPoolTaskData;
	private List<CheckUnit> units;
	
	public String getThreadPoolTaskData() {
		return threadPoolTaskData;
	}
	private AbstractDealsParse parse;
	RecheckThreadPoolTask(AbstractDealsParse parse, List<CheckUnit> units) {
		this.parse = parse;
		threadPoolTaskData = parse.getWrapperId();
		this.units = units;
	}
	private boolean finished = false;
	private static String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	private RecheckThreadPool pool = null;
	public void run() {
		try {
			logger.info("开始调度Recheck" + threadPoolTaskData);
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
				parse.process3(units);
			}			
			if (pool != null) {
				logger.info("结束调度Recheck" + threadPoolTaskData);
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

	


	public void setPool(RecheckThreadPool pool) {
		this.pool = pool;
	}


	public Object getTask() {
		return this.threadPoolTaskData;
	}
	
	 
	
	public static void main(String args[]){
	
	}
}
