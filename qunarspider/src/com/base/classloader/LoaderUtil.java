package com.base.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoaderUtil {

	public static String getParser(File f) {
		if (f.exists() && f.isFile()) {
			Properties p = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				p.load(fis);
				if (p.containsKey("parser")) {
					return p.getProperty("parser");
				} else {
					return null;
				}
				
			} catch(Exception e) {
				return null;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isInterface(File f) {
		if (f.exists() && f.isFile()) {
			Properties p = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				p.load(fis);
				return "interface".equals(p.getProperty("type"));				
			} catch(Exception e) {
				return false;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			return false;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
