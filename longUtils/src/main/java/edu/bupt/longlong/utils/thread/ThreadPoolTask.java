package edu.bupt.longlong.utils.thread;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public class ThreadPoolTask implements Runnable, Serializable {

    private String element;
    private BlockingQueue<String> urlQueue;
   
    ThreadPoolTask(String element, BlockingQueue<String> urlQueue) {
        this.element = element;
        this.urlQueue = urlQueue;
    }
    
    public void setPool(ThreadPool pool) {
    }

    public void run() {
        try {
           // 单个线程的处理逻辑(需要的参数为element)
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlQueue.remove(element);
        }
    }
}