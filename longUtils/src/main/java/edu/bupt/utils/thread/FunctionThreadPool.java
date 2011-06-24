/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package edu.bupt.utils.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 
 * @author oulong
 */
public class FunctionThreadPool {

	private static Logger logger = Logger.getLogger(FunctionThreadPool.class);
	public static final int DEFAULT_MINIMIUMPOOLSIZE = 10;
	public static final int DEFAULT_MAXIMUMPOOLSIZE = 10;
	public static final long DEFAULT_KEEPALIVETIME = 1 * 10000;
	public static final long DEFAULT_THREADTIMEOUT = 4 * 60000;
	public static final int DEFAULT_QUEUESIZE = 10;
	private int corePoolSize = DEFAULT_MINIMIUMPOOLSIZE;
	private int maxPoolSize = DEFAULT_MAXIMUMPOOLSIZE;
	// Thread keep alive time in milliseconds
	private long keepAliveTime = DEFAULT_KEEPALIVETIME;
	// Thread time out in milliseconds.
	private ThreadPoolExecutor threadPool = null;
	private List<String> infoList = new ArrayList<String>();

	public FunctionThreadPool(List<String> infoList) {
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.CallerRunsPolicy());

		new BoundedExecutor(threadPool, maxPoolSize);
		this.infoList = infoList;
	}

	private List<FunctionThreadPoolTask> tasks = new ArrayList<FunctionThreadPoolTask>();

	private BlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(maxPoolSize);

	//    private BoundedJobBlockingQueue urlQueue = new BoundedJobBlockingQueue(maxPoolSize);

	public void execute() {
		for (int index = 0; index < infoList.size(); index++) {
			String informationn = infoList.get(index);
			try {
				urlQueue.put(informationn);
			} catch (InterruptedException e1) {
				logger.error("Interrupted.", e1);
			}

			try {
				FunctionThreadPoolTask task = new FunctionThreadPoolTask(informationn, urlQueue);
				tasks.add(task);
				task.setPool(this);
				threadPool.execute(task);
				//                boundedExecutor.submitTask(task);
			} catch (Exception e) {
				logger.error("Executing error.", e);
			}
		}
		threadPool.shutdown();
	}
}
