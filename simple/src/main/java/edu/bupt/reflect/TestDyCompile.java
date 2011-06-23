package edu.bupt.reflect;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaFileManager.Location;

import junit.framework.Test;

@SuppressWarnings("restriction")
public class TestDyCompile {

	public static void main(String[] args) {

		StringBuilder classStr = new StringBuilder("package dyclass;public class Foo implements Test{");
		classStr.append("public void test(){");
		classStr.append("System.out.println(\"Foo2\");}}");

		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = jc.getStandardFileManager(null, null, null);
		Location location = StandardLocation.CLASS_OUTPUT;
		File[] outputs = new File[] { new File("bin/") };
		try {
			fileManager.setLocation(location, Arrays.asList(outputs));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JavaFileObject jfo = new JavaSourceFromString("dyclass.Foo", classStr.toString());
		JavaFileObject[] jfos = new JavaFileObject[] { jfo };
		Iterable<JavaFileObject> compilationUnits = Arrays.asList(jfos);
		boolean b = jc.getTask(null, fileManager, null, null, null, compilationUnits).call();
		if (b) {//如果编译成功     
			try {
				Test t = (Test) Class.forName("dyclass.Foo").newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
