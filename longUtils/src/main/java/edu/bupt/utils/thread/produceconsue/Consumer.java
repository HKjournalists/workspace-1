/**
 * Consumer.java
 */
package edu.bupt.utils.thread.produceconsue;

/**
 * @author long.ou 2011-6-14 ����03:15:33
 * 
 */
public class Consumer extends Thread {

	private int neednum; //����Ʒ������ 
	private Repository repository; //�ֿ� 

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
		//���ָ�������Ĳ�Ʒ 
		repository.consume(neednum);
	}

}
