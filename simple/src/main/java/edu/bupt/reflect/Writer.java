package edu.bupt.reflect;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class Writer {

	public static void main(String[] args) {
		Employee e = new Employee();

		//Set the properties using the setter methods
		//Note: This can also be done with a constructor.
		//Since we want to show that XStream can serialize
		//even without a constructor, this approach is used.
		e.setName("Jack");
		e.setDesignation("Manager");
		long s = 10000;
		e.setSalary(s);

		List<Employee> hhhh = new ArrayList<Employee>();
		hhhh.add(e);
		Employee e2 = new Employee();
		e2.setDepartment("Management");
		hhhh.add(e2);

		//Serialize the object
		XStream xs = new XStream();
		xs.alias("blog", Employee.class);

		//Write to a file in the file system
		try {
			FileOutputStream fs = new FileOutputStream("g:/output/employeedata.xml");
			xs.toXML(hhhh, fs);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
