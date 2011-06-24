/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils;

/**
 *
 * @author oulong
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;

/*
 *即向虚拟机添加关闭的钩子程序.
 *请注意, 该 hook 线程必须是已经初始化但是没有运行的线程, 这个线程将在虚拟机响应用户的中断之前运行
 */
public class ShutdownHookTest extends JFrame {

    private FileWriter fw_log;
    private BufferedWriter bw_log;

    public static void main(String[] args) throws Exception {
        ShutdownHookTest frame = new ShutdownHookTest();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.processApp1();
        frame.setVisible(true);
    }

    public ShutdownHookTest() throws IOException {
        fw_log = new FileWriter("log.txt");
        bw_log = new BufferedWriter(fw_log);

        setSize(200, 100);

        setLocation(100, 100);

        setTitle("Test for ShutdownHook");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                endApp();
            }
        });
    }

    public void processApp1() throws IOException {
        bw_log.write("testing");
        bw_log.newLine();
    }

    // close the log file
    public void endApp() {
        try {
            bw_log.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
