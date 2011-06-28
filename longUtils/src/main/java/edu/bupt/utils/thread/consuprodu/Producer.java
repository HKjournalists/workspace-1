package edu.bupt.utils.thread.consuprodu;

/**
 * @author long.ou 2011-6-14 ����03:13:52
 * 
 */
public class Producer extends Thread {

	private int neednum; //����Ʒ������ 
	private Repository repository; //�ֿ� 

	public Producer(int neednum, Repository repository) {
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
		repository.produce(neednum);
	}

}
