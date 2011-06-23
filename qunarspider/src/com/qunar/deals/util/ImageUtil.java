package com.qunar.deals.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
public class ImageUtil {

	public static void main(String[] args) {
        File f = new File("c:\\aa.jpg");
        if (f.exists()) {
            System.out.println(getFormatInFile(f));
        }
    }

	public static String getFormatInFile(File f) {
        return getFormatName(f);
    }
	
	public static void ensureDir(File dir) {
		if (dir.exists() && dir.isDirectory()) return;		
		if (!dir.getParentFile().exists())			
			ensureDir(dir.getParentFile());
		if (dir.isFile()) dir.delete();
		dir.mkdir();
	}
	
	public static String DownLoadIamge(String url, String filePath) {
		MyClient client = new MyClient();
		byte[] bs = client.getContentBytes(url);
		if (bs == null) return null;
		ensureDir(new File(filePath).getParentFile());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(bs);
			String fileFormatName = getFormatName(new File(filePath));
			if (fileFormatName != null && !fileFormatName.isEmpty()) {
				return filePath;
			}
		} catch(Exception e) {
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public static String DownLoadImage(String url, String wrapperId) {
		MyClient client = new MyClient();
		byte[] bs = client.getContentBytes(url);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (bs == null) return null;
		String filePath = ResourceBundle.getBundle("file").getString("BasePath") + "Image" + File.separator + wrapperId + File.separator + StringUtil.MD5Encode(url);
		ensureDir(new File(filePath).getParentFile());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(bs);
			String fileFormatName = getFormatName(new File(filePath));
			if (fileFormatName != null && !fileFormatName.isEmpty()) {
				return filePath;
			}
		} catch(Exception e) {
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
    
    // Returns the format name of the image in the object 'o'.
    // Returns null if the format is not known.
    private static String getFormatName(Object o) {
    	ImageInputStream iis = null;
        try {
            // Create an image input stream on the image
             iis = ImageIO.createImageInputStream(o);
    
            // Find all image readers that recognize the image format
            Iterator iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                // No readers found
                return null;
            }
    
            // Use the first reader
            ImageReader reader = (ImageReader)iter.next();
    
            String name = reader.getFormatName();
            // Return the format name
            return name;
        } catch (IOException e) {
            //
        } finally {
        	if (iis != null) {
        		try {
					iis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        // The image could not be read
        return null;
    }

}
