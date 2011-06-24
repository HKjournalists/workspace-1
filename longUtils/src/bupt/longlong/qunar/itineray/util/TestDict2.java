package bupt.longlong.qunar.itineray.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bupt.longlong.qunar.deals.util.RouteDetail;
import bupt.longlong.qunar.itineray.ie.DayTrip;
import bupt.longlong.qunar.itineray.ie.Itinerary;
import bupt.longlong.qunar.itineray.ie.Itineray;
import bupt.longlong.qunar.itineray.ie.ItinerayExtractor;
import bupt.longlong.qunar.itineray.ie.Sight;
import bupt.longlong.utils.StringUtil;

public class TestDict2 {

	public class RouteType {

		public String type;
		public String cities;
	}

	protected static List<Itinerary> getItineraries(List<DayTrip> dayTrips, Set<String> sightSpots, Set<String> sightSpotsC, RouteType routeType) {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	//		List<DayTrip> dayTrips = new ArrayList<DayTrip>();
	//		
	//		DayTrip dt = new DayTrip();
	//		dt.setDayNum(1);
	//		dt.setTitleInfo("北京-上海");
	//		dt.setDescription("做火车去上海，看黄浦江");
	//		dayTrips.add(dt);
	//		
	//		dt = new DayTrip();
	//		dt.setDayNum(2);
	//		dt.setTitleInfo("上海-南京");
	//		dt.setDescription("做火车去南京，看中山陵");
	//		dayTrips.add(dt);
	//		RouteType rt = new RouteType();
	//		List<Itinerary> its = getItineraries(dayTrips, new HashSet(), new HashSet(), rt);
	//		int x = 3;
	}

}
