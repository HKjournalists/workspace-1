package com.qunar.deals;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.base.classloader.LoaderUtil;
import com.base.classloader.ManageClassLoader;
import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.MyClient;

/**
 * 
 * @author jinfeng.zhang
 * 
 */
public class DealsSpiderThreadPool {
	static final Log logger = LogFactory.getLog(DealsSpiderThreadPool.class);
	public static final int DEFAULT_MINIMIUMPOOLSIZE = 20;
	public static final int DEFAULT_MAXIMUMPOOLSIZE = 40;
	public static final long DEFAULT_KEEPALIVETIME = 1 * 10000;
	public static final long DEFAULT_THREADTIMEOUT = 4 * 60000;
	public static final int DEFAULT_QUEUESIZE = 30;
	private int corePoolSize = DEFAULT_MINIMIUMPOOLSIZE;
	private int maxPoolSize = DEFAULT_MAXIMUMPOOLSIZE;
	private int blockingQueueSize = DEFAULT_QUEUESIZE;
	// Thread keep alive time in milliseconds
	private long keepAliveTime = DEFAULT_KEEPALIVETIME;
	// Thread time out in milliseconds.
	private ThreadPoolExecutor threadPool = null;
	private static int produceTaskSleepTime = 2000;
	public final static String EXTRACT_BASE_PATH = ResourceBundle.getBundle(
	"file").getString("htmlPath");
	public final static String PARSE_BASE_PATH = ResourceBundle.getBundle(
	"file").getString("xmlpath");

	public DealsSpiderThreadPool() {
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS, workQueue, handler);
	}

	private List<ParseThreadPoolTask> tasks = new ArrayList<ParseThreadPoolTask>();
	String finishUrl = ResourceBundle.getBundle("file").getString("FinishUrl");
	
	public static void main(String[] args) {
		DealsSpiderThreadPool pool = new DealsSpiderThreadPool();
		pool.parseExecute();
	}


	public void notifyPool(ParseThreadPoolTask task) {
		task.setFinished();
		boolean allFinished = true;
		logger.info("总任务数:" + tasks.size());
		for(int i = 0; i < tasks.size(); i++) {
			logger.info(tasks.get(i).getThreadPoolTaskData() + ":" + tasks.get(i).isFinished());
			if (!tasks.get(i).isFinished()) {
				allFinished = false;
				break;
			}
		}		
		if (allFinished) {
			MyClient client = new MyClient();			
			client.getHtml(finishUrl, "utf-8");
			logger.info("结束一次调度!");
//			AbstractDealsParse.flushCachedValue();
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Thread(new Runnable(){
				@Override
				public void run() {
					logger.info("初始化Cache for Recheck");
					//AbstractDealsParse.initCachedValue();
					RecheckThreadPool p = new RecheckThreadPool();
					p.parseExecute();
				}}).start();
			
			//System.out.println("结束了！");
		}
	}
	
	public void parseExecute() {
		logger.info("开始一次调度!");
		tasks.clear();
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
			for(File wrapperDir : f.listFiles()) {
				if (noUseParseRoutesSet.contains(wrapperDir.getName())) continue;
				if (wrapperDir.isDirectory()) {
					String wrapperId = wrapperDir.getName();
					File config = new File(base + wrapperId + File.separator + "wrapper.n3");
					String parser = LoaderUtil.getParser(config);
					if (parser == null) continue; 
					AbstractDealsParse parse;
					try {
						parse = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
						ParseThreadPoolTask parseTask = new ParseThreadPoolTask(parse);
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
