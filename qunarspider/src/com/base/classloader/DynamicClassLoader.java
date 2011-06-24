package com.base.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicClassLoader extends ClassLoader {
	
	
	// 定义文件所在目录
	private static final String DEAFAULTDIR = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	// 定义文件绝对路径
	
	private String wrapperId;
	
	public DynamicClassLoader(String wrapperId) {
		this.wrapperId = wrapperId;
	}
	
	public static String FormatClassName(String name, String wrapperId) {

		String result = DEAFAULTDIR + wrapperId + File.separator + name + ".class";
		return result;
	}
	
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = null;
		try {
			String filePath = FormatClassName(name, wrapperId);
			b = loadClassData(filePath);
		} catch (Exception e) {
			String filePath = FormatClassName(name, wrapperId);
			String[] classNameSplits = filePath.split("/|\\\\");
			String className = classNameSplits[classNameSplits.length - 1].replace(".class", "");
			return Class.forName(className);
		}
		return defineClass(name, b, 0, b.length);

	}
	
	private byte[] loadClassData(String filepath) throws Exception {
		int n = 0;
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(
				new File(filepath)));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((n = br.read()) != -1) {
			bos.write(n);
		}
		br.close();
		return bos.toByteArray();
	}
}
