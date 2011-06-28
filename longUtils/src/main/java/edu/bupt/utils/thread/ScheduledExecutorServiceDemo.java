/**
 * ScheduledExecutorServiceDemo.java
 */
package edu.bupt.utils.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author long.ou 2011-6-28 上午10:13:46
 */
public class ScheduledExecutorServiceDemo {

	public static void main(String[] args) throws Exception {
		ScheduledExecutorService execService = Executors.newScheduledThreadPool(3);
		for (int i = 0; i < 3; i++) {
			execService.scheduleWithFixedDelay(new SimpleTask(Integer.toString(i)), 0, 1, TimeUnit.SECONDS);
		}
		execService.scheduleAtFixedRate(new SimpleTask("4"), 1, 1, TimeUnit.SECONDS);
		execService.scheduleWithFixedDelay(new SimpleTask("5"), 0, 1, TimeUnit.SECONDS);
	}
}
