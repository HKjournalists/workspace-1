package edu.bupt.timer;

import java.util.Timer;

public class TimerTest {

	public static void main(String[] args) {
		Timer t = new Timer();

		//һ���Դ�������1000�����ʼ����  
		//		t.schedule(new HelloTask(), 1000);

		//�̶�ʱ�����  
		//		Calendar cal = Calendar.getInstance();
		//		cal.set(2010, Calendar.OCTOBER, 27);
		//		t.schedule(new HelloTask(), cal.getTime());

		//�����ظ�����,�̶��ӳٴ�����  
		t.schedule(new RandomTask(), 1000, 3000);

		//��ʱ������  
		//		t.scheduleAtFixedRate(new HelloTask(), 1000, 1000);

		Timer t2 = new Timer();
		t2.scheduleAtFixedRate(new HelloTask(), 0, 5000);
	}
}
