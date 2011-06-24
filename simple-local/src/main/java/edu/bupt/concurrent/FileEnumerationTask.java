/**
 * SearchTask.java
 */
package edu.bupt.concurrent;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * @author long.ou 2011-6-21 下午09:53:45
 * 
 */
public class FileEnumerationTask implements Runnable {

	public FileEnumerationTask(BlockingQueue<File> queue, File startingDirectory) {
		this.queue = queue;
		this.startingDirectory = startingDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			enumerate(startingDirectory);
			queue.put(DUMMY);
		} catch (InterruptedException e) {
			System.out.println("FileEnumerationTask Exception");
		}
	}

	public void enumerate(File directory) throws InterruptedException {
		File[] files = directory.listFiles();
		try {
			for (File file : files) {
				if (file.isDirectory())
					enumerate(file);
				else queue.put(file);
			}
		} catch (NullPointerException e) {
			System.out.println(e);
			Thread.sleep(10000);
		}

	}

	public static File DUMMY = new File("");

	private BlockingQueue<File> queue;
	private File startingDirectory;
}
