package edu.bupt.utils.reflection;

import java.lang.reflect.Method;

public class SampleMethod {

	public static void main(String[] args) {
		SampleMethod p = new SampleMethod();
		printMethods(p);
	}

	public static void printMethods(Object o) {
		Class c = o.getClass();
		String className = c.getName();
		Method[] m = c.getMethods();
		for (int i = 0; i < m.length; i++) {
			System.out.print(m[i].getReturnType().getName());
			System.out.print(" " + m[i].getName() + "(");
			Class[] parameterTypes = m[i].getParameterTypes();
			for (int j = 0; j < parameterTypes.length; j++) {
				System.out.print(parameterTypes[j].getName());
				if (parameterTypes.length > j + 1) {
					System.out.print(",");
				}
			}

			System.out.println(")");
		}
	}
}
