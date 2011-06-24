/**
 * FileEnumerationTask.java
 */
package edu.bupt.concurrent;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * @author long.ou 2011-6-21 下午09:53:35
 * 
 */
public class SearchTask implements Runnable {

	public SearchTask(BlockingQueue<File> queue, String keyword) {
		this.queue = queue;
		this.keyword = keyword;
	}

	public void run() {
		try {
			boolean done = false;
			while (!done) {
				File file = queue.take();
				if (file == FileEnumerationTask.DUMMY) {
					queue.put(file);
					done = false;
				} else search(file);
			}
		} catch (Exception e) {
			System.out.println("Search Exception");
		}
	}

	public void search(File file) {
		String path = file.getAbsolutePath();
		if (path.contains(keyword)) System.out.println(path);
	}

	private BlockingQueue<File> queue;
	private String keyword;
}
