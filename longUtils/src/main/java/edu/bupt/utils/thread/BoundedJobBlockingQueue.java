/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

/**
 *
 * @author oulong
 */
public class BoundedJobBlockingQueue {

    private static Logger logger = Logger.getLogger(BoundedJobBlockingQueue.class);
    private final BlockingQueue<String> jobs = new PriorityBlockingQueue<String>();
    private final Semaphore sem;

    public BoundedJobBlockingQueue(int bound) {
        sem = new Semaphore(bound);
    }

    public boolean put(String job) throws InterruptedException {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            logger.error("Interrupted.", e);
        }
        boolean wasAdded = false;
        try {
            wasAdded = jobs.add(job);
            return wasAdded;
        } finally {
            if (!wasAdded) {
                sem.release();
            }
        }
    }

    public String take() throws InterruptedException {
        String job = jobs.take();
        if (job != null) {
            sem.release();
        }
        return job;
    }
}
