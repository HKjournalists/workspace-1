package com.qunar.deals.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.base.classloader.LoaderUtil;
import com.qunar.deals.BookingCheck.BookingCheckUnit;
import com.qunar.deals.extract.AbstractDealsExtract;
import com.qunar.deals.util.CacheUnit;
import com.qunar.deals.util.CheckUnit;
import com.qunar.deals.util.CurrencyUtil;
import com.qunar.deals.util.DataUtil;
import com.qunar.deals.util.DateUtil;
import com.qunar.deals.util.ExtractUtil;
import com.qunar.deals.util.FileUtil;
import com.qunar.deals.util.ImageUtil;
import com.qunar.deals.util.Itinerary;
import com.qunar.deals.util.MailSender;
import com.qunar.deals.util.MyClient;
import com.qunar.deals.util.MyClient2;
import com.qunar.deals.util.NekoHtmlParser;
import com.qunar.deals.util.OfflineResult;
import com.qunar.deals.util.RequestResult;
import com.qunar.deals.util.RouteDetail;
import com.qunar.deals.util.Ship;
import com.qunar.deals.util.ShipCompany;
import com.qunar.deals.util.ShipDetail;
import com.qunar.deals.util.StringUtil;
import com.qunar.deals.util.VoyageRegion;
import com.qunar.itineray.ie.DayTrip;
import com.qunar.itineray.ie.Itineray;
import com.qunar.itineray.ie.ItinerayExtractor;
import com.qunar.itineray.ie.OptionSight;
import com.qunar.itineray.ie.Sight;
import com.sun.media.jai.codec.SeekableStream;

public abstract class AbstractDealsParse implements DealsParseInterface {

	public class RouteType {

		public String type;
		public String cities;
	}

	//I Added
	MyClient2 client = new MyClient2();

	protected static final Log logger = LogFactory.getLog(AbstractDealsParse.class);
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public static SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected String wrapperId = "noId";
	protected String wrapperName = "noName";
	protected String charset = "gbk";
	protected AbstractDealsExtract extracter;
	protected String proxyString = null;
	protected boolean isProxy = false;
	protected String cacheFile = "";
	protected int fileIndex = 0;
	protected int fileIndex2 = 0;
	private String JSON_PATH;
	private String JSON_PATH_2; //记录recheck生成的json
	private String JSON_PATH_3; //记录bookingcheck生成的json
	private String HTML_CACHE_PATH;
	private static Object lock = new Object();
	protected String fileTimeStamp = DateUtil.getFileTimeStamp();
	protected static String callbackUrl = ResourceBundle.getBundle("file").getString("CallbackUrl");
	//	protected static String bookingCheckTotalUrl = ResourceBundle.getBundle("file").getString("BookingCheckTotalUrl");
	//	protected static String priceChangeUrl = ResourceBundle.getBundle("file").getString("PriceChangeUrl");
	//	protected static String cachedPath = ResourceBundle.getBundle("file").getString("CachePath");
	protected static String imageServer = ResourceBundle.getBundle("file").getString("ImageServer");
	protected static String imageOnlineServer = ResourceBundle.getBundle("file").getString("image_qunar_online_server");
	protected static String readFromCache = ResourceBundle.getBundle("file").getString("readfromcache");
	protected static String rsync = ResourceBundle.getBundle("file").getString("rsync");
	//	protected static String interfaceOfflineUrl = ResourceBundle.getBundle("file").getString("InterfaceOffline");
	final static String WRAPPER_CLASSPATH = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	private Map<String, CacheUnit> cachedUnitMap = new HashMap<String, CacheUnit>();
	private Set<String> removedKey = new HashSet();
	private static Map<String, Set<String>> arroundMap = new HashMap();
	private Set<OfflineResult> offlineRoutes = new HashSet();
	private List<RouteDetail> totalObjLst = new ArrayList();
	protected long startTime;

	static final String urlReg = "http://([\\w-]+\\.)+[\\w-]+(/[\\w-   ./?%&=]*)?";
	private Boolean isInterface = null;
	static {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(AbstractDealsParse.class.getClassLoader().getResourceAsStream("arround.txt")));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				//				System.out.println(line);
				String[] fromto = line.split("=");
				if (fromto == null || fromto.length != 2) continue;
				String from = fromto[0];
				from = from.replaceAll("^\uFEFF", "");
				String tos = fromto[1];
				String[] toss = tos.split(",");
				Set<String> toSet = new HashSet();
				arroundMap.put(from, toSet);
				for (String to : toss) {
					toSet.add(to);
				}
			}
		} catch (Exception e) {
			logger.error("加载周边游城市配置文件出错!");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					//					System.out.println("run");
					//					execRsync();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, new Date(new Date().getTime()), 5 * 60 * 1000L);

	}

	private boolean isInterface() {
		if (isInterface == null) {
			final File config = new File(WRAPPER_CLASSPATH + wrapperId + File.separator + "wrapper.n3");
			isInterface = LoaderUtil.isInterface(config);
		}
		return isInterface;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	protected boolean isArround(RouteDetail detail) {
		int price = -1;
		try {
			price = Integer.parseInt(detail.getPrice());
		} catch (Exception e) {
			return false;
		}
		if (price > 1500) return false;
		if (detail.getTraffic() != null) {
			if (detail.getTraffic().indexOf("飞") != -1) {
				return false;
			}
		}
		if (detail.getMiscellaneous().size() > 3) return false;
		if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
			return false;
		}
		if (detail.getArrive() == null || detail.getDeparture() == null) return false;
		if (arroundMap.containsKey(detail.getDeparture())) {
			Set<String> toSet = arroundMap.get(detail.getDeparture());
			String[] tos = detail.getArrive().split(",");
			for (String to : tos) {
				if (toSet.contains(to)) {
					return true;
				}
			}
		}
		return false;
	}

	private Map<String, CacheUnit> newUnitsMap = new HashMap();

	static Set<String> ss = new HashSet();

	public String getCharset() {
		return charset;
	}

	public String getWrapperId() {
		return wrapperId;
	}

	public String getWrapperName() {
		return wrapperName;
	}

	public void flushCachedValue() {
		logger.info("生成 Cache文件!:" + wrapperId);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(cacheFile);
			for (String key : removedKey) {
				cachedUnitMap.remove(key);
			}
			for (String key : cachedUnitMap.keySet()) {
				CacheUnit unit = cachedUnitMap.get(key);
				String out = "";
				if (unit.getUrl() == null) {
					out += key + "," + unit.getTimestamp();
				} else {
					out += key + "," + unit.getTimestamp() + "," + unit.getUrl() + "," + unit.getWrapperId();
				}
				fos.write((out + "\r\n").getBytes());
			}
			for (String key : newUnitsMap.keySet()) {
				CacheUnit unit = newUnitsMap.get(key);
				String out = "";
				if (unit.getUrl() == null) {
					out += key + "," + unit.getTimestamp();
				} else {
					out += key + "," + unit.getTimestamp() + "," + unit.getUrl() + "," + unit.getWrapperId();
				}
				fos.write((out + "\r\n").getBytes());
			}
			cachedUnitMap.clear();
			newUnitsMap.clear();
			removedKey.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void initRouteOffline() {
		logger.info("初始化下线列表!");
		offlineRoutes.clear();
	}

	public void flushRouteOffline() {
		String checkUrlPath = ResourceBundle.getBundle("file").getString("BasePath") + "CheckUrl" + File.separator + wrapperId + File.separator + format3.format(startTime)
				+ File.separator + "checkurl";
		File file = new File(checkUrlPath);
		StringBuilder sb = new StringBuilder();
		Map<String, Map<String, Integer>> routeOfflineMailInfos = new HashMap();
		StringBuilder routeOfflineSb = new StringBuilder();
		for (OfflineResult or : offlineRoutes) {
			if (or.isOffline()) {
				sb.append(or.getId()).append("(").append(or.getReason()).append(")").append("\r\n");
				Map<String, Integer> routeOfflineMailInfo = null;
				if (routeOfflineMailInfos.containsKey(or.getWrapperName())) {
					routeOfflineMailInfo = routeOfflineMailInfos.get(or.getWrapperName());
				} else {
					routeOfflineMailInfo = new HashMap();
					routeOfflineMailInfos.put(or.getWrapperName(), routeOfflineMailInfo);
				}
				if (routeOfflineMailInfo.containsKey(or.getReason())) {
					int count = routeOfflineMailInfo.get(or.getReason());
					count++;
					routeOfflineMailInfo.put(or.getReason(), count);
				} else {
					routeOfflineMailInfo.put(or.getReason(), 1);
				}

			}
		}
		for (String wrapperName : routeOfflineMailInfos.keySet()) {
			routeOfflineSb.append(wrapperName).append("下线线路:\r\n");
			for (String reason : routeOfflineMailInfos.get(wrapperName).keySet()) {
				routeOfflineSb.append("\t").append(reason).append(":(").append(routeOfflineMailInfos.get(wrapperName).get(reason)).append("条)\r\n");
			}
		}
		try {
			if (routeOfflineSb.toString().trim().length() > 0) {
				MailSender.send(format4.format(new Date()) + wrapperName + "下线线路", routeOfflineSb.toString());
			}
		} catch (Exception e) {

		}

		file.getParentFile().mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes("utf8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String finishUrl = ResourceBundle.getBundle("checkurl").getString("finishUrl");
		Vector<String> params = new Vector();
		try {
			params.add(URLEncoder.encode(checkUrlPath, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = StringUtil.fillString(finishUrl, params);
		MyClient client = new MyClient();
		client.getHtml(url, "utf8");

	}

	public void initCachedValue() {
		File file = new File(cacheFile);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) return;
		cachedUnitMap.clear();
		newUnitsMap.clear();
		removedKey.clear();
		BufferedReader br = null;
		Date today = new Date();
		try {
			br = new BufferedReader(new FileReader(cacheFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) continue;
				CacheUnit cacheUnit = new CacheUnit();
				if (line.indexOf(",") == -1) {
					cacheUnit.setKey(line);
					cacheUnit.setTimestamp(format2.format(today));
				} else {
					String[] caches = line.split(",");
					if (caches.length != 2 && caches.length != 4) continue;

					if (caches.length == 2) {
						cacheUnit.setKey(caches[0]);
						cacheUnit.setTimestamp(caches[1]);
					} else if (caches.length == 4) {
						cacheUnit.setKey(caches[0]);
						cacheUnit.setTimestamp(caches[1]);
						cacheUnit.setUrl(caches[2]);
						cacheUnit.setWrapperId(caches[3]);
					}
				}
				try {
					Date d = format2.parse(cacheUnit.getTimestamp());
					if (today.getTime() - d.getTime() > 30 * 24 * 60 * 60 * 1000L) {
						continue;
					}
				} catch (ParseException e) {
					continue;
				}
				cachedUnitMap.put(cacheUnit.getKey(), cacheUnit);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected static int domesticSubtractDay = Integer.parseInt(ResourceBundle.getBundle("file").getString("DomesticSubtractDay"));
	protected static int outBoundSubtractDay = Integer.parseInt(ResourceBundle.getBundle("file").getString("OutBoundSubtractDay"));
	protected static int hkSubtractDay = Integer.parseInt(ResourceBundle.getBundle("file").getString("HKSubtractDay"));
	protected int validRoute = 0;

	protected void appendString(String path, String content) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(path, "rw");
			raf.seek(raf.length());
			raf.write((content + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected String trimURL(String info) {
		if (info == null) return "";
		return info.replaceAll(urlReg, "");
	}

	public AbstractDealsParse(String wrapperId, String wrapperName, String charset) {
		this.wrapperId = wrapperId;
		this.wrapperName = wrapperName;
		this.charset = charset;
		JSON_PATH = ResourceBundle.getBundle("file").getString("BasePath") + "JSON" + File.separator + wrapperId + File.separator;
		JSON_PATH_2 = ResourceBundle.getBundle("file").getString("BasePath") + "JSON2" + File.separator + wrapperId + File.separator;
		JSON_PATH_3 = ResourceBundle.getBundle("file").getString("BasePath") + "JSON3" + File.separator + wrapperId + File.separator;
		HTML_CACHE_PATH = ResourceBundle.getBundle("file").getString("htmlCachePath");
		//		cacheFile = cachedPath + wrapperId + File.separator + "cache.file";
	}

	private static void execRsync() {
		//		System.out.println("no rsnyc");
		String command = "bash " + rsync;
		System.out.println(command);
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(command);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			logger.error("rsync failed!");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String formatRouteSnapShot(String routeSnapShot, int type) {
		if (routeSnapShot.length() == 0) {
			return "";
		}

		String strImageUrl;
		if (type == 0) {
			strImageUrl = getImageUrl(routeSnapShot, "90", "60", "small");
		} else if (type == 1) {
			strImageUrl = getImageUrl(routeSnapShot, "120", "80", "mid");
		} else if (type == 2) {
			strImageUrl = getImageUrl(routeSnapShot, "220", "100", "big");
		} else {
			RenderedOp renderImage;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(routeSnapShot);
				SeekableStream ss = SeekableStream.wrapInputStream(fis, true);
				renderImage = JAI.create("stream", ss);
				((OpImage) renderImage.getRendering()).setTileCache(null);
				int originWidth = renderImage.getWidth();
				int originHeight = renderImage.getHeight();
				strImageUrl = getImageUrl(routeSnapShot, originWidth + "", originHeight + "", "orig");
			} catch (Exception e) {
				strImageUrl = "";
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		}

		if (strImageUrl == null || strImageUrl.length() == 0) return "";

		String strSnapshot_qunar;
		String strImageQunarOnlineServer;

		if (imageOnlineServer.endsWith("/"))
			strImageQunarOnlineServer = imageOnlineServer;
		else strImageQunarOnlineServer = imageOnlineServer + "/";

		if (strImageUrl.startsWith("/")) {
			strSnapshot_qunar = strImageQunarOnlineServer + strImageUrl.substring(1);
		} else {
			strSnapshot_qunar = strImageQunarOnlineServer + strImageUrl;
		}

		return strSnapshot_qunar;
	}

	public static String getImageUrl(String sourcUrl, String strWidth, String strHeight, String strType) {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(imageServer);

		NameValuePair[] data = { new NameValuePair("urls", sourcUrl), new NameValuePair("width", strWidth), new NameValuePair("height", strHeight),
				new NameValuePair("type", strType) };

		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.setRequestBody(data);
		String strImageUrl = "";
		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(postMethod);
			String content = postMethod.getResponseBodyAsString();

			JSONObject object = new JSONObject(content);
			String encodedUrl = sourcUrl;
			strImageUrl = object.getString(encodedUrl);
			if (strImageUrl == null) strImageUrl = "";
		} catch (Exception e) {
			logger.error("failed to get image,status=" + statusCode);
		}
		postMethod.releaseConnection();

		return strImageUrl;
	}

	protected String getXml(String path) {
		StringBuilder html = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				html.append(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {}
			}
		}
		return html.toString();
	}

	private void getDescs(Node node, List<String> result) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("#text".equals(child.getNodeName())) {
				String content = child.getTextContent();
				if (content.replaceAll("\\s|\u00A0|\u3000", "").isEmpty()) {
					continue;
				}
				result.add(child.getTextContent().replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
			} else {
				getDescs(child, result);
			}
		}
	}

	public List<String> getDescs(Node node) {
		List<String> result = new ArrayList<String>();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("#text".equals(child.getNodeName())) {
				String content = child.getTextContent();
				if (content.replaceAll("\\s|\u00A0|\u3000|&nbsp;?", "").isEmpty()) {
					continue;
				}
				result.add(child.getTextContent().replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
			} else {
				getDescs(child, result);
			}
		}
		return result;
	}

	public abstract String parserGroupTitle(String html, String url);

	public abstract String parserFreeTitle(String html, String url);

	protected void produceHtmlCache(String html, String url) {
		String filePath = HTML_CACHE_PATH + StringUtil.MD5Encode(url);
		FileUtil.writeFile(filePath, html);
	}

	//	protected String getHtml(String url) {
	//		String result = "";
	//		boolean flag = false;
	//		if ("true".equalsIgnoreCase(readFromCache)) {
	//			File f = new File(HTML_CACHE_PATH + StringUtil.MD5Encode(url));
	//			if (f.exists()) {
	//				FileInputStream fis = null;
	//				try {
	//					fis = new FileInputStream(f);
	//					byte[] bs = new byte[fis.available()];
	//					fis.read(bs);
	//					result = new String(bs, "UTF-8");
	//					if (result == null || result.isEmpty()) {
	//						flag = true;
	//					}
	//				} catch (Exception e) {
	//					flag = true;
	//				} finally {
	//					if (fis != null) {
	//						try {
	//							fis.close();
	//						} catch (IOException e) {
	//							e.printStackTrace();
	//						}
	//					}
	//				}
	//			} else {
	//				flag = true;
	//			}
	//		} else {
	//			flag = true;
	//		}
	//		if (flag) {
	//			MyClient client = new MyClient();
	//			result = client.getHtml(url, charset);
	//			if (result == null || result.isEmpty()) {
	//
	//			} else {
	//				produceHtmlCache(result, url);
	//			}
	//		}
	//		return result;
	//
	//	}

	public List<RouteDetail> parserFromInterface() {
		return null;
	}

	public OfflineResult testOffline(String url, String type, String function, String otherInfo) {
		MyClient2 client = new MyClient2();
		RequestResult rr = client.getMethodHtml(url, charset);
		if (rr.getHtml() == null) {
			rr.setHtml("");
		}
		OfflineResult or = isOffLine(rr, url, type, function, "", otherInfo, 0);
		return or;
	}

	//处理下线逻辑
	public void process4(List<CheckUnit> units) {
		logger.info(wrapperName + "总下线Check度假产品：" + units.size());
		for (int i = 0; i < units.size(); i++) {
			CheckUnit unit = units.get(i);
			String listInfo = unit.getListInfo();
			Map<String, String> listInfoMap = getListInfoMap(listInfo);
			String type = listInfoMap.get("type");
			String function = listInfoMap.get("function");
			String otherInfo = listInfoMap.get("otherInfo");
			String subject = listInfoMap.get("subject");
			String url = listInfoMap.get("url");
			MyClient2 client = new MyClient2();
			RequestResult rr = client.getMethodHtml(url, charset);
			if (rr.getHtml() == null) {
				rr.setHtml("");
			}
			OfflineResult or = isOffLine(rr, url, type, function, subject, otherInfo, unit.getId());
			offlineRoutes.add(or);
			System.out.println(or);
		}
	}

	public OfflineResult isOffLine(RequestResult rr, String url, String type, String function, String subject, String otherInfo, int id) {
		OfflineResult or = new OfflineResult();
		or.setId(id);
		or.setUrl(url);
		or.setWrapperId(wrapperId);
		or.setWrapperName(wrapperName);

		if (rr.getStatus() != 200) {
			String statusStr = rr.getStatus() + "";
			if (statusStr.startsWith("3") || statusStr.startsWith("4")) {
				or.setOffline(true);
				or.setReason("服务器状态码:" + rr.getStatus());
				return or;
			}
			if (statusStr.startsWith("5")) {
				logger.info("访问：" + rr.getUrl() + "返回状态码:" + rr.getStatus());
			}
		} else {
			if (type != null) {
				if ("mango".equals(wrapperId)) {
					rr.setHtml(client.getHtml(url, charset).getHtml());
				}
				RouteDetail routeObj = null;
				if (RouteDetail.FUNCTION_GROUP.equals(function)) {
					try {
						routeObj = parseGroupRoutePage(rr.getHtml(), url, type, subject, otherInfo);
					} catch (Exception e1) {
						try {
							routeObj = parseFreeRoutePage(rr.getHtml(), url, RouteDetail.FUNCTION_FREE, subject, otherInfo);
						} catch (Exception e2) {}
					}
				} else {
					try {
						routeObj = parseFreeRoutePage(rr.getHtml(), url, type, subject, otherInfo);
					} catch (Exception e1) {
						try {
							routeObj = parseGroupRoutePage(rr.getHtml(), url, RouteDetail.FUNCTION_GROUP, subject, otherInfo);
						} catch (Exception e2) {}
					}
				}
				if (routeObj == null) {
					or.setOffline(true);
					or.setReason("线路解析错误!");
					return or;
				} else {
					double price = -1;
					try {
						price = Double.parseDouble(routeObj.getPrice());
					} catch (Exception e) {

					}
					if (price <= 0) {
						or.setOffline(true);
						or.setReason("未获取到价格!");
						return or;
					}
					if (routeObj.getMiscellaneous().size() == 0) {
						or.setOffline(true);
						or.setReason("未获取到每日行程!");
						return or;
					}
				}
			}

		}
		or.setOffline(false);
		or.setReason("正常线路");
		return or;
	}

	public void produceCrawlerLog(long endtime) {

		if (totalObjLst.size() == 0) {
			try {
				MailSender.send(format4.format(new Date()) + wrapperName + "获取数为0", "");
			} catch (Exception e) {

			}
		}
		Connection conn = null;
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			Map<String, Integer> cityCount = new HashMap();
			for (int i = 0; i < totalObjLst.size(); i++) {
				RouteDetail detail = totalObjLst.get(i);
				if (cityCount.containsKey(detail.getDeparture())) {
					cityCount.put(detail.getDeparture(), cityCount.get(detail.getDeparture()) + 1);
				} else {
					cityCount.put(detail.getDeparture(), 1);
				}
			}
			String countByCity = "";
			for (String city : cityCount.keySet()) {
				countByCity += city + ":" + cityCount.get(city) + ",";
			}
			if (countByCity.endsWith(",")) {
				countByCity = countByCity.substring(0, countByCity.length() - 1);
			}
			System.out.println(countByCity);
			String sql = "insert into crawler_log (wrapperId,wrapperName,starttime,endtime,total,countbycity) values(?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, wrapperId);
			pstmt.setString(2, wrapperName);
			pstmt.setString(3, format4.format(new Date(startTime)));
			pstmt.setString(4, format4.format(new Date(endtime)));
			pstmt.setInt(5, totalObjLst.size());
			pstmt.setString(6, countByCity);
			pstmt.executeUpdate();
			pstmt.close();
			//conn.prepareStatement()
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void process2() {
		List<RouteDetail> routeObjLst = parserFromInterface();
		List<RouteDetail> resultObjLst = new ArrayList();
		totalObjLst.addAll(routeObjLst);
		int i = 0;
		for (RouteDetail routeObj : routeObjLst) {
			i++;
			if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
					&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
				boolean b = false;
				for (Itinerary it : routeObj.getMiscellaneous()) {
					if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty()) || (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
							|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
						b = true;
						break;
					}
				}
				if (b) {
					resultObjLst.add(routeObj);
				} else {
					logger.error("该线路被过滤:" + routeObj.getUrl());
				}
			} else {
				logger.error("该线路被过滤:" + routeObj.getUrl());
			}

			System.out.println("结束处理：接口xml");
			if ((resultObjLst.size() > 0 && resultObjLst.size() % 100 == 0) || i == routeObjLst.size()) {
				String filePath = JSON_PATH + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex++;
				produceJSON(resultObjLst, filePath);
				try {
					Thread.sleep(60000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		logger.info(wrapperName + "实际有效度假产品：" + validRoute);
		logger.info(wrapperName + "下线请求，之前测试入库程序是否正常");
		if (testDB()) {
			logger.info(wrapperName + "入库程序正常");
			try {
				MyClient client = new MyClient();
				Vector<String> params = new Vector<String>();
				params.add(wrapperId);
				//				String cUrl = StringUtil.fillString(interfaceOfflineUrl, params);
				logger.info(wrapperName + "提交下线请求!");
				//				client.getHtml(cUrl, "utf-8");
			} catch (Exception e) {
				MailSender.send(wrapperName + "提交下线请求失败!", "请确认管理程序运行正常后重新执行该wrapper的抓取!");
			}
		} else {
			logger.info(wrapperName + "入库程序异常，取消提交下线请求");
		}

	}

	public static boolean testDB() {
		MyClient client = new MyClient();
		Vector<String> params = new Vector<String>();
		params.add("test");
		String cUrl = StringUtil.fillString(callbackUrl, params);
		String html = client.getHtml(cUrl, "utf-8");
		if (html == null || html.isEmpty()) return false;
		if (html.contains("error:file not exist.")) return true;
		return false;
	}

	public static void main(String[] args) {
		MyClient2 client = new MyClient2();
		RequestResult rr = client.getMethodHtml("http://www.55tour.com/guilin/glss444.html", "gb2312");
		if (rr.getHtml() == null) {
			rr.setHtml("");
		}
		System.out.println(rr.getHtml());
	}

	protected static String getListInfo(String url, String type, String function, String subject, String otherInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<listInfo>\r\n").append("<type>url</type>\r\n").append("<value>").append(url).append("</value>\r\n").append("</listInfo>\r\n");

		sb.append("<listInfo>\r\n").append("<type>type</type>\r\n").append("<value>").append(type).append("</value>\r\n").append("</listInfo>\r\n");

		sb.append("<listInfo>\r\n").append("<type>function</type>\r\n").append("<value>").append(function).append("</value>\r\n").append("</listInfo>\r\n");

		sb.append("<listInfo>\r\n").append("<type>subject</type>\r\n").append("<value>").append(subject).append("</value>\r\n").append("</listInfo>\r\n");

		sb.append("<listInfo>\r\n").append("<type>otherInfo</type>\r\n").append("<value>").append(otherInfo).append("</value>\r\n").append("</listInfo>\r\n");

		return sb.toString();
	}

	public Map<String, String> getListInfoMap(String listInfo) {
		Map<String, String> result = new HashMap();
		NekoHtmlParser parser = new NekoHtmlParser();
		parser.load(listInfo, "utf8");

		NodeList list = parser.selectNodes("//LISTINFO");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String type = parser.getNodeText("TYPE", node);
			String value = parser.getNodeText("VALUE", node);
			result.put(type, value);
		}
		return result;
	}

	List<CheckUnit> getCheckUnits(boolean isInterface) {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<CheckUnit> result = new ArrayList();
		try {
			String username = ResourceBundle.getBundle("database").getString("username");
			String password = ResourceBundle.getBundle("database").getString("password");
			String url = ResourceBundle.getBundle("database").getString("url");
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			stat = conn.createStatement();
			String sql = "";
			if (isInterface) {
				sql = "select id, wrapperId, listInfo, status, sourceUrl from route where wrapperId='" + wrapperId + "' and status in (0,1,3)";
			} else {
				sql = "Select id, wrapperId, listInfo, status, sourceUrl from route where wrapperId='" + wrapperId
						+ "' and listInfo is not null and listInfo <> '' and dateOfLoad not like '" + format.format(new Date()) + "%'";
			}

			logger.info("Recheck sql:" + sql);
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt(1);
				String listInfo = rs.getString(3);
				String wrapperId = rs.getString(2);
				int status = rs.getInt(4);
				String surl = rs.getString(5);
				CheckUnit unit = new CheckUnit();
				unit.setId(id);
				unit.setListInfo(listInfo);
				unit.setWrapperId(wrapperId);
				unit.setStatus(status);
				unit.setUrl(surl);
				result.add(unit);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public RouteDetail checkInterfaceRoute4Test(BookingCheckUnit unit) {
		MyClient2 client = new MyClient2();
		RequestResult rr = client.getMethodHtml(unit.getSourceUrl(), charset);
		if (rr.getHtml() == null) {
			rr.setHtml("");
		}
		unit.setHtml(rr.getHtml());
		return checkInterfaceRoute(unit);
	}

	protected RouteDetail checkInterfaceRoute(BookingCheckUnit unit) {
		return null;
	}

	public void bookingCheck(List<BookingCheckUnit> units, boolean isInterface) {
		List<RouteDetail> routeObjLst = new ArrayList<RouteDetail>();
		Set<String> handledUrl = new HashSet<String>();
		String wrapperId = "";
		if (isInterface) {
			logger.info(wrapperName + "开始Booking校验(接口)");
			Map<Integer, BookingCheckUnit> priceChangeMap = new HashMap<Integer, BookingCheckUnit>();
			for (BookingCheckUnit unit : units) {
				wrapperId = unit.getWrapperId();
				String sUrl = unit.getSourceUrl();
				MyClient2 client = new MyClient2();
				RequestResult rr = client.getMethodHtml(sUrl, charset);
				if (rr.getHtml() == null) {
					rr.setHtml("");
				}
				OfflineResult or = isOffLine(rr, sUrl, null, null, null, null, unit.getId());
				if (or.isOffline()) {
					offlineRoutes.add(or);
					unit.setHtml("");
					unit.setListInfo("");
					unit.setStatus(-2);
					priceChangeMap.put(unit.getId(), unit);
					continue;
				} else {
					if (rr.getHtml() != null && !rr.getHtml().isEmpty()) {
						unit.setHtml(rr.getHtml());
						RouteDetail routeDetail = checkInterfaceRoute(unit);
						if (routeDetail != null) {
							if (routeDetail.getPrice() == null || routeDetail.getPrice().isEmpty()) { //如果接口在解析网页价格的时候，没有获取到价格，不下线，但记录日志，这样可以避免因为接口网站改版而大范围下线线路问题
								unit.setHtml("");
								unit.setListInfo("");
								unit.setStatus(-1);
								priceChangeMap.put(unit.getId(), unit);
							} else {
								Double p = null;
								try {
									p = Double.parseDouble(routeDetail.getPrice());
									if (p <= 0) {
										or.setOffline(true);
										or.setReason("未获取到价格!");
										offlineRoutes.add(or);
										continue;
									}
								} catch (Exception e) {
									or.setOffline(true);
									or.setReason("未获取到价格!");
									offlineRoutes.add(or);
									continue;
								}
								Double d = p;
								if (routeDetail.getCurrency() != null && !routeDetail.getCurrency().isEmpty() && !routeDetail.getCurrency().equals("CNY")) {
									d = CurrencyUtil.transCurrency(p, routeDetail.getCurrency()); //货币汇率转换									
								} else {
									if (d.doubleValue() != unit.getPrice().doubleValue()) {
										unit.setHtml("");
										unit.setListInfo("");
										unit.setvPrice(d);
										priceChangeMap.put(unit.getId(), unit);
									}
								}
							}

						}
					}

				}
			}
			if (priceChangeMap.size() > 0) {
				String filePath = JSON_PATH_3 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
				producePriceChangeJSON(priceChangeMap, filePath);
			}
		} else {
			logger.info(wrapperName + "开始Booking校验(普通)");
			Map<Integer, BookingCheckUnit> priceChangeMap = new HashMap<Integer, BookingCheckUnit>();
			for (int i = 0; i < units.size(); i++) {
				BookingCheckUnit unit = units.get(i);
				if (handledUrl.contains(unit.getSourceUrl())) {
					continue;
				} else {
					handledUrl.add(unit.getSourceUrl());
				}
				wrapperId = unit.getWrapperId();
				String listInfo = unit.getListInfo();
				Map<String, String> listInfoMap = getListInfoMap(listInfo);
				String type = listInfoMap.get("type");
				String function = listInfoMap.get("function");
				String otherInfo = listInfoMap.get("otherInfo");
				String subject = listInfoMap.get("subject");
				String url = listInfoMap.get("url");

				MyClient2 client = new MyClient2();
				RequestResult rr = client.getMethodHtml(url, charset);
				if (rr.getHtml() == null) {
					rr.setHtml("");
				}
				if (unit.getStatus() == 0 || unit.getStatus() == 1 || unit.getStatus() == 3) {
					OfflineResult or = isOffLine(rr, url, type, function, subject, otherInfo, unit.getId());
					if (or.isOffline()) {
						offlineRoutes.add(or);
						unit.setHtml("");
						unit.setListInfo("");
						unit.setStatus(-2);
						priceChangeMap.put(unit.getId(), unit);
						continue;
					}
				}
				RouteDetail routeObj = null;
				if (RouteDetail.FUNCTION_GROUP.equals(function)) {
					String routePage = rr.getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_FREE;
						try {
							routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObj.setBookingFlag("booking");
							routeObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				} else {
					String routePage = rr.getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_GROUP;
						try {
							routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObj.setBookingFlag("booking");
							routeObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				}

			}

			String filePath = JSON_PATH_3 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
			produceJSON(routeObjLst, filePath);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (priceChangeMap.size() > 0) {
				filePath = JSON_PATH_3 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
				producePriceChangeJSON(priceChangeMap, filePath);
			}
		}

		//		MyClient client = new MyClient();
		//		String url = bookingCheckTotalUrl + "?wrapperId=" + wrapperId + "&count=" + units.size();
		//		try {
		//			System.out.println(url);
		//			client.getHtml(url, "utf8");
		//		} catch(Exception e) {
		//			
		//		}
		logger.info(wrapperName + "结束Booking校验");
	}

	private void producePriceChangeJSON(Map<Integer, BookingCheckUnit> priceChangeMap, String filePath) {
		JSONArray json = JSONArray.fromCollection(priceChangeMap.values());

		FileUtil.writeFile(filePath, json.toString());
		try {
			MyClient client = new MyClient();
			Vector<String> params = new Vector<String>();
			params.add(URLEncoder.encode(filePath, "utf-8"));
			//			String cUrl = StringUtil.fillString(priceChangeUrl, params);
			//			System.out.println(cUrl);
			logger.info(wrapperName + "生成一个文件!");
			//			client.getHtml(cUrl, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public void process3(boolean isInterface) {
		List<CheckUnit> units = getCheckUnits(isInterface);
		logger.info(wrapperName + "总Recheck度假产品：" + units.size());
		List<RouteDetail> routeObjLst = new ArrayList<RouteDetail>();
		if (!isInterface) {
			for (int i = 0; i < units.size(); i++) {
				CheckUnit unit = units.get(i);
				String listInfo = unit.getListInfo();
				Map<String, String> listInfoMap = getListInfoMap(listInfo);
				String type = listInfoMap.get("type");
				String function = listInfoMap.get("function");
				String otherInfo = listInfoMap.get("otherInfo");
				String subject = listInfoMap.get("subject");
				String url = listInfoMap.get("url");
				MyClient2 client = new MyClient2();
				RequestResult rr = client.getMethodHtml(url, charset);
				if (rr.getHtml() == null) {
					rr.setHtml("");
				}
				if (unit.getStatus() == 0 || unit.getStatus() == 1 || unit.getStatus() == 3) {
					OfflineResult or = isOffLine(rr, url, type, function, subject, otherInfo, unit.getId());
					if (or.isOffline()) {
						offlineRoutes.add(or);
						continue;
					}
				}
				RouteDetail routeObj = null;
				if (RouteDetail.FUNCTION_GROUP.equals(function)) {
					String routePage = rr.getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_FREE;
						try {
							routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				} else {
					String routePage = rr.getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_GROUP;
						try {
							routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				}
				if ((routeObjLst.size() > 0 && routeObjLst.size() % 100 == 0) || i == units.size() - 1) {
					String filePath = JSON_PATH_2 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
					produceJSON(routeObjLst, filePath);
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (routeObjLst.size() > 0) {
				String filePath = JSON_PATH_2 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
				produceJSON(routeObjLst, filePath);
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < units.size(); i++) {
				CheckUnit unit = units.get(i);
				String sUrl = unit.getUrl();
				MyClient2 client = new MyClient2();
				RequestResult rr = client.getMethodHtml(sUrl, charset);
				if (rr.getHtml() == null) {
					rr.setHtml("");
				}
				if (unit.getStatus() == 0 || unit.getStatus() == 1 || unit.getStatus() == 3) {
					OfflineResult or = isOffLine(rr, sUrl, null, null, null, null, unit.getId());
					if (or.isOffline()) {
						offlineRoutes.add(or);
						continue;
					}
				}
			}
		}

	}

	public void process3(List<CheckUnit> units) {
		List<RouteDetail> routeObjLst = new ArrayList<RouteDetail>();
		logger.info(wrapperName + "总Recheck度假产品：" + units.size());
		for (int i = 0; i < units.size(); i++) {
			CheckUnit unit = units.get(i);
			String listInfo = unit.getListInfo();
			Map<String, String> listInfoMap = getListInfoMap(listInfo);
			String type = listInfoMap.get("type");
			String function = listInfoMap.get("function");
			String otherInfo = listInfoMap.get("otherInfo");
			String subject = listInfoMap.get("subject");
			String url = listInfoMap.get("url");
			RouteDetail routeObj = null;
			if (RouteDetail.FUNCTION_GROUP.equals(function)) {
				String routePage = client.getHtml(url, charset).getHtml();
				if (routePage == null || routePage.isEmpty()) {
					logger.error("获得html失败!");
					continue;
				}
				try {
					routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
				} catch (Exception e1) {
					logger.error("解析错误" + url);
					type = RouteDetail.FUNCTION_FREE;
					try {
						routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e2) {
						logger.error("解析错误" + url);
					}
				}
				if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
						&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
					boolean b = false;
					for (Itinerary it : routeObj.getMiscellaneous()) {
						if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty()) || (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
								|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
							b = true;
							break;
						}
					}
					if (b) {
						routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
						routeObjLst.add(routeObj);
					} else {
						logger.error("该线路被过滤:" + url);
					}
				} else {
					logger.error("该线路被过滤:" + url);
				}
			} else {
				String routePage = client.getHtml(url, charset).getHtml();
				if (routePage == null || routePage.isEmpty()) {
					logger.error("获得html失败!");
					continue;
				}
				try {
					routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
				} catch (Exception e1) {
					logger.error("解析错误" + url);
					type = RouteDetail.FUNCTION_GROUP;
					try {
						routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e2) {
						logger.error("解析错误" + url);
					}
				}
				if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
						&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
					boolean b = false;
					for (Itinerary it : routeObj.getMiscellaneous()) {
						if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty()) || (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
								|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
							b = true;
							break;
						}
					}
					if (b) {
						routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
						routeObjLst.add(routeObj);
					} else {
						logger.error("该线路被过滤:" + url);
					}
				} else {
					logger.error("该线路被过滤:" + url);
				}
			}
			System.out.println("结束处理：" + url);
//			if ((routeObjLst.size() > 0 && routeObjLst.size() % 100 == 0) || i == units.size() - 1) {
//				String filePath = JSON_PATH_2 + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex2++;
//				produceJSON(routeObjLst, filePath);
//				try {
//					Thread.sleep(10000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}

	@Override
	public void process() {
		List<RouteDetail> routeObjLst = new ArrayList<RouteDetail>();
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			doc = domfac.newDocumentBuilder().parse(new InputSource(new StringReader(getXml(extracter.getXmlPath(startTime)))));
			Element root = doc.getDocumentElement();
			NodeList routeLst = root.getElementsByTagName("route");
			logger.info(wrapperName + "总度假产品：" + routeLst.getLength());
			for (int i = 0; i < routeLst.getLength(); i++) {
				Element ele = (Element) routeLst.item(i);
				String url = ele.getElementsByTagName("url").item(0).getTextContent();
				
				if (!url.equals("http://www.ctsgz.cn/html/20101223103828.html")) continue;
				
				System.out.println("正在处理：" + url);
				String type = ele.getElementsByTagName("type").item(0).getTextContent();
				String function = ele.getElementsByTagName("function").item(0).getTextContent();
				String otherInfo = ele.getElementsByTagName("other").item(0).getTextContent();
				String subject = ele.getElementsByTagName("subject").item(0).getTextContent();
				RouteDetail routeObj = null;
				if (RouteDetail.FUNCTION_GROUP.equals(function)) {
					String routePage = client.getHtml(url, charset).getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						e1.printStackTrace();
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_FREE;
						try {
							routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObjLst.add(routeObj);
							totalObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				} else {
					String routePage = client.getHtml(url, charset).getHtml();
					if (routePage == null || routePage.isEmpty()) {
						logger.error("获得html失败!");
						continue;
					}
					try {
						routeObj = parseFreeRoutePage(routePage, url, type, subject, otherInfo);
					} catch (Exception e1) {
						logger.error("解析错误" + url);
						type = RouteDetail.FUNCTION_GROUP;
						try {
							routeObj = parseGroupRoutePage(routePage, url, type, subject, otherInfo);
						} catch (Exception e2) {
							logger.error("解析错误" + url);
						}
					}
					if (routeObj != null && routeObj != null && routeObj.getTitle() != null && !routeObj.getTitle().isEmpty() && routeObj.getMiscellaneous() != null
							&& routeObj.getMiscellaneous().size() != 0 && routeObj.getType() != null) {
						boolean b = false;
						for (Itinerary it : routeObj.getMiscellaneous()) {
							if ((it.getFromCity() != null && !StringUtil.trim(it.getFromCity()).isEmpty())
									|| (it.getToCity() != null && !StringUtil.trim(it.getToCity()).isEmpty())
									|| (it.getDescription() != null && !StringUtil.trim(it.getDescription()).isEmpty())) {
								b = true;
								break;
							}
						}
						if (b) {
							routeObj.setListInfo(getListInfo(url, type, function, subject, otherInfo));
							routeObjLst.add(routeObj);
							totalObjLst.add(routeObj);
						} else {
							logger.error("该线路被过滤:" + url);
						}
					} else {
						logger.error("该线路被过滤:" + url);
					}
				}
				System.out.println("结束处理：" + url);
//				if ((routeObjLst.size() > 0 && routeObjLst.size() % 100 == 0) || i == routeLst.getLength() - 1) {
//					String filePath = JSON_PATH + format3.format(new Date(startTime)) + File.separator + wrapperId + "." + fileIndex++;
//					produceJSON(routeObjLst, filePath);
//					try {
//						Thread.sleep(10000L);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		logger.info(wrapperName + "实际有效度假产品：" + validRoute);
	}

	private void normalizeSpecialChar(RouteDetail detail) {
		detail.setTitle(StringEscapeUtils.unescapeHtml(StringUtil.replaceSpecialChar(detail.getTitle())));
		detail.setDateOfDeparture(StringEscapeUtils.unescapeHtml(StringUtil.replaceSpecialChar(detail.getDateOfDeparture())));
		if (detail.getSightSpot() != null) {
			Set<String> tSet = new HashSet<String>();
			for (String t : detail.getSightSpot()) {
				t = StringEscapeUtils.unescapeHtml(StringUtil.replaceSpecialChar(t));
				tSet.add(t);
			}
			detail.setSightSpot(tSet);
		}
		if (detail.getMiscellaneous() != null) {
			for (Itinerary itinerary : detail.getMiscellaneous()) {
				itinerary.setDescription(StringEscapeUtils.unescapeHtml(StringUtil.replaceSpecialChar(itinerary.getDescription())));
			}
		}
		detail.setArrive(StringEscapeUtils.unescapeHtml(detail.getArrive()));
		if (detail.getFeature() != null) {
			List<String> tFeatures = new ArrayList();
			for (String feature : detail.getFeature()) {
				feature = StringEscapeUtils.unescapeHtml(StringUtil.replaceSpecialChar(feature));
				tFeatures.add(feature);
			}
			detail.setFeature(tFeatures);
		}
	}

	private void normalizeCJKSpace(RouteDetail detail) {
		detail.setTitle(StringUtil.normalizeCJKSpace(detail.getTitle()));
		detail.setDeparture(StringUtil.normalizeCJKSpace(detail.getDeparture()));
		if (detail.getSightSpot() != null) {
			Set<String> tSet = new HashSet<String>();
			for (String t : detail.getSightSpot()) {
				t = StringUtil.normalizeCJKSpace(t);
				tSet.add(t);
			}
			detail.setSightSpot(tSet);
		}
		if (detail.getMiscellaneous() != null) {
			for (Itinerary itinerary : detail.getMiscellaneous()) {
				itinerary.setDescription(StringUtil.normalizeCJKSpace(itinerary.getDescription()));
			}
		}
		detail.setArrive(StringUtil.normalizeCJKSpace(detail.getArrive()));
	}

	public static String transUrl(String sourceUrl, String cs) {
		sourceUrl = StringEscapeUtils.unescapeHtml(sourceUrl);
		String result = "";
		for (int i = 0; i < sourceUrl.length(); i++) {
			char c = sourceUrl.charAt(i);
			int ic = (int) c;
			if (ic <= 0 || ic >= 126) {
				try {
					result += URLEncoder.encode(c + "", cs);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (c == ' ') {
				result += "%20";
			} else if (c == '|') {
				result += "%7C";
			} else {
				result += c;
			}

		}
		result = result.replace("\\", "/");
		return result;
	}

	public String transUrl(String sourceUrl) {
		sourceUrl = StringEscapeUtils.unescapeHtml(sourceUrl);
		String result = "";
		for (int i = 0; i < sourceUrl.length(); i++) {
			char c = sourceUrl.charAt(i);
			int ic = (int) c;
			if (ic <= 0 || ic >= 126) {
				try {
					result += URLEncoder.encode(c + "", charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (c == ' ') {
				result += "%20";
			} else if (c == '|') {
				result += "%7C";
			} else {
				result += c;
			}

		}
		result = result.replace("\\", "/");
		return result;
	}

	protected Set<String> getSightSpotB(List<Sight> sightspotC) {
		Set<String> wList = DataUtil.getWhiteList();
		Set<String> bList = DataUtil.getBlackList();
		Set<String> result = new HashSet();
		for (Sight sight : sightspotC) {
			if (wList.contains(sight.getCity().getName() + "_" + sight.getName())) {
				result.add(sight.getName());
			}
		}
		return result;
	}

	protected void produceJSON(List<RouteDetail> routeObjLst, String path) {
		logger.info("[" + this.wrapperId + "]预计生成" + routeObjLst.size() + "条数据！");
		long starttime = System.currentTimeMillis();
		System.out.println("wrapper id:" + wrapperId + (System.currentTimeMillis() - starttime));
		List<RouteDetail> needProduces = new ArrayList<RouteDetail>();
		for (RouteDetail detail : routeObjLst) {
			try {
				if (ExtractUtil.isHKRoute(detail.getTitle())) {
					detail.setType(RouteDetail.TYPE_HK);
				}
				normalizeCJKSpace(detail);
				normalizeSpecialChar(detail);
				detail.setCurrency(CurrencyUtil.getCurrency(detail.getCurrency()));
				if (detail.getSightSpot_B() != null && detail.getSightSpot_B().size() > 0) {
					if (detail.getSightSpot() != null) {
						for (String s : detail.getSightSpot()) {
							Matcher m = Pattern.compile("\\d+_\\d+_(.*)").matcher(s);
							if (m.find()) {
								detail.getSightSpot_B().remove(m.group(1));
							}
						}
					}
				}
				if (detail.getRoute_snapShot() != null && !detail.getRoute_snapShot().isEmpty()) {
					String imageUrl = detail.getRoute_snapShot();
					detail.setRoute_snapShot(transUrl(detail.getRoute_snapShot()));
					String fileSavePath = ImageUtil.DownLoadImage(detail.getRoute_snapShot(), wrapperId);
					if (fileSavePath != null) {
						detail.setRoute_snapShotSavePath(fileSavePath);
						String small = formatRouteSnapShot(detail.getRoute_snapShotSavePath(), 0);
						String mid = formatRouteSnapShot(detail.getRoute_snapShotSavePath(), 1);
						detail.setRoute_snapShot_Small(small);
						detail.setRoute_snapShot_mid(mid);
						File f = new File(detail.getRoute_snapShotSavePath());
						if (f.exists() && f.isFile()) {
							f.delete();
						}
					} else {
						logger.info("原图片地址:" + imageUrl + ",新图片地址:" + detail.getRoute_snapShot() + ",下载失败");
					}
				}
				if (detail.getRoute_snapShot() != null) {
					detail.setRoute_snapShot(detail.getRoute_snapShot().replace("\\", "/"));
				}
				if (detail.getShip() != null) {
					ShipDetail shipDetail = detail.getShip();
					if (shipDetail.getVoyageImg() != null && !shipDetail.getVoyageImg().isEmpty()) {
						shipDetail.setVoyageImg(transUrl(shipDetail.getVoyageImg()));
						String fileSavePath = ImageUtil.DownLoadImage(shipDetail.getVoyageImg(), wrapperId);
						if (fileSavePath != null) {
							String origin = formatRouteSnapShot(fileSavePath, -1);
							String small = formatRouteSnapShot(fileSavePath, 0);
							String mid = formatRouteSnapShot(fileSavePath, 1);
							String big = formatRouteSnapShot(fileSavePath, 2);
							shipDetail.setVoyageImage_snapShot_Origin(origin);
							shipDetail.setVoyageImage_snapShot_Small(small);
							shipDetail.setVoyageImage_snapShot_Mid(mid);
							shipDetail.setVoyageImage_snapShot_Big(big);
							File f = new File(fileSavePath);
							if (f.exists() && f.isFile()) {
								f.delete();
							}
							if (origin == null || origin.isEmpty() || small == null || small.isEmpty() || mid == null || mid.isEmpty() || big == null || big.isEmpty()) {
								logger.error("原图片地址:" + shipDetail.getVoyageImg() + " 转换失败");
							}
						} else {
							logger.info("原图片地址:" + shipDetail.getVoyageImg() + ",新图片地址:" + fileSavePath + ",下载失败");
						}
					}
					detail.getShip().setVoyageRegion(VoyageRegion.getVoyageLine(detail.getShip().getVoyageRegion()));
					detail.getShip().setCompany(ShipCompany.getShipCompany(detail.getShip().getCompany()));
					detail.getShip().setShipName(Ship.getShip(detail.getShip().getShipName()));
					if (shipDetail.getCompany() == null || shipDetail.getCompany().isEmpty()) {
						shipDetail.setCompany(ShipCompany.extractShipCompany(detail.getTitle()));
					}
					if (shipDetail.getShipName() == null || shipDetail.getShipName().isEmpty()) {
						shipDetail.setShipName(Ship.extractShip(detail.getTitle()));
					}
				}
				String arriveString = "";
				String arrive = "";
				boolean titleFlag = false;
				Set<String> froms = new HashSet();
				if (detail.getDeparture() != null) {
					String[] ds = detail.getDeparture().split(",");
					for (String d : ds) {
						froms.add(d);
					}
				}
				if (detail.getMiscellaneous() != null) {
					Set<Sight> tCity = new HashSet();
					List<Sight> fromSights = new ArrayList();
					for (Itinerary itinerary : detail.getMiscellaneous()) {
						if (itinerary.getDay() == 1) {
							fromSights.addAll(itinerary.getFromCities());
						}
						if (itinerary.getDay() != detail.getMiscellaneous().size()) {
							List<Sight> tS = new ArrayList();
							tS.addAll(itinerary.getToCities());
							for (Sight s : tS) {
								if (s.getCity() == null) {
									if (s.getCountry() != null) {
										if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
											if ("中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
										if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
											if (!"中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
									}
									tCity.add(s);
									continue;
								}
								if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
									if ("中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
									if (!"中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								//								if (!RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
								//									if (froms.contains(s.getCity().getName())) continue;
								//								}
								tCity.add(s);
							}
						} else {
							List<Sight> tS = new ArrayList();
							tS.addAll(itinerary.getToCities());
							for (Sight s : tS) {
								boolean b = true;
								for (Sight fs : fromSights) {
									if (s.getName().equals(fs.getName())) {
										b = false;
										break;
									}
								}
								if (!b) continue;
								if (s.getCity() == null) {
									if (s.getCountry() != null) {
										if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
											if ("中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
										if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
											if (!"中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
									}
									tCity.add(s);
									continue;
								}
								if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
									if ("中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
									if (!"中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								//								if (!RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
								//									if (froms.contains(s.getCity().getName())) continue;
								//								}
								tCity.add(s);
							}
						}
						if (itinerary.getDay() > 1) {
							List<Sight> tS = new ArrayList();
							tS.addAll(itinerary.getFromCities());
							for (Sight s : tS) {
								if (s.getCity() == null) {
									if (s.getCountry() != null) {
										if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
											if ("中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
										if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
											if (!"中国".equals(s.getCountry().getName())) {
												continue;
											}
										}
									}
									tCity.add(s);
									continue;
								}
								if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
									if ("中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
									if (!"中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								//								if (!RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
								//									if (froms.contains(s.getCity().getName())) continue;
								//								}
								tCity.add(s);
							}
						}

					}
					if (!RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
						if (tCity.size() > 1) {
							Set<Sight> tmpCity = new HashSet();
							for (Sight city : tCity) {
								if (city.getCity() == null) {
									tmpCity.add(city);
									continue;
								}
								if (froms.contains(city.getCity().getName())) continue;
								tmpCity.add(city);
							}
							tCity.clear();
							tCity.addAll(tmpCity);
						}
					}
					for (Sight city : tCity) {
						if (city.isCity()) {
							arrive += city.getName() + ",";
						}
						Sight p = null;
						Sight t = city;
						while ((p = t.getParentSight()) != null) {
							t = p;
							if (p.isCity()) {
								arrive += p.getName() + ",";
							}

						}
					}
				}
				if (arrive.isEmpty()) {
					if (detail.getArrive() != null && !detail.getArrive().isEmpty()) {
						arriveString = detail.getArrive();
						List<OptionSight> os = ItinerayExtractor.getInstance().dict.extractOptionSights(arriveString);
						Set<Sight> tCity = new HashSet();
						for (OptionSight sight : os) {
							if (sight.getOptionSights().size() > 1) continue;
							for (Sight s : sight.getOptionSights()) {
								if (s.getCity() == null) continue;
								if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
									if ("中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
									if (!"中国".equals(s.getCity().getCountry().getName())) {
										continue;
									}
								}
								//								if (!RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
								//									if (froms.contains(s.getCity().getName())) continue;
								//								}
								tCity.add(s.getCity());
							}
						}
						if (!RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
							if (tCity.size() > 1) {
								Set<Sight> tmpCity = new HashSet();
								for (Sight city : tCity) {
									if (city.getCity() == null) {
										tmpCity.add(city);
										continue;
									}
									if (froms.contains(city.getCity().getName())) continue;
									tmpCity.add(city);
								}
								tCity.clear();
								tCity.addAll(tmpCity);
							}
						}
						for (Sight city : tCity) {
							if (city.isCity()) {
								arrive += city.getName() + ",";
							}
							Sight p = null;
							Sight t = city;
							while ((p = t.getParentSight()) != null) {
								t = p;
								if (p.isCity()) {
									arrive += p.getName() + ",";
								}

							}
						}
					}
				}
				if (arrive.isEmpty()) {
					String dTitle = detail.getTitle().replaceAll("\\(.*?\\)|（.*?）", "");
					List<OptionSight> os = ItinerayExtractor.getInstance().dict.extractOptionSights(dTitle);
					Set<Sight> tCity = new HashSet();
					for (OptionSight sight : os) {
						if (sight.getOptionSights().size() > 1) continue;
						for (Sight s : sight.getOptionSights()) {
							if (s.getCity() == null) continue;
							if (RouteDetail.TYPE_OUTBOUND.equals(detail.getType())) {
								if ("中国".equals(s.getCity().getCountry().getName())) {
									continue;
								}
							}
							if (RouteDetail.TYPE_DOMESTIC.equals(detail.getType()) || RouteDetail.TYPE_HK.equals(detail.getType())) {
								if (!"中国".equals(s.getCity().getCountry().getName())) {
									continue;
								}
							}
							//							if (!RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
							//								if (froms.contains(s.getCity().getName())) continue;
							//							}
							tCity.add(s.getCity());
						}
					}
					for (Sight city : tCity) {
						arrive += city.getName() + ",";
						//						Sight p = null;
						//						Sight t = city;
						//						while( (p=t.getParentSight()) != null) {
						//							t = p;
						//							arrive += p.getName() + ",";
						//						}
					}
					//由于对方网站title抽取ariive可能不准确
					logger.info("arrive from title:" + detail.getUrl() + "_" + detail.getTitle() + "_" + arrive);
					arrive = "";
				}

				if (detail.getSightSpot() != null) {
					for (String s : detail.getSightSpot()) {
						if (s.matches("1_\\d+_.+")) {
							arrive += s.replaceAll("1_\\d+_", "") + ",";
							continue;
						}
						Matcher m = Pattern.compile("2_(\\d+)_.+").matcher(s);
						if (m.find()) {
							String id = m.group(1);
							Sight sight = ItinerayExtractor.getInstance().dict.getSightById(id);
							if (sight != null) {
								if (sight.getCity() != null) {
									arrive += sight.getCity().getName() + ",";
								}
							}
						}
					}
				}
				if (!arrive.isEmpty()) {
					String[] arriveSplits = arrive.split(",");
					Set<String> tmp = new HashSet();
					arrive = "";

					for (String arriveSplit : arriveSplits) {
						if (arriveSplit.isEmpty()) continue;
						if (tmp.contains(arriveSplit)) continue;
						arrive += arriveSplit + ",";
						tmp.add(arriveSplit);
					}
					if (arrive.endsWith(",")) {
						arrive = arrive.substring(0, arrive.length() - 1);
					}
					detail.setArrive(arrive);
				}
				if (detail.getSightSpot() != null) {
					List<String> tmpLst = new ArrayList();
					tmpLst.addAll(detail.getSightSpot());
					for (String s : tmpLst) {
						if (s.contains("酒店") || s.contains("机场") || s.contains("海上巡游") || s.contains("游轮")) detail.getSightSpot().remove(s);

					}
				}
				if (detail.getSubject() != null) {
					detail.getSubject().remove("null");
				}
				if (detail.getFeature() != null) {
					List<String> tmpLst = new ArrayList();
					tmpLst.addAll(detail.getFeature());
					for (String s : tmpLst) {
						String t = StringUtil.trim(s);
						if (t.isEmpty()) {
							detail.getFeature().remove(s);
						}
					}
				}
				if (detail.getMiscellaneous() != null) {
					detail.setItineraryDay(detail.getMiscellaneous().size() + "");
					for (Itinerary it : detail.getMiscellaneous()) {
						if (it.getDescription() != null) {
							it.setDescription(StringUtil.trim(it.getDescription()));
						}
						it.getFromCities().clear();
						it.getToCities().clear();
					}
				}
				if (RouteDetail.FUNCTION_GROUP.equals(detail.getFunction()) && detail.getTitle().indexOf("自由行") != -1) {
					detail.setFunction(RouteDetail.FUNCTION_FREE);
				}
				if (detail.getDateOfBookingExpire() == null || detail.getDateOfBookingExpire().isEmpty()) {
					detail.setDateOfBookingExpire(getBookExpireDate(detail.getDateOfExpire(), detail.getType()));
				}
				if (RouteDetail.TYPE_ARROUND.equals(detail.getType())) {
					if (!isArround(detail)) {
						if (ExtractUtil.isHKRoute(detail.getTitle())) {
							detail.setType(RouteDetail.TYPE_HK);
						} else {
							detail.setType(RouteDetail.TYPE_DOMESTIC);
						}
					}
				} else {
					if (isArround(detail)) {
						detail.setType(RouteDetail.TYPE_ARROUND);
					}
				}

				boolean yl = false;
				if (detail.getSubject() != null) {
					for (String s : detail.getSubject()) {
						if (s == null) continue;
						if (s.matches(".*?[游邮油]轮.*?")) {
							yl = true;
							break;
						}
					}
				}
				if (!yl) { //如果主题里面没有邮轮游，则如果在title中抽取到了邮轮公司和邮轮名称则认为是邮轮游
					String company = ShipCompany.extractShipCompany(detail.getTitle());

					String shipName = Ship.extractShip(detail.getTitle());

					if (!company.isEmpty() && !shipName.isEmpty()) {
						yl = true;
						List<String> s = new ArrayList();
						s.add("邮轮游");
						detail.setSubject(s);
					}
				}
				if (yl) {
					if (detail.getShip() == null) {
						ShipDetail ship = new ShipDetail();
						detail.setShip(ship);
						ship.setFromHub(detail.getDeparture());
						RouteType routeType = new RouteType();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < detail.getMiscellaneous().size(); i++) {
							Itinerary it = detail.getMiscellaneous().get(i);
							sb.append("第").append(i + 1).append("天 ").append(it.getTitle()).append("\r\n").append(it.getDescription()).append("\r\n");
						}

						getItineraries(sb.toString(), new HashSet(), new HashSet(), routeType);

						ship.setCities(routeType.cities);

						String company = ShipCompany.extractShipCompany(detail.getTitle());
						ship.setCompany(company);

						String shipName = Ship.extractShip(detail.getTitle());
						ship.setShipName(shipName);
						ship.setVoyageRegion(VoyageRegion.getVoyageLine(routeType.cities));

					}
				}

				if (isInterface()) {
					needProduces.add(detail);
				} else {
					String key = md5RouteDetail(detail);
					if (cachedUnitMap.containsKey(key)) {
						CacheUnit unit = cachedUnitMap.get(key);
						String timestamp = unit.getTimestamp();
						try {
							Date t = format2.parse(timestamp);
							if (new Date().getTime() - t.getTime() > 30 * 24 * 60 * 60 * 1000L) {
								cachedUnitMap.remove(key);
								CacheUnit tUnit = new CacheUnit();
								tUnit.setKey(key);
								tUnit.setTimestamp(format2.format(new Date()));
								tUnit.setWrapperId(wrapperId);
								tUnit.setUrl(detail.getUrl());
								newUnitsMap.put(key, tUnit);
								needProduces.add(detail);
							} else {
								continue;
							}
						} catch (Exception e) {
							cachedUnitMap.remove(key);
							CacheUnit tUnit = new CacheUnit();
							tUnit.setKey(key);
							tUnit.setTimestamp(format2.format(new Date()));
							tUnit.setWrapperId(wrapperId);
							tUnit.setUrl(detail.getUrl());
							newUnitsMap.put(key, tUnit);
							needProduces.add(detail);
						}
					} else {
						for (String k : cachedUnitMap.keySet()) {
							CacheUnit findUnit = cachedUnitMap.get(k);
							if (detail.getUrl().equals(findUnit.getUrl())) {
								removedKey.add(k);
								break;
							}
						}
						CacheUnit tUnit = new CacheUnit();
						tUnit.setKey(key);
						tUnit.setTimestamp(format2.format(new Date()));
						tUnit.setWrapperId(wrapperId);
						tUnit.setUrl(detail.getUrl());
						newUnitsMap.put(key, tUnit);
						needProduces.add(detail);
					}
				}

			} catch (Exception e) {
				logger.error("数据转换错误:" + detail.getUrl());
			}
		}
		logger.info("[" + this.wrapperId + "]实际生成" + needProduces.size() + "条数据！");
		routeObjLst.clear();
		if (needProduces.size() == 0) return;
		JSONArray json = JSONArray.fromArray(needProduces.toArray());
		String filePath = path;
		FileUtil.writeFile(filePath, json.toString());
		validRoute += needProduces.size();
		try {
			MyClient client = new MyClient();
			Vector<String> params = new Vector<String>();
			params.add(URLEncoder.encode(filePath, "utf-8"));
			String cUrl = StringUtil.fillString(callbackUrl, params);
			logger.info(wrapperName + "生成一个文件!");
			client.getHtml(cUrl, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	protected List<Itinerary> getItineraries(List<DayTrip> dayTrips, Set<String> sightSpots, Set<String> sightSpotsC, RouteType routeType) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		Itineray itineray = itinerayExtractor.extract2(dayTrips);
		List<Itinerary> itineraries = new ArrayList();
		Map<Integer, Itinerary> itMaps = new HashMap();
		int domesticNum = 0, outboundNum = 0;
		Map<Sight, Set<Sight>> citiesMap = new HashMap();
		String cities = "";
		for (int i = 0; i < dayTrips.size(); i++) {
			DayTrip dayTrip = dayTrips.get(i);
			Itinerary itinerary = null;
			if (itMaps.containsKey(dayTrip.getDayNum())) {
				itinerary = itMaps.get(dayTrip.getDayNum());
				itinerary.setDescription(itinerary.getDescription() + "\r\n" + dayTrip.getTitle() + "\r\n" + dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
					if (dayTrip.getToRegions().get(j).isCity()) {
						Sight city = dayTrip.getToRegions().get(j);
						if (city.getCountry() != null) {
							Sight country = city.getCountry();
							if (citiesMap.containsKey(country)) {
								citiesMap.get(country).add(city);
							} else {
								Set<Sight> citySet = new HashSet();
								citySet.add(city);
								citiesMap.put(country, citySet);
							}
						}
					}
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						if (!"中国".equals(s.getCountry().getName())) {
							outboundNum++;
						} else {
							domesticNum++;
						}
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			} else {
				itinerary = new Itinerary();
				itMaps.put(dayTrip.getDayNum(), itinerary);
				itineraries.add(itinerary);
				itinerary.setDay(dayTrip.getDayNum());
				itinerary.setTitle(StringUtil.trim(StringUtil.normalizeDayTitle(dayTrip.getTitleInfo())));
				itinerary.setDescription(dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
					if (dayTrip.getToRegions().get(j).isCity()) {
						Sight city = dayTrip.getToRegions().get(j);
						if (city.getCountry() != null) {
							Sight country = city.getCountry();
							if (citiesMap.containsKey(country)) {
								citiesMap.get(country).add(city);
							} else {
								Set<Sight> citySet = new HashSet();
								citySet.add(city);
								citiesMap.put(country, citySet);
							}
						}
					}
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (dayTrip.getFromCity() != null) {
					itinerary.setFromCity(dayTrip.getFromCity().getName());
					itinerary.getFromCities().add(dayTrip.getFromCity());
				}
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						if (!"中国".equals(s.getCountry().getName())) {
							outboundNum++;
						} else {
							domesticNum++;
						}
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			}

		}
		for (Sight country : citiesMap.keySet()) {
			cities += "0_" + country.getId() + "_" + country.getName();
			cities += "(";
			String c = "";
			for (Sight city : citiesMap.get(country)) {
				c += "1_" + city.getId() + "_" + city.getName() + ",";
			}
			if (c.endsWith(",")) {
				c = c.substring(0, c.length() - 1);
			}
			cities += c;
			cities += "),";
		}
		if (cities.endsWith(",")) {
			cities = cities.substring(0, cities.length() - 1);
		}
		routeType.cities = cities;
		if (outboundNum != 0) {
			routeType.type = RouteDetail.TYPE_OUTBOUND;
		} else {
			if (domesticNum != 0) {
				routeType.type = RouteDetail.TYPE_DOMESTIC;
			}
		}
		return itineraries;
	}

	protected List<Itinerary> getItineraries(String descs, Set<String> sightSpots, Set<String> sightSpotsC, RouteType routeType) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		Itineray itineray = itinerayExtractor.extract2(descs);
		List<DayTrip> dayTrips = itineray.getDayTrips();
		List<Itinerary> itineraries = new ArrayList();
		Map<Integer, Itinerary> itMaps = new HashMap();
		int domesticNum = 0, outboundNum = 0;
		Map<Sight, Set<Sight>> citiesMap = new HashMap();
		String cities = "";
		for (int i = 0; i < dayTrips.size(); i++) {
			DayTrip dayTrip = dayTrips.get(i);
			Itinerary itinerary = null;
			if (itMaps.containsKey(dayTrip.getDayNum())) {
				itinerary = itMaps.get(dayTrip.getDayNum());
				itinerary.setDescription(itinerary.getDescription() + "\r\n" + dayTrip.getTitle() + "\r\n" + dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
					if (dayTrip.getToRegions().get(j).isCity()) {
						Sight city = dayTrip.getToRegions().get(j);
						if (city.getCountry() != null) {
							Sight country = city.getCountry();
							if (citiesMap.containsKey(country)) {
								citiesMap.get(country).add(city);
							} else {
								Set<Sight> citySet = new HashSet();
								citySet.add(city);
								citiesMap.put(country, citySet);
							}
						}
					}
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						if (!"中国".equals(s.getCountry().getName())) {
							outboundNum++;
						} else {
							domesticNum++;
						}
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			} else {
				itinerary = new Itinerary();
				itMaps.put(dayTrip.getDayNum(), itinerary);
				itineraries.add(itinerary);
				itinerary.setDay(dayTrip.getDayNum());
				itinerary.setTitle(StringUtil.trim(StringUtil.normalizeDayTitle(dayTrip.getTitleInfo())));
				itinerary.setDescription(dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
					if (dayTrip.getToRegions().get(j).isCity()) {
						Sight city = dayTrip.getToRegions().get(j);
						if (city.getCountry() != null) {
							Sight country = city.getCountry();
							if (citiesMap.containsKey(country)) {
								citiesMap.get(country).add(city);
							} else {
								Set<Sight> citySet = new HashSet();
								citySet.add(city);
								citiesMap.put(country, citySet);
							}
						}
					}
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (dayTrip.getFromCity() != null) {
					itinerary.setFromCity(dayTrip.getFromCity().getName());
					itinerary.getFromCities().add(dayTrip.getFromCity());
				}
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						if (!"中国".equals(s.getCountry().getName())) {
							outboundNum++;
						} else {
							domesticNum++;
						}
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			}

		}
		for (Sight country : citiesMap.keySet()) {
			cities += "0_" + country.getId() + "_" + country.getName();
			cities += "(";
			String c = "";
			for (Sight city : citiesMap.get(country)) {
				c += "1_" + city.getId() + "_" + city.getName() + ",";
			}
			if (c.endsWith(",")) {
				c = c.substring(0, c.length() - 1);
			}
			cities += c;
			cities += "),";
		}
		if (cities.endsWith(",")) {
			cities = cities.substring(0, cities.length() - 1);
		}
		routeType.cities = cities;
		if (outboundNum != 0) {
			routeType.type = RouteDetail.TYPE_OUTBOUND;
		} else {
			if (domesticNum != 0) {
				routeType.type = RouteDetail.TYPE_DOMESTIC;
			}
		}
		return itineraries;
	}

	protected List<Itinerary> getItineraries(List<DayTrip> dayTrips, Set<String> sightSpots, Set<String> sightSpotsC) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		Itineray itineray = itinerayExtractor.extract2(dayTrips);
		List<Itinerary> itineraries = new ArrayList();
		Map<Integer, Itinerary> itMaps = new HashMap();
		for (int i = 0; i < dayTrips.size(); i++) {
			DayTrip dayTrip = dayTrips.get(i);
			Itinerary itinerary = null;
			if (itMaps.containsKey(dayTrip.getDayNum())) {
				itinerary = itMaps.get(dayTrip.getDayNum());
				itinerary.setDescription(itinerary.getDescription() + "\r\n" + dayTrip.getTitle() + "\r\n" + dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			} else {
				itinerary = new Itinerary();
				itMaps.put(dayTrip.getDayNum(), itinerary);
				itineraries.add(itinerary);
				itinerary.setDay(dayTrip.getDayNum());
				itinerary.setTitle(StringUtil.trim(StringUtil.normalizeDayTitle(dayTrip.getTitleInfo())));
				itinerary.setDescription(dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				if (dayTrip.getFromCity() != null) {
					itinerary.setFromCity(dayTrip.getFromCity().getName());
					itinerary.getFromCities().add(dayTrip.getFromCity());
				}

				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						//sightSpots.add(s.getId() + "_" + s.getName());
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			}

		}

		return itineraries;
	}

	protected List<Itinerary> getItineraries(String descs, Set<String> sightSpots, Set<String> sightSpotsC) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		Itineray itineray = itinerayExtractor.extract2(descs);
		List<DayTrip> dayTrips = itineray.getDayTrips();
		List<Itinerary> itineraries = new ArrayList();
		Map<Integer, Itinerary> itMaps = new HashMap();
		for (int i = 0; i < dayTrips.size(); i++) {
			DayTrip dayTrip = dayTrips.get(i);
			Itinerary itinerary = null;
			if (itMaps.containsKey(dayTrip.getDayNum())) {
				itinerary = itMaps.get(dayTrip.getDayNum());
				itinerary.setDescription(itinerary.getDescription() + "\r\n" + dayTrip.getTitle() + "\r\n" + dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			} else {
				itinerary = new Itinerary();
				itMaps.put(dayTrip.getDayNum(), itinerary);
				itineraries.add(itinerary);
				itinerary.setDay(dayTrip.getDayNum());
				itinerary.setTitle(StringUtil.trim(StringUtil.normalizeDayTitle(dayTrip.getTitleInfo())));
				itinerary.setDescription(dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList();
				tLst.addAll(tmpSet);

				for (int j = 0; j < tLst.size(); j++) {
					if (j == tLst.size() - 1)
						toCity += tLst.get(j);
					else toCity += tLst.get(j) + ",";
				}
				if (dayTrip.getFromCity() != null) {
					itinerary.setFromCity(dayTrip.getFromCity().getName());
					itinerary.getFromCities().add(dayTrip.getFromCity());
				}

				itinerary.setToCity(toCity);
				itinerary.getToCities().addAll(dayTrip.getToRegions());
				if (sightSpots != null) {
					for (Sight s : dayTrip.getToSights()) {
						String t = "";
						if ("景区".equals(s.getType()) || "景点".equals(s.getType())) {
							t = "2";
						}
						if ("国家".equals(s.getType()) || "省份".equals(s.getType())) {
							t = "0";
						}
						if ("城市".equals(s.getType())) {
							t = "1";
						}
						if (t.isEmpty()) continue;
						sightSpots.add(t + "_" + s.getId() + "_" + s.getName());
						//sightSpots.add(s.getId() + "_" + s.getName());
					}
				}
				if (sightSpotsC != null) {
					for (Sight s : dayTrip.getToAuditSights()) {
						sightSpotsC.add(s.getName());
					}
				}
			}

		}

		return itineraries;
	}

	private String md5RouteDetail(RouteDetail detail) {
		String dateOfBookingExpire = detail.getDateOfBookingExpire();
		//		if (dateOfBookingExpire !=null && dateOfBookingExpire.equals(getBookExpireDate(detail.getDateOfExpire(), detail.getType()))) {
		//			dateOfBookingExpire = null;
		//		}
		Date today = new Date();
		String dateOfDeparture = detail.getDateOfDeparture();
		//		if (dateOfDeparture != null && dateOfDeparture.equals(format.format(today))) {
		//			dateOfDeparture = null;
		//		}
		String dateOfExpire = detail.getDateOfExpire();
		//		Calendar cal = Calendar.getInstance();
		//		cal.setTime(today);
		//		cal.add(Calendar.MONTH, 3);
		//		if (dateOfExpire != null && dateOfExpire.equals(format.format(cal.getTime()))) {
		//			dateOfExpire = null;
		//		}
		String beforeEncode = "RouteDetail [arrive=" + detail.getArrive() + ", dateOfBookingExpire=" + dateOfBookingExpire + ", dateOfDeparture=" + dateOfDeparture
				+ ", dateOfExpire=" + dateOfExpire + ", departure=" + detail.getDeparture() + ", feature=" + detail.getFeature() + ", function=" + detail.getFunction()
				+ "sightSpotB=" + detail.getSightSpot_B() + ", sightSpotC=" + detail.getSightSpot_C() + ", itineraryDay=" + detail.getItineraryDay() + ", miscellaneous="
				+ detail.getMiscellaneous() + ", price=" + detail.getPrice() + ", route_snapShot=" + detail.getRoute_snapShot() + ", sightSpot=" + detail.getSightSpot()
				+ ", starGrade=" + detail.getStarGrade() + ", subject=" + detail.getSubject() + ", title=" + detail.getTitle() + ", traffic=" + detail.getTraffic() + ", type="
				+ detail.getType() + ", url=" + detail.getUrl() + ", wrapperId=" + detail.getWrapperId() + ", wrapperName=" + detail.getWrapperName() + ", ship="
				+ detail.getShip() + "]";
		return StringUtil.MD5Encode(beforeEncode);
	}

	protected String getBookExpireDate(String dateOfExpire, String type) {
		try {
			Date date = format.parse(dateOfExpire);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (RouteDetail.TYPE_DOMESTIC.equals(type) || RouteDetail.TYPE_ARROUND.equals(type)) {
				cal.add(Calendar.DATE, (-1) * domesticSubtractDay);
			} else if (RouteDetail.TYPE_OUTBOUND.equals(type)) {
				cal.add(Calendar.DATE, (-1) * outBoundSubtractDay);
			} else if (RouteDetail.TYPE_HK.equals(type)) {
				cal.add(Calendar.DATE, (-1) * hkSubtractDay);
			}
			return format.format(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateOfExpire;
	}

	protected void checkRouteDetail(RouteDetail detail) {
		//		if (detail.getTitle() == null || detail.getTitle().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无title");
		//		}
		//		if (detail.getPrice() == null || detail.getPrice().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无报价");
		//		}
		//		if (detail.getRoute_snapShot() == null || detail.getRoute_snapShot().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无线路图片");
		//		}
		//		if (detail.getFeature() == null || detail.getFeature().size() == 0) {
		//			System.out.println(detail.getUrl() + ":无线路特色");
		//		}
		//		if (detail.getDeparture() == null || detail.getDeparture().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无出发城市");
		//		}
		//		if (detail.getDeparture() == null || detail.getDeparture().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无出发城市");
		//		}
		//		if (detail.getArrive() == null  || detail.getArrive().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无到达城市");
		//		}
		//		if (detail.getSubject()==null || detail.getSubject().size()==0) {
		//			System.out.println(detail.getUrl() + ":无线路主题");
		//		}
		//		if (detail.getItineraryDay() == null || detail.getItineraryDay().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无行程日期");
		//		}
		//		if (detail.getSightSpot()==null || detail.getSightSpot().size()==0) {
		//			System.out.println(detail.getUrl() + ":无景点");
		//		}
		//		if (detail.getTraffic() == null || detail.getTraffic().trim().isEmpty()) {
		//			System.out.println(detail.getUrl() + ":无交通方式");
		//		}
		//		if (detail.getMiscellaneous() == null || detail.getMiscellaneous().size() == 0) {
		//			System.out.println(detail.getUrl() + ":无行程内容");
		//		}
		if (detail.getDateOfDeparture() == null || detail.getDateOfDeparture().trim().isEmpty()) {
			System.out.println(detail.getUrl() + ":无起始日期");
		}
		if (detail.getDateOfExpire() == null || detail.getDateOfExpire().trim().isEmpty()) {
			System.out.println(detail.getUrl() + ":无过期日期");
		}
	}

}
