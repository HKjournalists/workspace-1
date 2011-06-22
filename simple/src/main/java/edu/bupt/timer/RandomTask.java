package edu.bupt.timer;

import java.util.Date;
import java.util.Random;
import java.util.TimerTask;

public class RandomTask extends TimerTask {

	@Override
	public void run() {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		System.out.println(new Date() + "," + sb.toString());
	}
}
