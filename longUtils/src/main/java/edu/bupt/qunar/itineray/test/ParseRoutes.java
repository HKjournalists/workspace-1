package edu.bupt.qunar.itineray.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.qunar.deals.util.RouteDetail;
import edu.bupt.qunar.itineray.ie.DayTrip;
import edu.bupt.qunar.itineray.ie.Itinerary;
import edu.bupt.qunar.itineray.ie.Itineray;
import edu.bupt.qunar.itineray.ie.ItinerayExtractor;
import edu.bupt.qunar.itineray.ie.Sight;
import edu.bupt.utils.StringUtil;


public class ParseRoutes {

	static List<Itinerary> getItineraries(String descs, Set<String> sightSpots, Set<String> sightSpotsC) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		//		算法的key就是这里 它是怎么样得到的dayTrips？
		Itineray itineray = itinerayExtractor.extract2(descs);
		List<DayTrip> dayTrips = itineray.getDayTrips();
		List<Itinerary> itineraries = new ArrayList<Itinerary>();
		Map<Integer, Itinerary> itMaps = new HashMap<Integer, Itinerary>();
		for (int i = 0; i < dayTrips.size(); i++) {
			DayTrip dayTrip = dayTrips.get(i);
			Itinerary itinerary = null;
			if (itMaps.containsKey(dayTrip.getDayNum())) {
				itinerary = itMaps.get(dayTrip.getDayNum());
				itinerary.setDescription(itinerary.getDescription() + "\r\n" + dayTrip.getTitle() + "\r\n" + dayTrip.getDescription());
				String toCity = "";
				Set<String> tmpSet = new HashSet<String>();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList<String>();
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
				Set<String> tmpSet = new HashSet<String>();
				for (int j = 0; j < dayTrip.getToRegions().size(); j++) {
					tmpSet.add(dayTrip.getToRegions().get(j).getName());
				}
				List<String> tLst = new ArrayList<String>();
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

	static void parseRoute(String data, String dir) throws Exception {
		String[] datas = data.split("[\r\n]+");
		String info = datas[0];
		String[] infos = info.split("<\\|>");
		Route r = new Route();
		r.id = infos[0];
		r.title = infos[1];
		r.type = infos[2];
		r.function = infos[3];
		r.sourceUrl = infos[4];
		r.departure = infos[5];
		r.itineraryDay = infos[6];
		String desc = "";
		for (int i = 1; i < datas.length; i++) {
			desc += datas[i] + "\r\n";
		}
		Set<String> sightspots = new HashSet<String>();
		List<Itinerary> its = getItineraries(desc, sightspots, new HashSet<String>());
		Set<String> froms = new HashSet<String>();
		//		froms += departure
		String[] ds = r.departure.split(",");
		for (String d : ds) {
			froms.add(d);
		}
		String arrive = "";
		Set<Sight> tCity = new HashSet<Sight>();
		List<Sight> fromSights = new ArrayList<Sight>();
		for (Itinerary itinerary : its) {
			//			第一天处理得到的fromCities加到fromSights里去
			if (itinerary.getDay() == 1) {
				fromSights.addAll(itinerary.getFromCities());
			}
			//			最后一天的toCities区分(满足国家和出境游、国内游的一致)加到tCity里去
			if (itinerary.getDay() != its.size()) {
				List<Sight> tS = new ArrayList<Sight>();
				tS.addAll(itinerary.getToCities());
				for (Sight s : tS) {
					if (s.getCity() == null) {
						if (s.getCountry() != null) {
							if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
								if ("中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
							if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
								if (!"中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
						}
						tCity.add(s);
						continue;
					}
					if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
						if ("中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
						if (!"中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					tCity.add(s);
				}
			}
			//			对中间日子景点的处理
			else {
				List<Sight> tS = new ArrayList<Sight>();
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
							if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
								if ("中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
							if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
								if (!"中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
						}
						tCity.add(s);
						continue;
					}
					if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
						if ("中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
						if (!"中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					tCity.add(s);
				}
			}
			if (itinerary.getDay() > 1) {
				List<Sight> tS = new ArrayList<Sight>();
				tS.addAll(itinerary.getFromCities());
				for (Sight s : tS) {
					if (s.getCity() == null) {
						if (s.getCountry() != null) {
							if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
								if ("中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
							if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
								if (!"中国".equals(s.getCountry().getName())) {
									continue;
								}
							}
						}
						tCity.add(s);
						continue;
					}
					if (RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
						if ("中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					if (RouteDetail.TYPE_DOMESTIC.equals(r.type)) {
						if (!"中国".equals(s.getCity().getCountry().getName())) {
							continue;
						}
					}
					tCity.add(s);
				}
			}
		}

		if (!RouteDetail.TYPE_OUTBOUND.equals(r.type)) {
			if (tCity.size() > 1) {
				Set<Sight> tmpCity = new HashSet<Sight>();
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

		if (sightspots != null) {
			for (String s : sightspots) {
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
			Set<String> tmp = new HashSet<String>();
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
		}

		appendString("D:\\vacation\\" + dir + "\\" + dir + ".txt", data);
		appendString("D:\\vacation\\" + dir + "\\" + dir + ".txt", "arrive:" + arrive);
		appendString("D:\\vacation\\" + dir + "\\" + dir + ".txt", "sightspot:" + sightspots);

		appendString("D:\\vacation\\" + dir + "\\" + dir + ".txt", "================");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String d = "chujin";
		FileInputStream fis = new FileInputStream("D:\\vacation\\" + d + "\\routes.txt");
		byte[] bs = new byte[fis.available()];
		fis.read(bs);

		fis.close();

		String datas = new String(bs, "utf8");
		String[] dataarrays = datas.split("==========");
		for (String data : dataarrays) {
			data = data.trim();
			if (data.isEmpty()) continue;
			parseRoute(data, d);
		}
	}

	protected static void appendString(String path, String content) {
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

}

class Route {

	String id;
	String title;
	String departure;
	String type;
	String function;
	String sourceUrl;
	String itineraryDay;
}
