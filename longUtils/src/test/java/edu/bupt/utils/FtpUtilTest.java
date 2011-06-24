/**
 * FtpUtilTest.java
 */
package edu.bupt.utils;

import org.junit.Before;
import org.junit.Test;

/**
 * @author long.ou 2011-6-24 下午01:40:02
 */
public class FtpUtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ftp = new FtpUtil("172.16.24.75", 21, "123", "123", null);
	}

	@Test
	public void testListFiles() throws Exception {
		ftp.login();
		System.out.println(ftp.isDirExist("/"));

		String[] names = ftp.listFiles(".");
		for (String name : names) {
			System.out.println(name);
			System.out.println(ftp.isDirExist(name));
		}
		ftp.logout();
	}

	private FtpUtil ftp = null;
}
