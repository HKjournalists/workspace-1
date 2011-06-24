package edu.bupt.qunar.gisdata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.qunar.gis.PNode;
import edu.bupt.qunar.gis.POIService;
import edu.bupt.utils.IoTool;
import edu.bupt.utils.StringUtil;

public class DivideAndConquer {

    private final static String hasFilter = "hasFilter";
    private final static String noResult = "noResult";
    private static List<String> siteList = new ArrayList<String>() {

        {
            add("people.com.cn");
            add("xinhuanet.com");
            add("china.com.cn");
            add("cctv.com");
            add("cri.cn");
            add("chinadaily.com.cn");
            add("youth.cn");
            add("ce.cn");
        }
    };

    public static void run(final String inputFileName, final String outputFileName, final String errorFileName,
            int groupNum) {
        List<String> queryList = IoTool.readMoreRows(inputFileName, "utf-8");

        //每组大小
        int groupSize = (queryList.size() % groupNum == 0) ? queryList.size() / groupNum : (queryList.size() / groupNum + 1);

        final CountDownLatch endSignal = new CountDownLatch(groupNum);
        final ReentrantLock lock = new ReentrantLock();

        for (int i = 0; i < groupNum; i++) {
            final List<String> subStrList = queryList.subList(i * groupSize, Math.min((i + 1) * groupSize, queryList.size()));

            Thread t = new Thread() {

                @Override
                public void run() {
                    List<String> outputList = new ArrayList<String>();
                    List<String> errorList = new ArrayList<String>();

                    for (final String str : subStrList) {
                        try {

                            String[] temp = str.split(" ");
                            if (temp.length > 1) {
                                process(temp[1], temp[0], outputList, errorList);
                            }
                            //先缓存着 30个再一起输出到文件 process一次产生一个
                            if (outputList.size() >= 30) {
                                try {
                                    lock.lock();
                                    IoTool.writeMultiRows(outputFileName, outputList, true);
                                    IoTool.writeMultiRows(errorFileName, errorList, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    outputList.clear();
                                    errorList.clear();
                                    lock.unlock();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        lock.lock();
                        IoTool.writeMultiRows(outputFileName, outputList, true);
                        IoTool.writeMultiRows(errorFileName, errorList, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        outputList.clear();
                        errorList.clear();
                        lock.unlock();
                    }
                    endSignal.countDown();
                }
            };
            t.start();
        }

        try {
            endSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void process(String name, String addr, List<String> outputList, List<String> errorList) {

        PNode pNode = POIService.doSearch("maps.google.com", name, addr);
        if (pNode != null) {
            outputList.add(StringUtil.divideBySignal(new String[]{addr, name, Double.toString(pNode.lat), Double.toString(pNode.lng)}));
        } else {
            outputList.add(StringUtil.divideBySignal(new String[]{addr, name, "NOT get lat and lng"}));
        }
        System.out.println(name + "\tOver!");

//            String site = "sogou";
//		Map<String, String> map = sogou(str);
//
//		if("w".equals(map.get(hasFilter))){
//			errorList.add(site + "|" + str);
//		}else{
//			outputList.add(str + "|" + site + "|" + map.get(hasFilter) + "|" + map.get(noResult));
//		}
    }

//	private static Map<String, String> soso(String keyword){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		map.put(hasFilter, "w");
//		map.put(noResult, "w");
//
//		//http://www.soso.com/q?sc=web&&num=10&w=202.200.226.0
//		String content = null;
//		try {
//			content = HttpUtil.getContentByUrl("http://www.soso.com/q?sc=web&num=10&w=" + URLEncoder.encode(keyword, "GB2312"), "GB2312", 5000, 5000);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		if(content != null){
//			if(content.contains("抱歉，找不到")){
//				map.put(hasFilter, "y");
//				map.put(noResult, "y");
//			}else{
//				boolean hasFilterFlag = true;
//
//				content = content.replaceAll("[\r\n]+", "");
//				Pattern p = Pattern.compile("<h3><a.*?href=\"(.+?)\" id=");
//				Matcher m = p.matcher(content);
//				while(m.find()){
//					String url = m.group(1);
//
//					boolean flag = false;
//					for(String site : siteList){
//						if(url.contains(site)){
//							flag = true;
//							break;
//						}
//					}
//
//					if(!flag){
//						hasFilterFlag = false;
//						break;
//					}
//				}
//
//
//				if(hasFilterFlag){
//					map.put(hasFilter, "y");
//					map.put(noResult, "n");
//				}else{
//					map.put(hasFilter, "n");
//					map.put(noResult, "n");
//				}
//			}
//		}
//		return map;
//	}
//
//
//	private static Map<String, String> sogou(String keyword){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		map.put(hasFilter, "w");
//		map.put(noResult, "w");
//
//		//
//		String content = null;
//		try {
//			content = HttpUtil.getContentByUrl("http://www.sogou.com/web?num=10&query=" + URLEncoder.encode(keyword, "GB2312"), "GB2312", 5000, 5000);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		if(content != null){
////			System.out.println(content);
//
//			if(content.contains("抱歉，没有找到与") || content.contains("您输入的关键词可能涉及不符合相关法律法规的内容")){
//				map.put(hasFilter, "y");
//				map.put(noResult, "y");
//			}else{
//				if(content.contains("根据相关法律法规和政策")){
//					map.put(hasFilter, "y");
//					map.put(noResult, "n");
//				}else{
//					map.put(hasFilter, "n");
//					map.put(noResult, "n");
//				}
//			}
//		}
//		return map;
//	}
//
//	private static Map<String, String> baidu(String keyword){
//		Map<String, String> map = new LinkedHashMap<String, String>();
//		map.put(hasFilter, "w");
//		map.put(noResult, "w");
//
//		//
//		String content = null;
//		try {
//			content = HttpUtil.getContentByUrl("http://www.baidu.com/s?num=10&wd=" + URLEncoder.encode(keyword, "GB2312"), "GB2312", 5000, 5000);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		if(content != null){
////			System.out.println(content);
//
//			if(content.contains("抱歉，没有找到与") || content.contains("搜索结果可能不符合")){
//				map.put(hasFilter, "y");
//				map.put(noResult, "y");
//			}else{
//				if(content.contains("根据相关法律法规和政策")){
//					map.put(hasFilter, "y");
//					map.put(noResult, "n");
//				}else{
//					map.put(hasFilter, "n");
//					map.put(noResult, "n");
//				}
//			}
//		}
//		return map;
//	}
    /**
     * @param args
     */
    public static void main(String[] args) {
        final String errorFileName = "H:/Java work/error.txt";
        final String inputFileName = "H:/Java work/xxxtag_canting.txt";
        final String outputFileName = "H:/Java work/result.txt";

        //分组大小
        int groupNum = 50;
        run(inputFileName, outputFileName, errorFileName, groupNum);

//		System.out.println(soso("最美女县委书记烧高香"));
//		System.out.println(soso("家庭"));
//		System.out.println(soso("法轮功"));
//		System.out.println(soso("中国禁闻"));

//		System.out.println(sogou("家庭"));
//		System.out.println(sogou("法轮功"));
//		System.out.println(sogou("中国禁闻"));
//		System.out.println(sogou("一九八九年事件 李鹏"));
//		System.out.println(sogou("chengrendianyingzaixiankan"));

//		System.out.println(baidu("chengrendianyingzaixiankan"));
//		System.out.println(baidu("珠明洞观音 孩童借口传言"));
//		System.out.println(baidu("一九八九年事件 李鹏"));
    }
}
