/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.utils.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author oulong
 */
public class JDKThreadPool {

    public static void testJDKThreadPool() {
        //        Executor executor = Executors.newFixedThreadPool(10);
        Executor executor = Executors.newCachedThreadPool();
        Runnable task = new Runnable() {

            @Override
            public void run() {
                System.out.println("task over");
            }
        };
        executor.execute(task);
        ExecutorService executorService = (ExecutorService) executor;
        executorService.shutdown();

        //定时器
        executor = Executors.newScheduledThreadPool(10);
        ScheduledExecutorService scheduler = (ScheduledExecutorService) executor;
        scheduler.scheduleAtFixedRate(task, 20, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
    }

    class Runner implements Runnable {

        private int i;

        public Runner(int i) {
            this.i = i;
        }

        public void run() {
            System.out.println("Thread" + i + " is working...");
            int sum = 0;
            for (int i = 0; i < 10000000; i++) {
                sum += i;
            }
        }
    }
}
