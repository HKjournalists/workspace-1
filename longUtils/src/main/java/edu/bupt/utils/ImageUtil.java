package edu.bupt.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

public class ImageUtil {

    private static final Logger LOGGER = Logger.getLogger(ImageUtil.class);

    public static void main(String[] args) {
	boolean bool = downLoadImage(
		"http://img1.qq.com/news/pics/4126/4126993.jpg", "test.jpg");
	System.out.println(bool);
    }

    public static String getFormatInFile(File f) {
	return getFormatName(f);
    }

    public static boolean downLoadImage(String url, String filePath) {
	boolean isLoaded = false;
	MyClient client = new MyClient();
	byte[] bs = client.getContentBytes(url);
	if (bs == null) {
	    return isLoaded;
	}
	if (new File(filePath).getParentFile() != null)
	    Directory.ensureDir(new File(filePath).getParentFile());
	FileOutputStream fos = null;
	try {
	    fos = new FileOutputStream(filePath);
	    fos.write(bs);
	    String fileFormatName = getFormatName(new File(filePath));
	    if (fileFormatName != null && !fileFormatName.isEmpty()) {
		isLoaded = true;
	    }
	} catch (Exception e) {
	    isLoaded = false;
	} finally {
	    if (fos != null) {
		try {
		    fos.close();
		} catch (IOException e) {
		    LOGGER.error("Closing error.", e);
		}
	    }
	}
	return isLoaded;
    }

    // Returns the format name of the image in the object 'o'.
    // Returns null if the format is not known.
    private static String getFormatName(Object o) {
	ImageInputStream iis = null;
	try {
	    // Create an image input stream on the image
	    iis = ImageIO.createImageInputStream(o);

	    // Find all image readers that recognize the image format
	    Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
	    if (!iter.hasNext()) {
		// No readers found
		return null;
	    }

	    // Use the first reader
	    ImageReader reader = (ImageReader) iter.next();

	    String name = reader.getFormatName();
	    // Return the format name
	    return name;
	} catch (IOException e) {
	    LOGGER.error("IO error.", e);
	} finally {
	    if (iis != null) {
		try {
		    iis.close();
		} catch (IOException e) {
		    LOGGER.error("Closing error.", e);
		}
	    }
	}

	// The image could not be read
	return null;
    }
}
