package com.qunar.deals.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Read str from the file or write str to the file.
 * 
 * @author oulong
 *
 */
public class IoTool {

    private static Logger logger = Logger.getLogger(IoTool.class);

    public static void write(String filename, String text, boolean append) {
        File out = new File(filename);
        BufferedWriter bw = null;
        try {
            out.getParentFile().mkdirs();
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out, append), "UTF-8"));
            bw.write(text);
            bw.flush();
        } catch (IOException e) {
            logger.error("An I/O error occurs.", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error("Writer cannot close.", e);
                }
            }
        }
    }

    public static void writeMultiRows(String filename, List<String> multiRow, boolean append) {
        File out = new File(filename);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out, append), "UTF-8"));
            for (String str : multiRow) {
                bw.write(str + "\n");
            }
            bw.flush();
        } catch (IOException e) {
            logger.error("An I/O error occurs.", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error("Writer cannot close.", e);
                }
            }
        }
    }

    public static String readLine(String filename, String Charset) {
        String str = null;
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), Charset));
            str = bufReader.readLine();
        } catch (IOException e) {
            logger.error("Can't load the specified file.", e);
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    logger.error("Writer cannot close.", e);
                }
            }
        }
        return str;
    }

    public static List<String> readMoreRows(String filename, String Charset) {
        List<String> list = new LinkedList<String>();

        String str = null;
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), Charset));
            while ((str = bufReader.readLine()) != null) {
                list.add(str);
            }
        } catch (IOException e) {
            logger.error("Can't load the specified file.", e);
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    logger.error("Writer cannot close.", e);
                }
            }
        }
        return list;
    }

    public static String stream2String(InputStream in, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] tmpByte = new byte[1024];
            int len = 0;
            while ((len = in.read(tmpByte)) != -1) {
                baos.write(tmpByte, 0, len);
            }
        } finally {
            baos.close();
        }
        return new String(baos.toByteArray(), encoding);
    }

    public static List<String> getConfigData(String filename) {
        List<String> resultList = new ArrayList<String>();

        InputStream is = IoTool.class.getResourceAsStream("/" + filename);
        try {
            String str = stream2String(is, "UTF-8");
            String[] strs = str.split("[\r\n]+");
            if (strs != null) {
                resultList = Arrays.asList(strs);
            }
        } catch (Exception e) {
            logger.error("Cann't access the " + filename, e);
        }

        return resultList;
    }

    public static boolean writeStream(InputStream in, String filePath) {
        boolean status = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte[] tmpByte = new byte[1024];
                int len = 0;
                while ((len = in.read(tmpByte)) != -1) {
                    baos.write(tmpByte, 0, len);
                }
            } finally {
                baos.close();
            }

            File file = new File(filePath);
            if (!file.exists()) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(baos.toByteArray());
                fout.close();
                status = true;
            }
        } catch (Exception e) {
            logger.error("output strem error", e);
        }
        return status;
    }

    public static void main(String args[]) {
        String filename = "name2pinyin.txt";
        List<String> resultList = getConfigData(filename);
        for (String row : resultList) {
            System.out.println(row);
            break;
        }

        InputStream is = IoTool.class.getResourceAsStream("/01.jpg");
        writeStream(is, "d:/1.jpg");
    }
}
