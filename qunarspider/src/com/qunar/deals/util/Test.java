package com.qunar.deals.util;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qunar.database.DataSet;

public class Test {
	protected static final Log logger = LogFactory.getLog(Test.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String[][] rows = DataSet.query("sight", "select count(*) from route", 0, 0);
		int x= 3;
	}

}
