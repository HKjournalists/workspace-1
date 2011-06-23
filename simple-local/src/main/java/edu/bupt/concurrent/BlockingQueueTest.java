/**
 * BlockingQueueTest.java
 */
package edu.bupt.concurrent;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Administrator 2011-6-21 下午09:46:57
 * 
 */
public class BlockingQueueTest {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter base directory: ");
		String directory = in.nextLine();
		System.out.println("Enter keyword: ");
		String keyword = in.nextLine();

		final int queue_size = 10;
		final int search_threads = 100;

		BlockingQueue<File> queue = new ArrayBlockingQueue<File>(queue_size);

		FileEnumerationTask enumerator = new FileEnumerationTask(queue, new File(directory));
		new Thread(enumerator).start();
		for (int i = 1; i <= search_threads; i++) {
			new Thread(new SearchTask(queue, keyword)).start();
		}
	}
}