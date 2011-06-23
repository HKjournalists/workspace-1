package edu.bupt.reflect;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("employee")
public class Employee {

	private String name;
	private String designation;
	private String department;
	private long salary;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setSalary(long salary) {
		this.salary = salary;
	}

	public long getSalary() {
		return salary;
	}

}
