package edu.bupt.reflect;

import java.lang.reflect.Method;

public class SampleMethod {

	public static void main(String[] args) {
		App p = new App();
		printMethods(p);
	}

	public static void printMethods(Object o) {
		Class c = o.getClass();
		String className = c.getName();
		Method[] m = c.getMethods();
		for (int i = 0; i < m.length; i++) {
			//输出方法的返回类型 
			System.out.print(m[i].getReturnType().getName());
			//输出方法名 
			System.out.print(" " + m[i].getName() + "(");
			//获取方法的参数 
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
