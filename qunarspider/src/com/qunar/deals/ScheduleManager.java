package com.qunar.deals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.base.classloader.LoaderUtil;
import com.base.classloader.ManageClassLoader;
import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.MyClient;

public class ScheduleManager {

	static final Log logger = LogFactory.getLog(ScheduleManager.class);
	
	private static ScheduleManager instance = new ScheduleManager();
	
	static SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat format2= new SimpleDateFormat("yyyy-MM-dd");
	
	final String WRAPPER_CLASSPATH = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	
	final String FINISH_URL = ResourceBundle.getBundle("file").getString("FinishUrl");
	
	String crawlerStartTime;
	
	String crawlerParserTime;
	
	String recheckStartTime;
	
	String recheckParserTime;
	{
		Properties p = new Properties();
		try {
			p.load(ScheduleManager.class.getClassLoader().getResourceAsStream("schedule/default.properties"));
		} catch(Exception e) {
			
		}
		crawlerStartTime = p.getProperty("crawler.starttime");
		crawlerParserTime = p.getProperty("crawler.pasertime");
		
		recheckStartTime = p.getProperty("recheck.starttime");
		recheckParserTime = p.getProperty("recheck.parsertime");
	}
	private ScheduleManager(){}
	
	
	private Map<String, ScheduleUnit> scheduleUnits = new HashMap();
	

	public static ScheduleManager getInstance() {
		return instance;
	}
	
	
	public synchronized String onlineCrawler(String wrapperId) {
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(this.getClass().getClassLoader().getResource("nouseclass.properties").getFile());
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String noUseParseRoutes = p.getProperty("no_use_extract_class");
		String[] ss = noUseParseRoutes.split(",");
		noUseParseRoutes = "";
		boolean b = false;
		for(String s : ss) {
			if (wrapperId.equals(s)){
				b = true;
				continue;
			}
			noUseParseRoutes += s + ",";
		}
		if (!b) {
			return "上线失败,该wrapper已上线!";
		}
		if (noUseParseRoutes.endsWith(",")) {
			noUseParseRoutes = noUseParseRoutes.substring(0, noUseParseRoutes.length() - 1);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(this.getClass().getClassLoader().getResource("nouseclass.properties").getFile());
			String s = "no_use_extract_class=" + noUseParseRoutes;
			fos.write(s.getBytes());
		} catch (IOException e) {
			return "上线失败,文件写入错误!";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public synchronized String offlineCrawler(String wrapperId) {
		final ScheduleUnit su = scheduleUnits.get(wrapperId);
		if (su == null) return "下线失败,该wrapper已下线!";
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(this.getClass().getClassLoader().getResource("nouseclass.properties").getFile());
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String noUseParseRoutes = p.getProperty("no_use_extract_class");
		if (noUseParseRoutes == null || noUseParseRoutes.isEmpty()) {
			noUseParseRoutes = wrapperId;
		} else {
			noUseParseRoutes += "," + wrapperId;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(this.getClass().getClassLoader().getResource("nouseclass.properties").getFile());
			String s = "no_use_extract_class=" + noUseParseRoutes;
			fos.write(s.getBytes());
			scheduleUnits.remove(wrapperId);
		} catch (IOException e) {
			return "下线失败,文件写入错误!";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public synchronized List<String> getValidWrapper() {
		List<String> result = new ArrayList();
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(this.getClass().getClassLoader().getResource("nouseclass.properties").getFile());
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String noUseParseRoutes = p.getProperty("no_use_extract_class");
		System.out.println(noUseParseRoutes);
		String[] noRoutes = noUseParseRoutes.split(",");
		Set<String> noUseParseRoutesSet = new HashSet();
		for(String s : noRoutes) {
			noUseParseRoutesSet.add(s);
		}
		
		File f = new File(WRAPPER_CLASSPATH);
		if (f.exists() && f.isDirectory()) {
			for(File wrapperDir : f.listFiles()) {
				if (noUseParseRoutesSet.contains(wrapperDir.getName())) continue;
				if (wrapperDir.isDirectory()) {
					String wrapperId = wrapperDir.getName();
					result.add(wrapperId);
				}
			}
		}
		return result;		 
	}
	void loadScheduleParams(ScheduleUnit su) {
		String wrapperId = su.wrapperId;
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(this.getClass().getClassLoader().getResource("schedule/" + wrapperId + ".properties").getFile());
			p.load(is);
		} catch(Exception e) {
			logger.info(su.wrapperId + " use default properties!");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		su.crawlerStartTime = p.getProperty("crawler.starttime", crawlerStartTime);
		su.crawlerParserTime = p.getProperty("crawler.pasertime", crawlerParserTime);
		
		su.recheckStartTime = p.getProperty("recheck.starttime", recheckStartTime);
		su.recheckParserTime = p.getProperty("recheck.parsertime", recheckParserTime);
	}
	
	public synchronized String schedule(String wrapperId, String type) {
		final ScheduleUnit su = scheduleUnits.get(wrapperId);
		if (su == null) return "调度失败,没有找到解析类!";
		if (su.isInSchedule()) return "wrapper正在运行中!";
		if ("crawler".equals(type)) {
			ManageClassLoader cl = ManageClassLoader.getInstance();
			final File config = new File(WRAPPER_CLASSPATH + su.wrapperId + File.separator + "wrapper.n3");
			String parser = LoaderUtil.getParser(config);
			if (parser == null) return "调度失败,没有找到解析类!";
			final AbstractDealsParse task;
			try {
				task = (AbstractDealsParse)cl.loadClass(parser, su.wrapperId).newInstance();
				
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							logger.info("开始执行抓取:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
							su.crawlerLastStartTime = new Date().getTime();
							su.crawlerLastFinishTime = -1;
							su.type = 0;
							task.setStartTime(su.crawlerLastStartTime);
							su.status = 0;
							task.initCachedValue();
							if (LoaderUtil.isInterface(config)) {
								task.process2();
							} else {
								task.process();
							}
							task.flushCachedValue();
							
						} catch(Exception e) {
							e.printStackTrace();
						} finally {
							su.status= 1;
							su.crawlerLastFinishTime = new Date().getTime();
							task.produceCrawlerLog(su.crawlerLastFinishTime);
							MyClient client = new MyClient();			
							client.getHtml(FINISH_URL, "utf-8");
							logger.info("结束抓取:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
						}
						
					}}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			ManageClassLoader cl = ManageClassLoader.getInstance();
			final File config = new File(WRAPPER_CLASSPATH + su.wrapperId + File.separator + "wrapper.n3");
			String parser = LoaderUtil.getParser(config);
			if (parser == null) return "调度失败,没有找到解析类!";
			final boolean isInterface = LoaderUtil.isInterface(config);
			final AbstractDealsParse task;
			try {
				task = (AbstractDealsParse)cl.loadClass(parser, su.wrapperId).newInstance();
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							logger.info("开始执行Recheck:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
							su.recheckLastStartTime = new Date().getTime();
							su.recheckLastFinishTime = -1;
							su.type = 1;
							task.setStartTime(su.recheckLastStartTime);
							su.status = 0;
							task.initCachedValue();
							task.initRouteOffline();
							task.process3(isInterface);
							task.flushCachedValue();
							task.flushRouteOffline();
						} catch(Exception e) {
							e.printStackTrace();
						} finally {
							su.status= 1;
							su.recheckLastFinishTime = new Date().getTime();
							logger.info("结束执行Recheck:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
						}
						
					}}).start();
			} catch(Exception e) {
				
			}
		}
		return "";
	}
	
	synchronized void schedule (final ScheduleUnit su) {
		System.out.println(su.wrapperId + " start time is :" + su.crawlerStartTime);
		if (su.isInSchedule()) return;		
		long currentTime = new Date().getTime();
		long crawlerLastFinishTime = su.crawlerLastFinishTime;
		long recheckLastFinishTime = su.recheckLastFinishTime;
		long cPTime = 0;
		long rPTime = 0;
		try {
			cPTime = Long.parseLong(su.crawlerParserTime);
			rPTime = Long.parseLong(su.recheckParserTime);
		} catch(Exception e) {
		}
		if (cPTime == 0 || rPTime == 0) {
			logger.error(su.wrapperId + "调度配置文件出错!");
			return;
		}
		long cTime = 0;
		long rTime = 0;
		if (crawlerLastFinishTime == -1) {
			 Date today = su.loadedDate;
    		 Calendar cal = Calendar.getInstance();
 			 cal.setTime(today);
 			 String tryTime = format2.format(today) + " " + su.crawlerStartTime;
 			 Date tryDate;
			try {
				tryDate = format.parse(tryTime);
				Date startDate = null;
	 			if (today.getTime() <= tryDate.getTime()) {
	 				startDate = tryDate;
	 			} else {
	 				cal.add(Calendar.DATE, 1);
	 				startDate = format.parse( format2.format(cal.getTime()) + " " + su.crawlerStartTime);
	 			}
	 			cTime = startDate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}			 
		} else {
			cTime = crawlerLastFinishTime + cPTime;
		}
		
		
		if (recheckLastFinishTime == -1) {
			 Date today = su.loadedDate;
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(today);
			 String tryTime = format2.format(today) + " " + su.recheckStartTime;
			 Date tryDate;
			try {
				tryDate = format.parse(tryTime);
				Date startDate = null;
	 			if (today.getTime() <= tryDate.getTime()) {
	 				startDate = tryDate;
	 			} else {
	 				cal.add(Calendar.DATE, 1);
	 				startDate = format.parse( format2.format(cal.getTime()) + " " + su.recheckStartTime);
	 			}
	 			rTime = startDate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}			 
		} else {
			rTime = recheckLastFinishTime + rPTime;
		}		
		
		if (rTime < cTime) {
			if (rTime < currentTime) {  //调度recheck
				ManageClassLoader cl = ManageClassLoader.getInstance();
				final File config = new File(WRAPPER_CLASSPATH + su.wrapperId + File.separator + "wrapper.n3");
				String parser = LoaderUtil.getParser(config);
				if (parser == null) return;
				final boolean isInterface = LoaderUtil.isInterface(config);
				final AbstractDealsParse task;
				try {
					task = (AbstractDealsParse)cl.loadClass(parser, su.wrapperId).newInstance();
					new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								logger.info("开始执行Recheck:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
								su.recheckLastStartTime = new Date().getTime();
								su.recheckLastFinishTime = -1;
								su.type = 1;
								task.setStartTime(su.recheckLastStartTime);
								su.status = 0;
								task.initCachedValue();
								task.initRouteOffline();
								task.process3(isInterface);
								task.flushCachedValue();
								task.flushRouteOffline();
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								su.status= 1;
								su.recheckLastFinishTime = new Date().getTime();
								logger.info("结束执行Recheck:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
							}
							
						}}).start();
				} catch(Exception e) {
					
				} finally {
					try {
						Thread.sleep(5000L); //等5秒钟再开始调度下一个任务
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		} else {
			if (cTime < currentTime) { //调度crawler
				ManageClassLoader cl = ManageClassLoader.getInstance();
				final File config = new File(WRAPPER_CLASSPATH + su.wrapperId + File.separator + "wrapper.n3");
				String parser = LoaderUtil.getParser(config);
				if (parser == null) return;
				final AbstractDealsParse task;
				try {
					task = (AbstractDealsParse)cl.loadClass(parser, su.wrapperId).newInstance();
					
					new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								logger.info("开始执行抓取:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
								su.crawlerLastStartTime = new Date().getTime();
								su.crawlerLastFinishTime = -1;
								su.type = 0;
								task.setStartTime(su.crawlerLastStartTime);
								su.status = 0;
								task.initCachedValue();
								if (LoaderUtil.isInterface(config)) {
									task.process2();
								} else {
									task.process();
								}
								task.flushCachedValue();
								
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								su.status= 1;
								su.crawlerLastFinishTime = new Date().getTime();
								task.produceCrawlerLog(su.crawlerLastFinishTime);
								MyClient client = new MyClient();			
								client.getHtml(FINISH_URL, "utf-8");
								logger.info("结束抓取:" + task.getWrapperId() + "[" + task.getWrapperName() + "]");
							}
							
						}}).start();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						Thread.sleep(5000L); //等5秒钟再开始调度下一个任务
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){ //每隔5分钟对配置文件进行扫描

			@Override
			public void run() {
				System.out.println("start");
				List<String> wrappers = getValidWrapper();
				Set<String> keys = scheduleUnits.keySet();
				keys.retainAll(wrappers);
				for(String wrapper : wrappers) {					
					if (scheduleUnits.containsKey(wrapper)) {
						ScheduleUnit su = scheduleUnits.get(wrapper);
						loadScheduleParams(su);
						schedule(su);
					} else {
						ScheduleUnit su = new ScheduleUnit();
						su.wrapperId = wrapper;
						loadScheduleParams(su);
						scheduleUnits.put(wrapper, su);
						schedule(su);
					}
					
				}
				System.out.println("end");
			}}, 
				new Date(),
				1*60*1000L
				);
	}
	
	public static void main(String[] args) {
		instance.schedule();
	}
	
	public String getCrawlerDuration(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		long parserTime = 0;
		try {
			parserTime = Long.parseLong(su.crawlerParserTime);
		} catch(Exception e) {
			return "";
		}
		return "" + parserTime/60000;
	}
	
	public String getRecheckDuration(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		long parserTime = 0;
		try {
			parserTime = Long.parseLong(su.recheckParserTime);
		} catch(Exception e) {
			return "";
		}
		return "" + parserTime/60000;
	}
	
	public String getStatus(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		if (su.isInSchedule()) {
			return "运行中";
		} else {
			return "空闲";
		}
	}
	public String getType(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		if (su.isCrawler()) {
			return "抓取";
		} else {
			return "Recheck";
		}
	}
	public String getStartTime(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		
		if (su.isCrawler()) {
			if (su.crawlerLastStartTime == -1) return "&nbsp;";
			return format.format(new Date(su.crawlerLastStartTime));
		} else {
			if (su.recheckLastStartTime == -1) return "&nbsp;";
			return format.format(new Date(su.recheckLastStartTime));
		}
	}
	public String getEndTime(String wrapperId) {
		if (!scheduleUnits.containsKey(wrapperId)) return "";
		ScheduleUnit su = scheduleUnits.get(wrapperId);		
		if (su.isCrawler()) {
			if (su.crawlerLastFinishTime == -1) return "&nbsp;";
			return format.format(new Date(su.crawlerLastFinishTime));
		} else {
			if (su.recheckLastFinishTime == -1) return "&nbsp;";
			return format.format(new Date(su.recheckLastFinishTime));
		}
	}
	
	public String setDuration(String wrapperId, String type, String value) {
		if (!scheduleUnits.containsKey(wrapperId)) return "设置失败,没有找到解析类!";
		if (!value.matches("\\d+")) return "设置失败,输入格式有误!";
		ScheduleUnit su = scheduleUnits.get(wrapperId);
		FileOutputStream fos = null;
		try {
			String filePath = ScheduleManager.class.getClassLoader().getResource("schedule").getFile();
			File f = new File(filePath + File.separator + wrapperId + ".properties");
			fos = new FileOutputStream(f);
			if ("crawler".equals(type)) {
				su.crawlerParserTime = Long.parseLong(value)*60*1000 + "";
			} else {
				su.recheckParserTime = Long.parseLong(value)*60*1000 + "";
			}
			String str = "crawler.pasertime=" + su.crawlerParserTime + "\r\n";
			str += "crawler.starttime=" + su.crawlerStartTime + "\r\n";
			str += "recheck.parsertime=" + su.recheckParserTime + "\r\n";
			str += "recheck.starttime=" + su.recheckStartTime;
			fos.write(str.getBytes("utf8"));
		} catch(Exception e) {
			return "设置失败!";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	static class ScheduleUnit {
		String wrapperId;
		
		Date loadedDate = new Date();
		
		long crawlerLastStartTime = -1;
		
		long crawlerLastFinishTime = -1;
		
		long recheckLastStartTime = -1;
		
		long recheckLastFinishTime = -1;		
		
		int type;  //0-抓取调度 1-recheck调度
		
		int status = 1; //0-调度中 1-结束调度
		
		String crawlerStartTime;
		
		String crawlerParserTime;
		
		String recheckStartTime;
		
		String recheckParserTime;
		
		boolean isInSchedule() {
			return status == 0;
		}
		
		boolean isCrawler() {
			return type == 0;
		}		
	}
}
