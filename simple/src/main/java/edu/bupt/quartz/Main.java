/**
 * Main.java
 */
package edu.bupt.quartz;

import java.text.DecimalFormat;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author long.ou 2011-6-23 обнГ04:07:12
 * 
 */
public class Main {

	public static void main(String[] args) throws SigarException {
		System.out.println(System.getProperty("java.library.path"));
		Sigar sigar = new Sigar();
		CpuPerc cpuPerc = sigar.getCpuPerc();
		DecimalFormat df = new DecimalFormat();
		System.out.println(df.format(cpuPerc.getCombined()));
	}
}
