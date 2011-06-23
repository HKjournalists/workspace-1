/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bupt.longlong.utils;

import java.io.Console;
import java.util.Arrays;

/**
 *
 * @author oulong
 */
public class SampleTest {

    /*只有在命令行中才能获得Console对象*/
    public static void ConsoleTest() {
        Console console = System.console();  // 获得Console实例对象
        if (console != null) {               // 判断是否有控制台的使用权
            String user = console.readLine("Enter username:");       // 读取整行字符
            String pwd = new String(console.readPassword("Enter passowrd:"));    // 读取密码,输入时不显示
            console.printf("Username is: " + user + "\n");       // 显示用户名
            console.printf("Password is: " + pwd + "\n");    // 显示密码
        } else {
            System.out.println("Console is unavailable.");   // 提示无控制台使用权限
        }
    }

    public static void main(String[] args) {
        int[] sample = new int[10];
        for (int i = 0; i < 7; i++) {
            sample[i] = i + 1;
        }

        int[] result = Arrays.copyOfRange(sample, 2, 4);

        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.binarySearch(sample, 8));
        Arrays.fill(sample, 30);
        System.out.println(Arrays.toString(sample));
    }
}
