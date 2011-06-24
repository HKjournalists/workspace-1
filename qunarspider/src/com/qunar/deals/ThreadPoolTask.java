package com.qunar.deals;

import java.io.Serializable;


/**
 * 线程池执行的任务
 * 
 * @author jinfeng.zhang
 * 
 */
public class ThreadPoolTask implements Runnable, Serializable {
	private static final long serialVersionUID = 0;
	// 保存任务所需要的数据
	private String threadPoolTaskData;
	ThreadPoolTask(String tasks) {
		this.threadPoolTaskData = tasks;
	}

	public void run() {
		try {
			System.out.println("com.qunar.deals.extract."+threadPoolTaskData);
			Class c = Class.forName("com.qunar.deals.extract."+threadPoolTaskData);
			ExtractInterface extract = (ExtractInterface)c.newInstance();
			extract.process();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object getTask() {
		return this.threadPoolTaskData;
	}
	
	 
	
	public static void main(String args[]){
		ThreadPoolTask t = new ThreadPoolTask("Best517Html");
		t.run();
	}
}
