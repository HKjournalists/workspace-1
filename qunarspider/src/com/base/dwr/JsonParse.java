package com.base.dwr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import com.base.classloader.LoaderUtil;
import com.base.classloader.ManageClassLoader;
import com.qunar.deals.BookingCheck.BookingCheckUnit;
import com.qunar.deals.parse.AbstractDealsParse;
import com.qunar.deals.util.ImageUtil;
import com.qunar.deals.util.Itinerary;
import com.qunar.deals.util.MyClient;
import com.qunar.deals.util.NekoHtmlParser;
import com.qunar.deals.util.RouteDetail;
import com.qunar.deals.util.RouteDetail4Test;
import com.qunar.deals.util.ShipDetail;
import com.qunar.deals.util.StringUtil;

public class JsonParse {

	final String WRAPPER_CLASSPATH = ResourceBundle.getBundle("file").getString("wrapper.classpath");
	public String offlineCheckResult(String oOtherInfo, String oFunction, String oType, String oUrl, String tUrl,String wrapperId) {
		ManageClassLoader cl = ManageClassLoader.getInstance();
		final File config = new File(WRAPPER_CLASSPATH + wrapperId + File.separator + "wrapper.n3");
		String parser = LoaderUtil.getParser(config);
		if (parser == null) return "未找到wrapper";
		final boolean isInterface = LoaderUtil.isInterface(config);
		
		try {
			AbstractDealsParse task = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
			if (isInterface) {
				oType = null;
				oFunction = "";
				oOtherInfo = "";
				oUrl = tUrl;
				
			}
			return task.testOffline(oUrl, oType, oFunction, oOtherInfo).toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "测试失败。。。";
	}
	public String bookingCheckResult(String wrapperId, String url) {
		ManageClassLoader cl = ManageClassLoader.getInstance();
		final File config = new File(WRAPPER_CLASSPATH + wrapperId + File.separator + "wrapper.n3");
		String parser = LoaderUtil.getParser(config);
		if (parser == null) return "未找到wrapper";
		final boolean isInterface = LoaderUtil.isInterface(config);
		if (!isInterface) {
			return "非接口无需测试booking校验";
		}
		try {
			AbstractDealsParse task = (AbstractDealsParse)cl.loadClass(parser, wrapperId).newInstance();
			BookingCheckUnit unit = new BookingCheckUnit();
			unit.setWrapperId(wrapperId);
			unit.setSourceUrl(url);
			RouteDetail result = task.checkInterfaceRoute4Test(unit);
			if (result == null) {
				return "未实现booking校验接口";
			} else {
				return "价格:" + result.getPrice() ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "未获得价格";
	}
	public List<String> getDateList(String jsonKey) {
		List<String> temp = new ArrayList();
		ResourceBundle resource = ResourceBundle.getBundle("file");
		String wrapperPath = resource.getString("BasePath") + "JSON" + File.separator + jsonKey;
		File wrapperDir = new File(wrapperPath);
		for(String dateDir : wrapperDir.list()) {
			temp.add(dateDir);
		}
		Collections.sort(temp);
		List<String> result = new ArrayList<String>();
		for(int i=temp.size() - 1; i >= 0; i--) {
			result.add(temp.get(i));
			if (result.size() > 30) break;
		}
		return result;
	}
	
	public List<String> getFileList(String jsonKey, String dateString) {
		String regex = "\\w+\\.\\d+";
		List<String> result = new ArrayList();
		ResourceBundle resource = ResourceBundle.getBundle("file");
		String wrapperPath = resource.getString("BasePath") + "JSON" + File.separator + jsonKey;		
		File dir = new File(wrapperPath + File.separator + dateString);
		if (!dir.exists()) return result;
		for(String filename : dir.list()) {
			if (!filename.matches(regex)) continue;
			result.add(filename);
		}
		Collections.sort(result, new Comparator<String>() {

			@Override
			public int compare(String a, String b) {
				String[] aa = a.split("\\.");
				Integer ia = Integer.parseInt(aa[1]);
				String[] bb = b.split("\\.");
				Integer ib = Integer.parseInt(bb[1]);
				return ia.compareTo(ib);
			}});
		return result;
	}
	
	public String doInsert(String jsonKey, String dateString, String filename) { 
		ResourceBundle resource = ResourceBundle.getBundle("file");
		String wrapperPath = resource.getString("BasePath") + "JSON" + File.separator + jsonKey;		
		String jsonFilePath = wrapperPath + File.separator + dateString + File.separator + filename;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(jsonFilePath);
			byte[] bs = new byte[fis.available()];
			fis.read(bs);
			String jsonString = new String(bs, "UTF-8");
			JSONArray array = JSONArray.fromString(jsonString);
			for(int i = 0; i < array.length(); i++) {
				JSONObject object = (JSONObject)array.get(i);
				String oriFilePath = object.getString("route_snapShotSavePath");
				if (oriFilePath != null && !oriFilePath.isEmpty()) {
					ImageUtil.DownLoadIamge(object.getString("route_snapShot"), oriFilePath);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String url = resource.getString("CallbackUrl");
		Vector<String> params = new Vector();
		try {
			params.add(URLEncoder.encode(jsonFilePath, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url = StringUtil.fillString(url, params);
		MyClient client = new MyClient();
		client.getHtml(url, "UTF-8");
		return url;
	}
	public List<RouteDetail> getRoutes(String jsonKey, String dateString, String filename) {
		List<RouteDetail> result = new ArrayList();
		ResourceBundle resource = ResourceBundle.getBundle("file");
		String wrapperPath = resource.getString("BasePath") + "JSON" + File.separator + jsonKey;		
		File jsonFile = new File(wrapperPath + File.separator + dateString + File.separator + filename);
		if (jsonFile.exists() && jsonFile.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(jsonFile);
				byte[] bs = new byte[fis.available()];
				fis.read(bs);
				String jsonString = new String(bs, "UTF-8");
				JSONArray array = JSONArray.fromString(jsonString);
				for(int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject)array.get(i);
					RouteDetail4Test detail = new RouteDetail4Test();
					result.add(detail);
					detail.setTitle(object.getString("title"));
					detail.setArrive(object.getString("arrive"));
					detail.setDateOfBookingExpire(object.getString("dateOfBookingExpire"));
					detail.setDateOfDeparture(object.getString("dateOfDeparture"));
					detail.setDateOfExpire(object.getString("dateOfExpire"));
					detail.setDeparture(object.getString("departure"));
					if (object.get("feature") instanceof JSONArray) {
						JSONArray featureArray = object.getJSONArray("feature");
						List<String> feature = new ArrayList();
						for(Object o : featureArray.toArray()) {
							feature.add((String)o);
						}
						detail.setFeature(feature);
					} else {
						detail.setFeature(new ArrayList());
					}
					detail.setFunction(object.getString("function"));
					detail.setItineraryDay(object.getString("itineraryDay"));
					if (object.get("miscellaneous") instanceof JSONArray) {
						JSONArray miscellaneousArray = object.getJSONArray("miscellaneous");
						List<Itinerary> miscellaneous = new ArrayList();
						for(int j = 0; j < miscellaneousArray.length(); j++) {
							JSONObject miscellaneousObject = (JSONObject)miscellaneousArray.get(j);
							Itinerary it = new Itinerary();
							miscellaneous.add(it);
							it.setDay(miscellaneousObject.getInt("day"));
							it.setDescription(miscellaneousObject.getString("description"));
							it.setFromCity(miscellaneousObject.getString("fromCity"));
							it.setToCity(miscellaneousObject.getString("toCity"));
							it.setTitle(miscellaneousObject.getString("title"));
						}
						detail.setMiscellaneous(miscellaneous);
					} else {
						detail.setMiscellaneous(new ArrayList());
					}
					detail.setPrice(object.getString("price"));
					detail.setRoute_snapShot(object.getString("route_snapShot"));
					detail.setStarGrade(object.getString("starGrade"));

					if (object.get("subject") instanceof JSONArray) {
						JSONArray subjectArray = object.getJSONArray("subject");
						List<String> subject = new ArrayList();
						for(Object o : subjectArray.toArray()) {
							subject.add((String)o);
						}
						detail.setSubject(subject);
					} else {
						detail.setSubject( new ArrayList());
					}
					detail.setWrapperId(object.getString("wrapperId"));
					detail.setTraffic(object.getString("traffic"));
					detail.setType(object.getString("type"));
					detail.setUrl(object.getString("url"));
					try {
						Object oo = object.get("currency");
						if ((oo instanceof  JSONNull) || oo == null) {
							detail.setCurrency("CNY");
						} else {
							detail.setCurrency(object.getString("currency"));
						}
					} catch(Exception e) {
						detail.setCurrency("CNY");
					}
					
					if (object.get("sightSpot") instanceof JSONArray) {
						JSONArray sightSpotArray = object.getJSONArray("sightSpot");
						Set<String> sightSpot = new HashSet();
						for(Object o : sightSpotArray.toArray()) {
							sightSpot.add((String)o);
						}
						detail.setSightSpot(sightSpot);
					} else {
						detail.setSightSpot( new HashSet());
					}
					
					try {
						Object o = object.get("ship");
						if (!(o instanceof  JSONNull)) {
							JSONObject shipObject = object.getJSONObject("ship");
							if (shipObject != null) {
								ShipDetail ship = new ShipDetail();
								detail.setShip(ship);
								String fromHub = shipObject.getString("fromHub");
								ship.setFromHub(fromHub);
								
								String toHub = shipObject.getString("toHub");
								ship.setToHub(toHub);
								
								String shipName = shipObject.getString("shipName");
								ship.setShipName(shipName);
								
								String company = shipObject.getString("company");
								ship.setCompany(company);
								
								Object datesO = shipObject.get("dates");
								if (!(datesO instanceof  JSONNull)) {
									JSONArray datesJSON = shipObject.getJSONArray("dates");
									if (datesJSON != null) {
										List<String> dates = new ArrayList();
										for(int j = 0; j < datesJSON.length(); j++) {
											dates.add(datesJSON.getString(j));
										}
										ship.setDates(dates);
									}
								}
								
								
								String voyageRegion = shipObject.getString("voyageRegion");
								ship.setVoyageRegion(voyageRegion);
								
								String priceHtml = shipObject.getString("priceHtml");
								ship.setPriceHtml(priceHtml);
								
								String cities = shipObject.getString("cities");
								ship.setCities(cities);
								
								String voyageImg = shipObject.getString("voyageImg");
								ship.setVoyageImg(voyageImg);
								
							}
						}
					} catch(Exception e) {
						
					}
					
					Object oListInfo = object.get("listInfo");
					if (!(oListInfo instanceof  JSONNull)) {
						String listInfo = object.getString("listInfo");
						Map<String, String> listInfoMap = getListInfoMap(listInfo);
						String oType = listInfoMap.get("type");
						String oFunction = listInfoMap.get("function");
						String oOtherInfo = listInfoMap.get("otherInfo");
						String oSubject = listInfoMap.get("subject");
						String oUrl = listInfoMap.get("url");
						detail.setoType(oType);
						detail.setoFunction(oFunction);
						detail.setoOtherInfo(oOtherInfo);
						detail.setoSubject(oSubject);
						detail.setoUrl(oUrl);
					}
					
					
				}				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
			}
			return result;
		} else return new ArrayList();
		
	}
	public Map<String, String> getListInfoMap(String listInfo) {
		Map<String, String> result = new HashMap();
		NekoHtmlParser parser = new NekoHtmlParser();
		parser.load(listInfo, "utf8");
		
		NodeList list = parser.selectNodes("//LISTINFO");
		for(int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String type = parser.getNodeText("TYPE", node);
			String value = parser.getNodeText("VALUE", node);
			result.put(type, value);
		}
		return result;
	}
	
}
