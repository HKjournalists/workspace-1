package com.base.test;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class TestClient {
	private HttpClient hc = new HttpClient();
	{
		hc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		hc.getHttpConnectionManager().getParams().setSoTimeout(30000);
	}
	
	public TestClient() {
		//hc = new HttpClient();
	}
	public TestClient(String proxyAddress, int port) {
		//hc = new HttpClient();
		hc.getHostConfiguration().setProxy(proxyAddress, port);		
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));
		hc.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
		//hc.getParams().setAuthenticationPreemptive(true);
		   
		

	}
	public TestClient(String proxyAddress, int port,String username , String password) {
		//hc = new HttpClient();
		hc.getHostConfiguration().setProxy(proxyAddress, port);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		hc.getState().setProxyCredentials(AuthScope.ANY, creds);
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));
		hc.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
		
	}
	public String getHtml(String url, String charset) {		
		String page = "";
		GetMethod get = null;

		if (charset.equals("")) {
			charset = "GBK";
		}		 
		try {
			get = new GetMethod(url);			
			get.getParams().setContentCharset(charset);
			hc.executeMethod(get);
			page = get.getResponseBodyAsString();
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
		}
		return page;
	}

	public String postHtml(String url, String body, String charset) {
		String page = "";
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

			post.getParams().setContentCharset(charset);
			post.setRequestEntity(new StringRequestEntity(body,"application/x-www-form-urlencoded", null));

			hc.executeMethod(post);
			page = post.getResponseBodyAsString();
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
	
	public static void main(String[] args) {
		TestClient client = new TestClient();	
		System.out.println(client.getHtml("http://www.cytsbj.com", "gbk"));
	}
}
