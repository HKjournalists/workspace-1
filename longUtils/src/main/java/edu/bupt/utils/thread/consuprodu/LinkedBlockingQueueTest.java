/**
 * LinkedBlockingQueueTest.java
 */
package edu.bupt.utils.thread.consuprodu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.bupt.utils.thread.SimpleTask;

/**
 * @author long.ou 2011-6-28 下午06:18:23
 */
public class LinkedBlockingQueueTest {

	/**
	 * 有两种方法向线程池提交任务： 1 execute(E); 2 workQueue.add(E)。
	 * LinkedBlockingQueue线程安全的。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor producePool = new ThreadPoolExecutor(2, 5, 6000, TimeUnit.MILLISECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());

		workQueue.add(new SimpleTask("other"));

		for (int i = 0; i < 3; i++) {
			producePool.execute(new SimpleTask(Integer.toString(i)));
		}

		while (true) {
			workQueue.add(new SimpleTask("end"));
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
