/**
 * Repository.java
 */
package edu.bupt.utils.thread.produceconsue;

/**
 * @author long.ou 2011-6-14 ����03:08:02
 * 
 */
public class Repository {

	public static final int MAX_SIZE = 100; //������� 
	public int curnum; //��ǰ����� 

	public Repository() {}

	public Repository(int curnum) {
		this.curnum = curnum;
	}

	/**
	 * ���ָ�������Ĳ�Ʒ
	 * 
	 * @param neednum
	 */
	public synchronized void produce(int neednum) {
		//�����Ƿ���Ҫ��� 
		while (neednum + curnum > MAX_SIZE) {
			System.out.println("Ҫ���Ĳ�Ʒ����" + neednum + "����ʣ������" + (MAX_SIZE - curnum) + "����ʱ����ִ���������!");
			try {
				//��ǰ������̵߳ȴ� 
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//�������������������������򵥵ĸ�ĵ�ǰ����� 
		curnum += neednum;
		System.out.println("�Ѿ������" + neednum + "����Ʒ���ֲִ���Ϊ" + curnum);
		//�����ڴ˶���������ϵȴ�������߳� 
		notifyAll();
	}

	/**
	 * ���ָ�������Ĳ�Ʒ
	 * 
	 * @param neednum
	 */
	public synchronized void consume(int neednum) {
		//�����Ƿ����� 
		while (curnum < neednum) {
			try {
				//��ǰ������̵߳ȴ� 
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//��������������������ѣ�����򵥵ĸ�ĵ�ǰ����� 
		curnum -= neednum;
		System.out.println("�Ѿ������" + neednum + "����Ʒ���ֲִ���Ϊ" + curnum);
		//�����ڴ˶���������ϵȴ�������߳� 
		notifyAll();
	}
}
