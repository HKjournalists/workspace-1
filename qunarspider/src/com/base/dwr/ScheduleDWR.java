package com.base.dwr;

import com.qunar.deals.ScheduleManager;

public class ScheduleDWR {

	
	public String scheduleCrawler(String wrapperId, String type) {
		ScheduleManager manager = ScheduleManager.getInstance();
		return manager.schedule(wrapperId, type);
	}
	
	public String setDuration(String wrapperId, String type, String value) {
		ScheduleManager manager = ScheduleManager.getInstance();
		return manager.setDuration(wrapperId, type, value);
	}
	
	public String offlineCrawler(String wrapperId) {
		ScheduleManager manager = ScheduleManager.getInstance();
		return manager.offlineCrawler(wrapperId);
	}
	
	public String onlineCrawler(String wrapperId) {
		ScheduleManager manager = ScheduleManager.getInstance();
		return manager.onlineCrawler(wrapperId);
	}
}
