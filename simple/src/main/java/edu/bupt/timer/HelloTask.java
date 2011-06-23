package edu.bupt.timer;

import java.util.Date;
import java.util.TimerTask;

public class HelloTask extends TimerTask {

	@Override
	public void run() {
		System.out.println(new Date() + ", Hello!");
	}
}
