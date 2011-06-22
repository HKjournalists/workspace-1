package edu.bupt.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class LoadMethod {

	public Object Load(String cName, String MethodName, String[] type, String[] param) {
		Object retobj = null;
		try {
			//加载指定的Java类 
			Class cls = Class.forName(cName);

			//获取指定对象的实例 
			Constructor ct = cls.getConstructor(null);
			Object obj = ct.newInstance(null);

			//构建方法参数的数据类型 
			Class partypes[] = this.getMethodClass(type);

			//在指定类中获取指定的方法 
			Method meth = cls.getMethod(MethodName, partypes);

			//构建方法的参数值 
			Object arglist[] = this.getMethodObject(type, param);

			//调用指定的方法并获取返回值为Object类型 
			retobj = meth.invoke(obj, arglist);

		} catch (Throwable e) {
			System.err.println(e);
		}
		return retobj;
	}

	//获取参数类型Class[]的方法 
	public Class[] getMethodClass(String[] type) {
		Class[] cs = new Class[type.length];
		for (int i = 0; i < cs.length; i++) {
			if (!type[i].trim().equals("") || type[i] != null) {
				if (type[i].equals("int") || type[i].equals("Integer")) {
					cs[i] = Integer.TYPE;
				} else if (type[i].equals("float") || type[i].equals("Float")) {
					cs[i] = Float.TYPE;
				} else if (type[i].equals("double") || type[i].equals("Double")) {
					cs[i] = Double.TYPE;
				} else if (type[i].equals("boolean") || type[i].equals("Boolean")) {
					cs[i] = Boolean.TYPE;
				} else {
					cs[i] = String.class;
				}
			}
		}
		return cs;
	}

	//获取参数Object[]的方法 
	public Object[] getMethodObject(String[] type, String[] param) {
		Object[] obj = new Object[param.length];
		for (int i = 0; i < obj.length; i++) {
			if (!param[i].trim().equals("") || param[i] != null) {
				if (type[i].equals("int") || type[i].equals("Integer")) {
					obj[i] = new Integer(param[i]);
				} else if (type[i].equals("float") || type[i].equals("Float")) {
					obj[i] = new Float(param[i]);
				} else if (type[i].equals("double") || type[i].equals("Double")) {
					obj[i] = new Double(param[i]);
				} else if (type[i].equals("boolean") || type[i].equals("Boolean")) {
					obj[i] = new Boolean(param[i]);
				} else {
					obj[i] = param[i];
				}
			}
		}
		return obj;
	}
}
