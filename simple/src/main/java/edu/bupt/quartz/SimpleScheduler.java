package edu.bupt.quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class SimpleScheduler {

	private static final Logger LOGGER = Logger.getLogger(MyQuartzJob.class);

	public static void main(String[] args) {
		SimpleScheduler simple = new SimpleScheduler();
		simple.startScheduler();
	}

	public void startScheduler() {
		Scheduler scheduler = null;

		try {
			// Get a Scheduler instance from the Factory       
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// Start the scheduler       
			scheduler.start();
			LOGGER.info("Scheduler started at " + new Date());

			scheduler.shutdown();

		} catch (SchedulerException ex) {
			// deal with any exceptions       
			LOGGER.error(ex);
		}
	}

	// Create and Schedule a MyQuartJob with the Scheduler       
	public void scheduleJob(Scheduler scheduler) throws SchedulerException {

	}

}
