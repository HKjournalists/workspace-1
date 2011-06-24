package edu.bupt.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author oulong
 * 
 */

public class Output {

	// 一般输出xml查看故如此命名
	public static boolean produceXml(String pathName, String content) {
		BufferedWriter bw = null;
		try {
			File file = new File(pathName);
			file.getParentFile().mkdirs();
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	// 文本后附加content行
	public synchronized static void appendString(String pathName, String content) {
		RandomAccessFile raf = null;
		try {
			File file = new File(pathName);
			file.getParentFile().mkdirs();
			raf = new RandomAccessFile(pathName, "rw");
			// 文件指针移到文件最后
			raf.seek(raf.length());
			raf.write((content + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}