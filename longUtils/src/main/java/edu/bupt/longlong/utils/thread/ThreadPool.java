package edu.bupt.longlong.utils.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.bupt.longlong.utils.thread.ThreadPoolTask;


public class ThreadPool {

    public static final int DEFAULT_MINIMIUMPOOLSIZE = 10;
    public static final int DEFAULT_MAXIMUMPOOLSIZE = 10;
    public static final long DEFAULT_KEEPALIVETIME = 1 * 10000;
    public static final long DEFAULT_THREADTIMEOUT = 4 * 60000;
    public static final int DEFAULT_QUEUESIZE = 10;
    private int corePoolSize = DEFAULT_MINIMIUMPOOLSIZE;
    private int maxPoolSize = DEFAULT_MAXIMUMPOOLSIZE;
    // Thread keep alive time in milliseconds
    private long keepAliveTime = DEFAULT_KEEPALIVETIME;
    // Thread time out in milliseconds.
    private ThreadPoolExecutor threadPool = null;
    private List<String> elementList = new ArrayList<String>();
    
    public ThreadPool(List<String> elementList) {
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue(), new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r,
                    ThreadPoolExecutor executor) {
                ThreadPoolTask task = (ThreadPoolTask) r;
            }
        });
        this.elementList = elementList;
    }
    private List<ThreadPoolTask> tasks = new ArrayList<ThreadPoolTask>();

    private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(maxPoolSize);

    public void execute() {
        for (int index = 0; index < elementList.size(); index++) {
            String element = elementList.get(index);
            try {
                queue.put(element);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            try {
                ThreadPoolTask task = new ThreadPoolTask(element, queue);
                tasks.add(task);
                task.setPool(this);
                threadPool.execute(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
    }
}
