package com.qunar.deals.util;

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
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class MyClient {
	static Properties proxy = new Properties();
	static String defaultProxy = null;
	static {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("proxy");
			for(String key : rb.keySet()) {
				if ("defaultProxy".equals(key)) {
					defaultProxy = rb.getString(key);
				} 
				String value = rb.getString(key);
				proxy.put(key, value);			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private HttpClient hc = new HttpClient(new MultiThreadedHttpConnectionManager());
	{
		hc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		hc.getHttpConnectionManager().getParams().setSoTimeout(30000);
	}	
	public HttpClient getHc() {
		return hc;
	}
	public MyClient() {
		//hc = new HttpClient();
	}
	public MyClient(String proxyAddress, int port) {
		//hc = new HttpClient();
		hc.getHostConfiguration().setProxy(proxyAddress, port);		
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));
		hc.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
		//hc.getParams().setAuthenticationPreemptive(true);
	}
	public String getHtml(String url, String charset) {
		System.out.println("获得html");
		if (proxy != null && proxy.size() > 0) {
			boolean findProxy = false;
			for(Object key : proxy.keySet()) {
				String domain = key.toString();
				if (url.contains(domain)) {
					String value = proxy.getProperty(domain);
					Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(value);
					if (m.find()) {
						hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));	
						findProxy = true;
						break;
					}					
				}
			}
			if (!findProxy && defaultProxy != null) {
				Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(defaultProxy);
				if (m.find()) {
					hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));
				}		
			}
		}
		String page = "";
		GetMethod get = null;
		if (charset.equals("")) {
			charset = "GBK";
		}		
		try {
			get = new GetMethod(url);			
			get.getParams().setContentCharset(charset);
			get.setRequestHeader("Accept-Language","zh-cn");
		    get.setRequestHeader("User-Agent"," Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
		    get.setRequestHeader("Connection"," Keep-Alive");
		    get.setRequestHeader("Cookie","iscookies=0");
			get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3,false));
			get.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000);  
			hc.executeMethod(get);
			byte[] bs = get.getResponseBody();
			page = new String(bs, charset);
			//page = get.getResponseBodyAsString();
		}catch (java.io.IOException e) {
			e.printStackTrace();
			return "";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
			System.out.println("获得结束");
		}
		return page;
	}

	public String postHtml(String url, String body, String charset) {
		String page = "";
		if (proxy != null && proxy.size() > 0) {
			boolean findProxy = false;
			for(Object key : proxy.keySet()) {
				String domain = key.toString();
				if (url.contains(domain)) {
					String value = proxy.getProperty(domain);
					Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(value);
					if (m.find()) {
						hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));	
						findProxy = true;
						break;
					}					
				}
			}
			if (!findProxy && defaultProxy != null) {
				Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(defaultProxy);
				if (m.find()) {
					hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));
				}		
			}
		}
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
			post.setRequestHeader("Accept-Language","zh-cn");
			post.setRequestHeader("User-Agent"," Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
			post.setRequestHeader("Connection"," Keep-Alive");
			post.getParams().setContentCharset(charset);
			post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,30000); 
			post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3,false));
			post.setRequestEntity(new StringRequestEntity(body,"application/x-www-form-urlencoded", null));

			hc.executeMethod(post);
			byte[] bs = post.getResponseBody();
			page = new String(bs, charset);
			//page = post.getResponseBodyAsString();
			return page;
		}catch (java.io.IOException e) {
			e.printStackTrace();
			return "";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
	}
	
	public byte[] getContentBytes(String url)
	{
		if (proxy != null && proxy.size() > 0) {
			boolean findProxy = false;
			for(Object key : proxy.keySet()) {
				String domain = key.toString();
				if (url.contains(domain)) {
					String value = proxy.getProperty(domain);
					Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(value);
					if (m.find()) {
						hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));	
						findProxy = true;
						break;
					}					
				}
			}
			if (!findProxy && defaultProxy != null) {
				Matcher m = Pattern.compile("(.*?):(\\d+)").matcher(defaultProxy);
				if (m.find()) {
					hc.getHostConfiguration().setProxy(m.group(1), Integer.parseInt(m.group(2)));
				}		
			}
		}
		GetMethod getMethod = null;
		try
		{
			getMethod = new GetMethod(url);
			getMethod.addRequestHeader("Accept-Language", "zh-cn");
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());

			int statusCode = hc.executeMethod(getMethod);
			if (statusCode==200)
			{
				return getMethod.getResponseBody();
			}
			else return null;
		}
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			if (getMethod!=null) getMethod.releaseConnection();
		}
	}
	public static void main(String[] args) throws Exception{
		MyClient client = new MyClient();
		String html = client.getHtml(" http://www.cits.com.cn//outbound/route/04001018127.htm", "utf8");
		System.out.println(html);
	}
}
