package edu.bupt.quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MyQuartzJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(MyQuartzJob.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LOGGER.info("MyQuartz-" + new Date());
	}
}
