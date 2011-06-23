package edu.bupt.reflect;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

@SuppressWarnings("restriction")
public class JavaSourceFromString extends SimpleJavaFileObject {

	/**
	 * 源码
	 */
	final String code;

	/**
	 * 构造方法：从字符串中构造一个FileObject
	 * 
	 * @param name
	 *            the name of the compilation unit represented by this file
	 *            object
	 * @param code
	 *            the source code for the compilation unit represented by this
	 *            file object
	 */
	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}

}
