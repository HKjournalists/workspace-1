package edu.bupt.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hello world!
 * 
 */
public class App {

	private String ip;

	public static void main(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Class cl = App.class;

		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = String.class;
		Method method = cl.getMethod("setIp", parameterTypes);
		Object[] cans = new Object[1];//假设你要传入两个参数////应该是一个 
		String argments = "122";
		cans[0] = argments;

		App a = (App) cl.newInstance();

		method.invoke(a, cans);//调用方法 
		System.out.println(a.getIp());
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}
}
