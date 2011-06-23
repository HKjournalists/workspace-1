/**
 * Consumer.java
 */
package edu.bupt.concurrency.produconsu;

/**
 * @author long.ou 2011-6-14 下午03:15:33
 * 
 */
public class Consumer extends Thread {

	private int neednum; //生产产品的数量 
	private Repository repository; //仓库 

	public Consumer(int neednum, Repository repository) {
		this.neednum = neednum;
		this.repository = repository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//消费指定数量的产品 
		repository.consume(neednum);
	}

}
