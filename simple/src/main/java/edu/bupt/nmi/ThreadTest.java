package edu.bupt.nmi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ThreadTest {

	public static void main(String args[]) throws Exception {
		Process p = Runtime.getRuntime().exec("tasklist ");
		System.out.println(p);

		BufferedReader bw = new BufferedReader(new InputStreamReader(p.getInputStream()));
		System.out.println(bw.readLine());
		System.out.println(bw.readLine());
		System.out.println(bw.readLine());
		System.out.println(bw.readLine());
		System.out.println(bw.readLine());
		System.out.println(bw.readLine());
	}
}
