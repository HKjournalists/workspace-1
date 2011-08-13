package edu.bupt.utils;

/**
 * Modified by me. Control the getting-html process with properties files
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

public class MyClient {

    private static final Logger LOGGER = Logger.getLogger(MyClient.class);
    private final String proxyInUse = ResourceBundle.getBundle("file")
	    .getString("useproxy");
    private final String readFromCache = ResourceBundle.getBundle("file")
	    .getString("readfromcache");
    private final String HTML_CACHE_PATH = ResourceBundle.getBundle("file")
	    .getString("htmlCachePath");
    static Properties proxy = new Properties();
    static String defaultProxy = null;
    static List<String> proxyList = new ArrayList<String>();
    // 获取代理

    // static {
    // try {
    // String strProxy = ResourceBundle.getBundle("file").getString("proxy");
    // proxyList = IoTool.readMoreRows(strProxy, "GBK");
    // } catch (Exception e) {
    // LOGGER.error("get proxy error.", e);
    // }
    // }
    private HttpClient hc = new HttpClient(
	    new MultiThreadedHttpConnectionManager());

    {
	hc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
	hc.getHttpConnectionManager().getParams().setSoTimeout(30000);
    }

    public HttpClient getHc() {
	return hc;
    }

    public MyClient() {
	// hc = new HttpClient();
    }

    public MyClient(String proxyAddress, int port) {
	// hc = new HttpClient();
	hc.getHostConfiguration().setProxy(proxyAddress, port);
	List<Header> headers = new ArrayList<Header>();
	headers.add(new Header("User-Agent",
		"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));
	hc.getHostConfiguration().getParams()
		.setParameter("http.default-headers", headers);
	// hc.getParams().setAuthenticationPreemptive(true);
    }

    private void useProxy(String url, String postOrGet) {
	System.out.println("[Try to use proxy.]");
	if (proxyList.size() > 0) {
	    boolean findProxy = false;
	    for (String value : proxyList) {
		Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(value);
		if (m.find()) {
		    hc.getHostConfiguration().setProxy(m.group(1),
			    Integer.parseInt(m.group(2)));
		    findProxy = true;
		    if (postOrGet.equalsIgnoreCase("post"))
			break;
		    GetMethod getStatus = new GetMethod(url);
		    int status;
		    try {
			status = hc.executeMethod(getStatus);
			if (status == HttpStatus.SC_OK) {
			    break;
			}
		    } catch (HttpException e) {
			LOGGER.error("httpException", e);
		    } catch (IOException e) {
		    }
		}
	    }
	    if (!findProxy && defaultProxy != null) {
		Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(
			defaultProxy);
		if (m.find()) {
		    hc.getHostConfiguration().setProxy(m.group(1),
			    Integer.parseInt(m.group(2)));
		}
	    }
	}
    }

    private void produceHtmlCache(String html, String url) {
	System.out.println("waitting to cache...");
	String filePath = HTML_CACHE_PATH + StringUtil.MD5Encode(url);
	IoTool.write(filePath, html, false);
	System.out.println("cache done.");
    }

    // 缓存 post&get
    public RequestResult obtain(String url, String charset) {
	RequestResult result = new RequestResult();
	String html = "";
	boolean flag = false;
	if ("true".equalsIgnoreCase(readFromCache)) {
	    File f = new File(HTML_CACHE_PATH + StringUtil.MD5Encode(url));
	    if (f.exists()) {
		System.out.println("reading from cache...");
		FileInputStream fis = null;
		try {
		    fis = new FileInputStream(f);
		    byte[] bs = new byte[fis.available()];
		    fis.read(bs);
		    html = new String(bs, "UTF-8");
		    if (html == null || html.isEmpty()) {
			flag = true;
		    } else {
			System.out.println("readed.");
		    }
		} catch (Exception e) {
		    flag = true;
		} finally {
		    if (fis != null) {
			try {
			    fis.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}
	    } else {
		flag = true;
	    }
	} else {
	    flag = true;
	}
	if (flag) {
	    html = getMethodHtml(url, charset).getHtml();
	    if (html == null || html.isEmpty()) {
		System.out.println("[Get Nothing using GETMethod]");
	    } else {
		produceHtmlCache(html, url);
	    }
	}
	result.setUrl(url);
	result.setHtml(html);
	result.setStatus(HttpStatus.SC_OK); // 这里简便地设定为统一值
	return result;
    }

    public RequestResult getMethodHtml(String url, String charset) {
	System.out.println("GET[" + url + ']');
	RequestResult result = new RequestResult();
	result.setUrl(url);
	if ("true".equalsIgnoreCase(proxyInUse))
	    useProxy(url, "get");
	String page = "";
	GetMethod get = null;
	if (charset.equals("")) {
	    charset = "GBK";
	}
	try {
	    get = new GetMethod(url);
	    get.getParams().setContentCharset(charset);
	    get.setRequestHeader("Accept-Language", "zh-cn");
	    get.setRequestHeader("User-Agent",
		    " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
	    get.setRequestHeader("Connection", " Keep-Alive");
	    get.setRequestHeader("Cookie", "iscookies=0");
	    get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		    new DefaultHttpMethodRetryHandler(3, false));
	    get.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
	    int status = hc.executeMethod(get);

	    // byte[] bs = get.getResponseBody();
	    // page = new String(bs, charset);

	    page = IoTool.stream2String(get.getResponseBodyAsStream(), charset);

	    result.setStatus(status);
	    result.setHtml(page);
	} catch (IOException e) {
	    LOGGER.error("IO error.", e);
	} catch (Exception e) {
	    LOGGER.error("error.", e);
	} finally {
	    if (get != null) {
		get.releaseConnection();
	    }
	    System.out.println("Done.");
	}
	return result;
    }

    public RequestResult postMethodHtml(String url, String body, String charset) {
	System.out.println("POST[" + url + ']');
	String page = "";
	RequestResult result = new RequestResult();
	result.setUrl(url);

	if ("true".equalsIgnoreCase(proxyInUse))
	    useProxy(url, "post");

	PostMethod post = null;
	if (charset.equals("")) {
	    charset = "GBK";
	}
	try {
	    post = new PostMethod(url);
	    hc.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	    Cookie[] cookies = hc.getState().getCookies();
	    String cookieStr = "";
	    for (Cookie cookie : cookies) {
		cookieStr += cookie.getName() + "=" + cookie.getValue() + ";";
	    }
	    post.setRequestHeader("Cookie", cookieStr);
	    post.setRequestHeader("Accept-Language", "zh-cn");
	    post.setRequestHeader("User-Agent",
		    " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
	    post.setRequestHeader("Connection", " Keep-Alive");
	    post.getParams().setContentCharset(charset);
	    post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
	    post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		    new DefaultHttpMethodRetryHandler(3, false));
	    post.setRequestEntity(new StringRequestEntity(body,
		    "application/x-www-form-urlencoded", null));

	    int status = hc.executeMethod(post);
	    byte[] bs = post.getResponseBody();
	    page = new String(bs, charset);
	    result.setStatus(status);
	    result.setHtml(page);
	} catch (java.io.IOException e) {
	    LOGGER.error("IO error.", e);
	} catch (Exception e) {
	    LOGGER.error("error.", e);
	} finally {
	    if (post != null) {
		post.releaseConnection();
	    }
	    System.out.println("Done.");
	}
	return result;
    }

    public byte[] getContentBytes(String url) {
	if ("true".equalsIgnoreCase(proxyInUse))
	    useProxy(url, "post");
	GetMethod getMethod = null;
	try {
	    getMethod = new GetMethod(url);
	    getMethod.addRequestHeader("Accept-Language", "zh-cn");
	    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		    new DefaultHttpMethodRetryHandler());

	    int statusCode = hc.executeMethod(getMethod);
	    if (statusCode == 200) {
		return getMethod.getResponseBody();
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    return null;
	} finally {
	    if (getMethod != null) {
		getMethod.releaseConnection();
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	MyClient client = new MyClient();
	client.obtain("http://jingqu.travel.sohu.com/pcl-100447.shtml", "gbk")
		.getHtml();
    }
}
