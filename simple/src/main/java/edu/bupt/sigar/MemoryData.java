package edu.bupt.sigar;

import java.text.DecimalFormat;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import com.thoughtworks.xstream.XStream;

public class MemoryData {

	private Mem mem;
	private Swap swap;

	public MemoryData() {}

	public void populate(Sigar sigar) throws SigarException {
		mem = sigar.getMem();
		swap = sigar.getSwap();
	}

	public static MemoryData gather(Sigar sigar) throws SigarException {
		MemoryData data = new MemoryData();
		data.populate(sigar);
		return data;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("java.library.path"));
		Sigar sigar = new Sigar();
		CpuPerc cpuPerc = sigar.getCpuPerc();
		for(double d:sigar.getLoadAverage()){
			System.out.println(d);
		}
//		
		DecimalFormat df = new DecimalFormat();
		System.out.println(df.format(cpuPerc.getCombined()));
	}
}
