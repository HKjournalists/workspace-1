/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package edu.bupt.longlong.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
/**
 * Eclipse默认把这些受访问限制的API设成了ERROR。只要把Windows-Preferences-Java-Complicer-Errors/
 * Warnings里面的Deprecated and restricted API中的Forbidden references(access
 * rules)选为Warning就可以编译通过。
 */
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 * 
 * @author oulong
 */
@SuppressWarnings("restriction")
public class FileUtil {

	private static Logger logger = Logger.getLogger(FileUtil.class);

	public static final int CHANGE_WIDTH = 220;
	public static final int CHANGE_HEIGHT = 220;

	/**
	 * 
	 * @param frompath
	 * @param topath
	 * @param change
	 *            true changing to the same width and height false not chage
	 * @throws IOException
	 */
	public static void copyJPEG(String frompath, String topath, boolean change) throws IOException {
		File from = new File(frompath); //读入文件
		Image src = ImageIO.read(from); //构造Image对象
		int width = src.getWidth(null); //得到源图宽
		int height = src.getHeight(null); //得到源图长

		/*
		 * //绘制缩小后的图 BufferedImage tag = new
		 * BufferedImage(width/2,height/2,BufferedImage.TYPE_INT_RGB);
		 * tag.getGraphics().drawImage(src,0,0,width/2,height/2,null); //绘制缩小后的图
		 */
		if (change) {
			width = CHANGE_WIDTH;
			height = CHANGE_HEIGHT;
		}

		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		tag.getGraphics().drawImage(src, 0, 0, width, height, null);
		FileOutputStream out = new FileOutputStream(topath); //输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

		encoder.encode(tag); //JPEG编码
		out.close();
	}

	public static boolean forceDelete(File f) {
		boolean result = false;
		int tryCount = 0;
		while (!result && tryCount++ < 10) {
			logger.debug("try to delete file " + f.getName() + " cnt:" + tryCount);
			System.gc();
			result = f.delete();
		}
		return result;
	}
}
