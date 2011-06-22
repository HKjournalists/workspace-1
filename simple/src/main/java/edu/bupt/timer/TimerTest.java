package edu.bupt.timer;

import java.util.Timer;

public class TimerTest {

	public static void main(String[] args) {
		Timer t = new Timer();

		//一次性触发器，1000毫秒后开始运行  
		//		t.schedule(new HelloTask(), 1000);

		//固定时间调用  
		//		Calendar cal = Calendar.getInstance();
		//		cal.set(2010, Calendar.OCTOBER, 27);
		//		t.schedule(new HelloTask(), cal.getTime());

		//调用重复任务,固定延迟触发器  
		t.schedule(new RandomTask(), 1000, 3000);

		//定时触发器  
		//		t.scheduleAtFixedRate(new HelloTask(), 1000, 1000);

		Timer t2 = new Timer();
		t2.scheduleAtFixedRate(new HelloTask(), 0, 5000);
	}
}
