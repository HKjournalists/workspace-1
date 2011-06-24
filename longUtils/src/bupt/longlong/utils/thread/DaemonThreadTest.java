/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bupt.longlong.utils.thread;

import java.io.IOException;

public class DaemonThreadTest extends Thread {

    private static volatile double d = 1;

    public DaemonThreadTest() {
        super.setDaemon(true);
        start();
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while (true) {
//            d = d + (Math.E + Math.PI) / d;
            d = System.currentTimeMillis() - start;
        }
    }

    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        new DaemonThreadTest();
        Thread.sleep(300);
        System.in.read();
        System.out.println(d + "ms");
    }
}
