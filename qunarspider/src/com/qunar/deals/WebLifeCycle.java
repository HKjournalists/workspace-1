package com.qunar.deals;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



/**
 * 
 * @author jinfeng.zhang
 *
 */
public class WebLifeCycle implements ServletContextListener {
	
    public void contextInitialized(ServletContextEvent sce) {    	
    	
    	ScheduleManager.getInstance().schedule();

    	BookingCheck.getInstance().schedule();
    	
    	BookingCheck.getInstance().monitor();
    }

    public void contextDestroyed(ServletContextEvent sce) {
       
    }
}
