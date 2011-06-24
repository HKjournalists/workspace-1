/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bupt.longlong.qunar.gisdata;

import bupt.longlong.qunar.gis.Image;
import bupt.longlong.utils.Directory;
import bupt.longlong.utils.FileUtil;
import bupt.longlong.utils.ImageUtil;
import bupt.longlong.utils.IoTool;
import bupt.longlong.utils.MyClient;
import bupt.longlong.utils.NekoHtmlParser;
import bupt.longlong.utils.SimilarityChinese;
import bupt.longlong.utils.StringUtil;
import bupt.longlong.utils.thread.FunctionThreadPool;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.RenderedOp;

/**
 *下载对应景点的图片 大于300*300 没有水印 内容与景点大概一致
 *
 * @author oulong
 */
public class PerfectImage {

    public static void getData() {

        //获得已经checked的数据id
        List<String> idList = new ArrayList<String>();
        File[] files = Directory.local("H:\\Java work\\longUtils\\Image\\checked\\", ".*?\\.jpg");
        for (int i = 0; i < files.length; i++) {
            String tmp = files[i].getName();
            Matcher matcher = Pattern.compile("(\\d+)\\(").matcher(tmp);
            while (matcher.find()) {
                idList.add(matcher.group(1));
            }
        }

        //matched
        List<String> idTxtList = IoTool.readMoreRows(mine, "gbk");

        for (int i = 0; i < idTxtList.size(); i++) {
            String s = idTxtList.get(i);
            String[] temp = s.split("\\|");
            boolean bool = true;
            /*1去除已有的*/
            for (String id : idList) {
                if (temp[0].equals(id)) {
                    bool = false;
                }
            }

            if (bool) {
                IoTool.write(resultFilePath, s + "\n", true);
                System.out.println("waitting...");
            }
        }

        /*
        DbOperator.init("db.xml");
        String[][] raws = null;
        String sql = "select id,country,province,city,name,type from Sight where photo_id is null and type in ('国家','城市','景点','景区','省份')";
        raws = DataSet.query(sourceDatabaseName, sql);

        for (int i = 0; i < raws.length; i++) {
        String[] temp = raws[i];
        for (int j = 0; j < temp.length; j++) {
        if (temp[j] == null) {
        temp[j] = "Empty";
        }
        }
        boolean bool = true;
        //1去除已有的
        for (String id : idList) {
        if (temp[0].equals(id)) {
        bool = false;
        }
        }

        //2去除已有的
        for (String id : idTxtList) {
        id = id.replace(".jpg", "");
        id = StringUtil.trim(id);
        if (temp[0].equals(id)) {
        bool = false;
        }
        }

        if (bool) {
        String s = StringUtil.divideBySignal(temp);
        IoTool.write(resultFilePath, s + "\n", true);
        System.out.println("waitting...");
        }
        }
         */
    }
    public static final String sourceDatabaseName = "sight213backend";
    public static final String resultFilePath = "H:/Java work/needImages.txt";
    public static final String mine = "H:/Java work/mine.txt";
    public static final String baiduformatting = "http://image.baidu.com/i?ct=201326592&cl=2&lm=-1&tn=baiduimage&pv=&z=0&s=0&word=%s";
    public static final String googleformatting = "http://www.google.com.hk/images?hl=en&source=imghp&biw=1280&bih=643&q=%s&gbv=2&aq=f&aqi=&aql=&oq=&gs_rfai=";
    public static final String baiducharset = "gb2312";
    public static final String googlecharset = "utf-8";

    public static List<String> baiduImageUrl(String sightName) throws UnsupportedEncodingException {
        List<String> imageuris = new ArrayList<String>();
        List<Image> imageList = new ArrayList<Image>();
        String url = String.format(baiduformatting, URLEncoder.encode(sightName, baiducharset));

        MyClient client = new MyClient();
        String html = client.postMethodHtml(url, baiducharset).getHtml();

        NekoHtmlParser parser = new NekoHtmlParser();
        parser.load(html, baiducharset);


        Matcher matcher = Pattern.compile("\"objURL\":\"(.*?)\".*?\"width\":(\\d+),\"height\":(\\d+),\".*?\"fromPageTitle\":\"(.*?)\"").matcher(html);
        while (matcher.find()) {
            String name, uri;
            int width, height;
            uri = StringUtil.trim(matcher.group(1));
            width = Integer.parseInt(matcher.group(2));
            height = Integer.parseInt(matcher.group(3));
            name = matcher.group(4).replaceAll("[^\u4e00-\u9fa5]", "");
            if (name != null && uri != null && width > 0 && height > 0) {
                Image e = new Image(name, uri, width, height);
                imageList.add(e);
            }
        }

        if (imageList.size() > 0) {
            //TODO 设定抓取多少个
            List<Image> images = bestImageList(imageList, sightName, 5);
            for (int i = 0; i < images.size(); i++) {
                imageuris.add(images.get(i).getUri());
            }
        }
        return imageuris;
    }

    public static List<String> googleImageUrl(String sightName) throws UnsupportedEncodingException {
        List<String> imageuris = new ArrayList<String>();
        List<Image> imageList = new ArrayList<Image>();
        String url = String.format(googleformatting, URLEncoder.encode(sightName, googlecharset));

        MyClient client = new MyClient();
        String html = client.postMethodHtml(url, googlecharset).getHtml();

        NekoHtmlParser parser = new NekoHtmlParser();
        parser.load(html, googlecharset);

        Matcher matcher = Pattern.compile("/imgres\\?imgurl=(.*?)&(.*?)&[wh]=(\\d+)(.*?)&[wh]=(\\d+)(.*?)\\:").matcher(html);
        int i = 0;
        while (matcher.find()) {
            int width = Integer.parseInt(matcher.group(3));
            int height = Integer.parseInt(matcher.group(5));
            if (i > -1 && width > 300 && height > 300) {
                imageuris.add(matcher.group(1));
            }
            i++;
            if (i == 10) {
                break;
            }
        }

        return imageuris;
    }

    private static Image bestOneImage(List<Image> imageList, String sightName) {
        Image best = new Image();

        best = bestImageList(imageList, sightName, 1).get(0);

        return best;
    }

    private static List<Image> bestImageList(List<Image> imageList, String sightName, int ranknum) {
        List<Image> bestList = new ArrayList<Image>(ranknum);
        class TImage extends Image {

            double d;
            Image i;

            TImage(Image i, double d) {
                this.d = d;
                this.i = i;
            }

            public Image getImage() {
                return i;
            }
        }

        List<TImage> tlist = new ArrayList<TImage>();

        //图片选取规则
        for (Image e : imageList) {
            if (e.getHeight() < 300 || e.getWidth() < 300) {
            } else {
                double d = SimilarityChinese.Comparability(e.getName(), sightName);
                TImage ti = new TImage(e, d);
                tlist.add(ti);
            }
        }

        Collections.sort(tlist, new Comparator<TImage>() {

            public int compare(TImage o1, TImage o2) {
                double d = (o1.d - o2.d);
                if (d > 0) {
                    return -1;
                } else if (d < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        //不足就全取
        if (tlist.size() < ranknum) {
            ranknum = tlist.size();
        }
        //TODO tlist.get(i+1).getImage()去除第一个取后面的
        for (int i = 0; i < ranknum; i++) {
            bestList.add(tlist.get(i + 5).getImage());
        }

        return bestList;
    }

    public static void downloadAndName(String unit) throws UnsupportedEncodingException {
        String[] temp = unit.split("\\|");
        String name = temp[0] + "(" + temp[4] + "-" + temp[3] + "-" + temp[5] + ")";
        String qName = temp[4];

        //搜索词处理
        if (!temp[3].equals("Empty")) {
            qName = temp[3] + " " + qName;
        }
//        if (temp[5].equals("城市") && !name.endsWith("市")) {
//            qName = qName + "市";
//        }

        List<String> urls = baiduImageUrl(qName);

//        List<String> urls = googleImageUrl(qName);

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            String path = String.format(nameformatter, name, i + 1);
            boolean bool = ImageUtil.downLoadImage(url, path);
            if (bool) {
//                try {
//                    FileUtil.copyJPEG(path, String.format(topath, name, i + 1), true);
//                } catch (IOException ex) {
//                    System.out.println("Copy erro." + name);
//                }
                System.out.println(name + " " + (i + 1));

                //下载后判断图片长宽，小于300的直接删除
//                RenderedOp renderImage;
//                FileInputStream fis = null;
                ImageInputStream iis = null;
                try {

                    Iterator readers = ImageIO.getImageReadersByFormatName("jpg");
                    ImageReader reader = (ImageReader) readers.next();
                    iis = ImageIO.createImageInputStream(path);
                    reader.setInput(iis, true);


//                    fis = new FileInputStream(path);
//                    SeekableStream ss = SeekableStream.wrapInputStream(fis, true);
//                    renderImage = JAI.create("stream", ss);
//                    ((OpImage) renderImage.getRendering()).setTileCache(null);
//                    int originWidth = renderImage.getWidth();
//                    int originHeight = renderImage.getHeight();

                    int originWidth = reader.getWidth(0);
                    int originHeight = reader.getHeight(0);

                    //处理
                    if (originWidth < 300 || originHeight < 300) {
                        File remove = new File(path);
                        FileUtil.forceDelete(remove);
                        System.out.println(name + " " + (i + 1) + " 不符合，已经删除！");
                    }
                } catch (Exception e) {
                } finally {
                    if (iis != null) {
                        try {
                            iis.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }
    public static final String nameformatter = "H:/Java work/longUtils/Image/baidu/%s-%d.jpg";
    public static final String topath = "H:/Java work/longUtils/Image/changed/%s-%d.jpg";

    public static void main(String[] args) throws UnsupportedEncodingException {
        //获取需要check的数据
        getData();
        //test
//        baiduImageUrl("玉带河");
//        googleImageUrl("七府坟");
//        downloadAndName("1434|中国|山西|运城|孤峰山国际滑雪场|景区");

//        List<String> source = IoTool.readMoreRows(resultFilePath, "utf-8");
//
//        FunctionThreadPool pool = new FunctionThreadPool(source);
//        pool.execute();
    }
}
