package com.base.classloader;

import java.io.File;
import java.util.ResourceBundle;

import com.qunar.deals.parse.AbstractDealsParse;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception{
		ManageClassLoader cl = ManageClassLoader.getInstance();
		String base = ResourceBundle.getBundle("file").getString("wrapper.classpath");
		File f = new File(base);
		if (f.exists() && f.isDirectory()) {
			for(File wrapperDir : f.listFiles()) {
				if (wrapperDir.isDirectory()) {
					String wrapperId = wrapperDir.getName();
					File config = new File(base + wrapperId + File.separator + "wrapper.n3");
					String parser = LoaderUtil.getParser(config);
					if (parser == null) continue;
					AbstractDealsParse parse = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
					parse.process();
				}
			}
		}
		
	}

}
