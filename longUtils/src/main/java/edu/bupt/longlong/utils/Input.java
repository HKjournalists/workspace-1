package edu.bupt.longlong.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 
 * @author oulong
 * 
 *         流的包装并设定读入编码
 */

public class Input {

	// 从输入流中读取txt文件
	public static boolean readTxt(InputStream in, String charset,
			List<String> List) {
		BufferedReader reader = null;
		// InputStreamReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));

			// reader = new InputStreamReader(new FileInputStream(file), "GBK");
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line == null || line.isEmpty()) {
					continue;
				}
				List.add(line);
			}

			// int ch = 0;
			// //以字符方式显示文件内容
			// while((ch = reader.read()) != -1)
			// {
			// System.out.print((char) ch);
			// }

			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 逐行读取txt文件
	public static boolean readTxt(File file, String charset, List<String> List)
			throws FileNotFoundException {
		if (file.exists())
			return readTxt(new FileInputStream(file), charset, List);
		else {
			System.out.println(file.getName() + "文件不存在。");
			return false;
		}
	}

	public static boolean readTxt(String filePath, String charset,
			List<String> list) throws IOException {
		return Input.readTxt(new File(filePath), charset, list);
	}

	// TODO 假定zip文件中仅以文本文件。
	public static boolean readZip(String zipname) {
		ZipInputStream zin = null;
		try {
			zin = new ZipInputStream(new FileInputStream(zipname));
			while ((zin.getNextEntry()) != null) {
				// zip内为一个文本文件
				// Scanner in = new Scanner(zin);
				// System.out.println(entry.getName());
				// while (in.hasNextLine()) {
				// System.out.println(in.nextLine());
				// }

				InputStreamReader in = new InputStreamReader(zin, "GBK");

				int ch = 0;
				// 以字符方式显示文件内容
				while ((ch = in.read()) != -1) {
					System.out.print((char) ch);
				}

				zin.closeEntry();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (zin != null)
				try {
					zin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public static void main(String[] args) throws IOException {
	}
}
