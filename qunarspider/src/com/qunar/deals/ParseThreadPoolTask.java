package com.qunar.deals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qunar.deals.parse.AbstractDealsParse;


/**
 * 线程池执行的任务
 * 
 * @author jinfeng.zhang
 * 
 */
public class ParseThreadPoolTask implements Runnable, Serializable {
	static final Log logger = LogFactory.getLog(ParseThreadPoolTask.class);
	private static final long serialVersionUID = 0;
	// 保存任务所需要的数据
	private String threadPoolTaskData;
	
	
	public String getThreadPoolTaskData() {
		return threadPoolTaskData;
	}


	@Deprecated
	ParseThreadPoolTask(String tasks) {
		this.threadPoolTaskData = tasks;
	}
	
	private AbstractDealsParse parse;
	ParseThreadPoolTask(AbstractDealsParse parse) {
		this.parse = parse;
		threadPoolTaskData = parse.getWrapperId();
	}
	private boolean finished = false;
	private static String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	private DealsSpiderThreadPool pool = null;
	public void run() {
		try {
			logger.info("开始调度" + threadPoolTaskData);
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
			if ("interface".equals(props.getProperty("type"))) {
				parse.process2();
			} else {
				parse.process();
			}			
			if (pool != null) {
				logger.info("结束调度" + threadPoolTaskData);
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

	


	public void setPool(DealsSpiderThreadPool pool) {
		this.pool = pool;
	}


	public Object getTask() {
		return this.threadPoolTaskData;
	}
	
	 
	
	public static void main(String args[]){
		ParseThreadPoolTask t = new ParseThreadPoolTask("Best517Html");
		t.run();
	}
}
