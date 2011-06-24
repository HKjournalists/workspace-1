package com.qunar.deals.util;

import java.util.HashSet;
import java.util.Set;

public class HtmlUtil {

	private static Set<String> newLineTag = new HashSet<String> ();
	private static Set<String> unVisibleTag = new HashSet<String> ();
	static {
		newLineTag.add("TR");
		newLineTag.add("P");
		newLineTag.add("DIV");
		newLineTag.add("BR");
		
		unVisibleTag.add("STYLE");
	}
	
	public static boolean isNewLineTag(String tagName) {
		return newLineTag.contains(tagName);
	}
	
	
	
}
