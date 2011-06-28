/**
 * SimpleTask.java
 */
package edu.bupt.utils.thread;

/**
 * @author long.ou 2011-6-28 上午10:20:20
 */
public class SimpleTask extends Thread {

	private String signal = "";

	public SimpleTask() {

	}

	public SimpleTask(String signal) {
		this.setSignal(signal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		System.out.println(getSignal() + " " + Thread.currentThread().getName() + " -> " + System.currentTimeMillis());
	}

	/**
	 * @param signal
	 *            the signal to set
	 */
	public void setSignal(String signal) {
		this.signal = signal;
	}

	/**
	 * @return the signal
	 */
	public String getSignal() {
		return signal;
	}
}
