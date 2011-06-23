/**
 * MyTask.java
 */
package edu.bupt.concurrency.threadpool;

/**
 * @author long.ou 2011-6-14 下午04:55:23
 * 
 */
public class MyTask implements Runnable {

	private Object attachData;

	public MyTask(Object tasks) {
		this.attachData = tasks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("开始执行任务：" + attachData);
		attachData = null;
	}

	public Object getTask() {
		return this.attachData;
	}

}
