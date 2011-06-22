/**
 * MyThreadPool.java
 */
package edu.bupt.concurrency.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author long.ou 2011-6-14 下午04:38:47
 * 
 */
public class MyThreadPoolTest {

	private static int produceTaskMaxNumber = 10;

	private final static int default_corePoolSize = 5;

	private final static int default_queueSize = 5;

	private final static int maximumPoolSize = 10;

	private final static long keepAliveTime = 60L;

	public static void main(String[] args) {

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(default_corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
				default_queueSize), new ThreadPoolExecutor.CallerRunsPolicy());
		for (int i = 1; i <= produceTaskMaxNumber; i++) {
			try {
				String task = "task@ " + i;
				System.out.println("创建任务并提交到线程池中：" + task);
				threadPool.execute(new MyTask(task));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		threadPool.shutdown();
	}
}
