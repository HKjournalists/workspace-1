package edu.bupt.qunar.itineray.ie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.bupt.qunar.itineray.dict.SightDictionary;
import edu.bupt.qunar.itineray.util.ItinerayUtil;
import edu.bupt.utils.StringUtil;


public class ItinerayExtractor {

	private static Logger logger = Logger.getLogger(ItinerayExtractor.class);

	public static final String dayTripPatternStr = "(?:^|[\n\r])\\s*([第D]\\s*([\\d一二三四五六七八九十]+)\\s*[天日]?) *[:：]?([^\n]*)\n";
	//public static final String dayTripPatternStr = "(?:^|\n)\\s*(D[\\d一二三四五六七八九十]+)\\s*[:：](?:[^\n]*)\n([^\n]+)\n";
	public static final Pattern dayTripPattern = Pattern.compile(dayTripPatternStr, Pattern.DOTALL);

	public static final String fromCityPatternStr = "(?:从)";
	//public static final String toCityPatternStr = "(?:飞往|抵达|到达|到|住宿|入住)$";
	public static final String toCityPatternStr = "(?:飞往|抵达|赴|到达|住宿|前往|入住|至|住宿\\s*[:：]\\s*)$";
	public static final Pattern toCityPattern = Pattern.compile(toCityPatternStr);
	public static final String[] dayTripPatternStrs = new String[] { "^(第\\s*([\\d一二三四五六七八九十]+)\\s*[天日])\\s*[:：]?(.*)", "^(D\\s*([\\d一二三四五六七八九十]+))\\s*[:：]?(.*)" };
	public SightDictionary dict = null;

	private static ItinerayExtractor itinerayExtractor = null;

	private static String lastTimestamp = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static {
		try {
			ResourceBundle.getBundle("dict").getString("dict.path");
		} catch (Exception e) {

		}
	}

	//确保一天加载一次
	public static boolean needInit() {
		String today = sdf.format(new Date());
		if (today.equals(lastTimestamp)) {
			return false;
		} else {
			lastTimestamp = today;
			return true;
		}
	}

	public synchronized static ItinerayExtractor getInstance() {
		if (needInit()) {
			itinerayExtractor = new ItinerayExtractor();
			if (!itinerayExtractor.init()) {
				return null;
			}
		}
		return itinerayExtractor;
	}

	private ItinerayExtractor() {}

	private boolean init() {
		try {
			dict = new SightDictionary();
		} catch (Exception e) {
			logger.fatal("load sight dict file failed!");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * extract itineray from itineray's description
	 * 
	 * @param content
	 *            a itineray's description
	 * @return
	 */
	public Itineray extract(String content) {
		Itineray itineray = new Itineray();

		if (content == null) {
			return null;
		}

		// extract day trips
		String title = null;
		String dayTitle = null;
		String titleInfo = "";
		String desc = null;

		String dayNumStr = null;

		int lastEndPos = 0;
		List<DayTrip> dayTrips = itineray.getDayTrips();

		Matcher dayTripMatcher = dayTripPattern.matcher(content);
		while (dayTripMatcher.find(lastEndPos)) {
			logger.debug("day start matcher group: " + dayTripMatcher.group());
			if (lastEndPos > 0) {
				desc = content.substring(lastEndPos, dayTripMatcher.start()).trim();

				if (titleInfo.length() < 1) {
					int idx = desc.indexOf("\n");
					if (idx > 0) {
						String firstDesc = desc.substring(0, idx + 1);
						String otherDesc = desc.substring(idx + 1);
						if (firstDesc.length() < 20 && firstDesc.length() < otherDesc.length() && !ItinerayUtil.containPunctuation(titleInfo)) {
							titleInfo = firstDesc;
							title = dayTitle + " " + titleInfo;
							desc = otherDesc;
						}
					}
				} else if (titleInfo.length() > 10 && ItinerayUtil.containPunctuation(titleInfo)) {
					desc = titleInfo + " " + desc;
					title = dayTitle;
					titleInfo = "";
				}

				if (title == null) {
					title = dayTitle + " " + titleInfo;
				}

				int dayNum = ItinerayUtil.normalizeNumber(dayNumStr);
				if (dayNum < 0) { // some error 
					dayNum = dayTrips.size() + 1;
				}

				dayTrips.add(new DayTrip(title, desc, titleInfo, dayNum));
			}

			dayTitle = dayTripMatcher.group(1).trim();
			dayNumStr = dayTripMatcher.group(2).trim();
			titleInfo = dayTripMatcher.group(3).trim();

			title = null;

			lastEndPos = dayTripMatcher.end();
		}

		if (lastEndPos > 0) {
			desc = content.substring(lastEndPos).trim();
			if (titleInfo.length() < 1) {
				int idx = desc.indexOf("\n");
				if (idx > 0) {
					String firstDesc = desc.substring(0, idx + 1);
					String otherDesc = desc.substring(idx + 1);
					if (firstDesc.length() < 20 && firstDesc.length() < otherDesc.length() && !ItinerayUtil.containPunctuation(titleInfo)) {
						titleInfo = firstDesc;
						title = dayTitle + " " + titleInfo;
						desc = otherDesc;
					}
				}
			} else if (titleInfo.length() > 10 && ItinerayUtil.containPunctuation(titleInfo)) {
				desc = titleInfo + " " + desc;
				title = dayTitle;
				titleInfo = "";
			}

			if (title == null) {
				title = dayTitle + " " + titleInfo;
			}

			int dayNum = ItinerayUtil.normalizeNumber(dayNumStr);
			dayTrips.add(new DayTrip(title, desc, titleInfo, dayNum));
		}

		// extract to sights, and to regions
		int size = dayTrips.size();
		if (size > 0) {
			itineray.setDayTotal(dayTrips.get(size - 1).getDayNum());
		}

		for (int i = 0; i < size; i++) {
			DayTrip dayTrip = dayTrips.get(i);
			extractDayTrip2(dayTrip);
		}

		return itineray;
	}

	public String getDayPattern(String line) {
		String result = "";
		for (int j = 0; j < dayTripPatternStrs.length; j++) {
			String tp = dayTripPatternStrs[j];
			if (line.matches(tp)) {
				result = tp;
				break;
			}
		}
		return result;
	}

	public Itineray extract2(List<DayTrip> dayTrips) {
		Itineray itineray = new Itineray();
		// extract to sights, and to regions
		int size = dayTrips.size();
		if (size > 0) {
			itineray.setDayTotal(dayTrips.get(size - 1).getDayNum());
		}

		for (int i = 0; i < size; i++) {
			DayTrip dayTrip = dayTrips.get(i);
			extractDayTrip2(dayTrip);
		}
		RouteContext context = extractRouteContext(dayTrips);
		int sightCount = 0;
		if (context.getRegion().size() > 0) {
			for (int i = 0; i < size; i++) {
				DayTrip dayTrip = dayTrips.get(i);
				extractDayTrip(dayTrip, context);
				sightCount += dayTrip.getToSights().size();
			}
		}
		if (sightCount == 0) {
			if (context.getOptionRegion().size() > 0) {
				context.switchOptionSight();
				for (int i = 0; i < size; i++) {
					DayTrip dayTrip = dayTrips.get(i);
					extractDayTrip(dayTrip, context);
					sightCount += dayTrip.getToSights().size();
				}
				if (sightCount == 0) {
					context.switchOptionSight();
					for (int i = 0; i < size; i++) {
						DayTrip dayTrip = dayTrips.get(i);
						extractDayTrip(dayTrip, context);
						sightCount += dayTrip.getToSights().size();
					}
				}
			}
		}
		return itineray;
	}

	public Itineray extract2(String content) {
		Itineray itineray = new Itineray();

		if (content == null) {
			return null;
		}

		// extract day trips
		String title = null;
		String dayTitle = null;
		String titleInfo = "";
		String desc = null;

		String dayNumStr = null;

		List<DayTrip> dayTrips = itineray.getDayTrips();

		String[] dayContents = content.split("[\r\n]+");
		for (int i = 0; i < dayContents.length; i++) {
			String dayContent = StringUtil.trim(dayContents[i]);
			if (dayContent.isEmpty()) continue;
			String patternStr = "";
			patternStr = getDayPattern(dayContent);
			if (patternStr.isEmpty()) continue;
			Matcher tm = Pattern.compile(patternStr).matcher(dayContent);
			tm.find();
			dayTitle = tm.group(1);
			dayNumStr = tm.group(2);
			titleInfo = tm.group(3);
			title = null;
			desc = "";
			String firstDesc = "";
			String otherDesc = "";
			int j = i + 1;
			for (; j < dayContents.length; j++) {
				String line = StringUtil.trim(dayContents[j]);
				if (line.isEmpty()) continue;
				String p = getDayPattern(line);
				if (p.isEmpty()) {
					if (desc.isEmpty()) {
						firstDesc = line;
					} else {
						otherDesc += line + "\r\n";
					}
					desc += line + "\r\n";
				} else {
					j = j - 1;
					break;
				}
			}
			i = j;
			if (titleInfo.length() < 1) {
				if (firstDesc.length() > 0) {
					if (firstDesc.length() < 20 && firstDesc.length() < otherDesc.length() && !ItinerayUtil.containPunctuation(firstDesc)) {
						titleInfo = firstDesc;
						title = dayTitle + " " + titleInfo;
						desc = otherDesc;
					}
				}
			} else if (titleInfo.length() > 20 && ItinerayUtil.containPunctuation(titleInfo)) {
				desc = titleInfo + " " + desc;
				title = dayTitle;
				titleInfo = "";
			}

			if (title == null) {
				title = dayTitle + " " + titleInfo;
			}

			int dayNum = ItinerayUtil.normalizeNumber(dayNumStr);
			if (dayNum < 0) { // some error 
				dayNum = dayTrips.size() + 1;
			}

			dayTrips.add(new DayTrip(title, desc, titleInfo, dayNum));
		}
		// extract to sights, and to regions
		int size = dayTrips.size();
		if (size > 0) {
			itineray.setDayTotal(dayTrips.get(size - 1).getDayNum());
		}

		for (int i = 0; i < size; i++) {
			DayTrip dayTrip = dayTrips.get(i);
			extractDayTrip2(dayTrip);
		}
		//TODO
		RouteContext context = extractRouteContext(dayTrips);
		int sightCount = 0;
		if (context.getRegion().size() > 0) {
			for (int i = 0; i < size; i++) {
				DayTrip dayTrip = dayTrips.get(i);
				//TODO
				extractDayTrip(dayTrip, context);
				sightCount += dayTrip.getToSights().size();
			}
		}
		if (sightCount == 0) {
			if (context.getOptionRegion().size() > 0) {
				context.switchOptionSight();
				for (int i = 0; i < size; i++) {
					DayTrip dayTrip = dayTrips.get(i);
					extractDayTrip(dayTrip, context);
					sightCount += dayTrip.getToSights().size();
				}
				if (sightCount == 0) {
					context.switchOptionSight();
					for (int i = 0; i < size; i++) {
						DayTrip dayTrip = dayTrips.get(i);
						extractDayTrip(dayTrip, context);
						sightCount += dayTrip.getToSights().size();
					}
				}
			}
		}
		return itineray;
	}

	//判断线路的上下文
	public RouteContext extractRouteContext(List<DayTrip> dayTrips) {
		RouteContext context = new RouteContext();
		int domesticCount = 0;
		int foreignCount = 0;
		for (DayTrip dayTrip : dayTrips) {
			for (Sight sight : dayTrip.getToRegions()) {
				Sight country = sight.getCountry();
				if (country == null) continue;
				if ("中国".equals(country.getName())) {
					domesticCount++;
				} else {
					foreignCount++;
				}
			}
			for (OptionSight oSight : dayTrip.getCitiesByTitle().values()) {
				int domestic = 0;
				int foreign = 0;
				for (Sight sight : oSight.getOptionSights()) {
					Sight country = sight.getCountry();
					if (country == null) continue;
					if ("中国".equals(country.getName())) {
						domestic++;
					} else {
						foreign++;
					}
				}
				if (domestic > 0) domesticCount++;
				if (foreign > 0) foreignCount++;
			}
			for (Object o : dayTrip.getCitiesByDesc()) {
				if (o instanceof Sight) {
					Sight sight = (Sight) o;
					Sight country = sight.getCountry();
					if (country == null) continue;
					if ("中国".equals(country.getName())) {
						domesticCount++;
					} else {
						foreignCount++;
					}
				} else if (o instanceof OptionSight) {
					OptionSight oSight = (OptionSight) o;
					int domestic = 0;
					int foreign = 0;
					for (Sight sight : oSight.getOptionSights()) {
						Sight country = sight.getCountry();
						if (country == null) continue;
						if ("中国".equals(country.getName())) {
							domestic++;
						} else {
							foreign++;
						}
					}
					if (domestic > 0) domesticCount++;
					if (foreign > 0) foreignCount++;
				}
			}
		}

		if (domesticCount > foreignCount) { //国内游
			context.setDomestic(true);
			Map<Sight, Integer> regionCount = new HashMap<Sight, Integer>();
			Map<Sight, Integer> provinceCount = new HashMap<Sight, Integer>();
			for (DayTrip dayTrip : dayTrips) {
				provinceCount = new HashMap<Sight, Integer>();
				for (Sight sight : dayTrip.getToRegions()) {
					Sight country = sight.getCountry();
					if (country == null) continue;
					Sight region = sight.getRegion();
					if (region == null) continue;
					Integer count = 0;
					if (regionCount.containsKey(region)) {
						count = regionCount.get(region) + 1;
					}
					regionCount.put(region, count);

					Sight province = sight.getProvince();
					if (province == null) continue;
					count = 0;
					if (provinceCount.containsKey(province)) {
						count = provinceCount.get(province) + 1;
					}
					provinceCount.put(province, count);
				}
				for (OptionSight oSight : dayTrip.getCitiesByTitle().values()) {
					Map<Sight, Integer> tCount = new HashMap<Sight, Integer>();
					Map<Sight, Integer> tPCount = new HashMap<Sight, Integer>();
					for (Sight sight : oSight.getOptionSights()) {
						Sight country = sight.getCountry();
						if (country == null) continue;
						Sight region = sight.getRegion();
						if (region == null) continue;
						Integer count = 0;
						if (tCount.containsKey(region)) {
							count = tCount.get(region) + 1;
						}
						tCount.put(region, count);

						Sight province = sight.getProvince();
						if (province == null) continue;
						count = 0;
						if (tPCount.containsKey(province)) {
							count = tPCount.get(province) + 1;
						}
						tPCount.put(province, count);
					}
					for (Sight region : tCount.keySet()) {
						Integer count = 0;
						if (regionCount.containsKey(region)) {
							count = regionCount.get(region) + 1;
						}
						regionCount.put(region, count);
					}
					for (Sight province : tPCount.keySet()) {
						Integer count = 0;
						if (provinceCount.containsKey(province)) {
							count = provinceCount.get(province) + 1;
						}
						provinceCount.put(province, count);
					}
				}
				for (Object o : dayTrip.getCitiesByDesc()) {
					if (o instanceof Sight) {
						Sight sight = (Sight) o;
						Sight country = sight.getCountry();
						if (country == null) continue;
						Sight region = sight.getRegion();
						if (region == null) continue;
						Integer count = 0;
						if (regionCount.containsKey(region)) {
							count = regionCount.get(region) + 1;
						}
						regionCount.put(region, count);

						Sight province = sight.getProvince();
						if (province == null) continue;
						count = 0;
						if (provinceCount.containsKey(province)) {
							count = provinceCount.get(province) + 1;
						}
						provinceCount.put(province, count);

					} else if (o instanceof OptionSight) {
						OptionSight oSight = (OptionSight) o;
						Map<Sight, Integer> tCount = new HashMap<Sight, Integer>();
						Map<Sight, Integer> tPCount = new HashMap<Sight, Integer>();
						for (Sight sight : oSight.getOptionSights()) {
							Sight country = sight.getCountry();
							if (country == null) continue;
							Sight region = sight.getRegion();
							if (region == null) continue;
							Integer count = 0;
							if (tCount.containsKey(region)) {
								count = tCount.get(region) + 1;
							}
							tCount.put(region, count);

							Sight province = sight.getProvince();
							if (province == null) continue;
							count = 0;
							if (tPCount.containsKey(province)) {
								count = tPCount.get(province) + 1;
							}
							tPCount.put(province, count);
						}
						for (Sight region : tCount.keySet()) {
							Integer count = 0;
							if (regionCount.containsKey(region)) {
								count = regionCount.get(region) + 1;
							}
							regionCount.put(region, count);
						}

						for (Sight province : tPCount.keySet()) {
							Integer count = 0;
							if (provinceCount.containsKey(province)) {
								count = provinceCount.get(province) + 1;
							}
							provinceCount.put(province, count);
						}

					}
				}

				//				Sight province = null;
				//				int maxCount = -1;
				//				for(Sight r : provinceCount.keySet()) {
				//					int count = provinceCount.get(r);
				//					if (province == null) {
				//						province = r;
				//						maxCount = count;
				//					} else {
				//						if (maxCount < count) {
				//							province = r;
				//							maxCount = count;
				//						}
				//					}
				//				}
				//				Set<Sight> s = new HashSet();
				//				s.add(province);
				//				context.getSubRegion().put(dayTrip.getDayNum(), s);				
			}
			Sight region = null;
			Sight optionRegion = null;
			int maxCount = -1;
			for (Sight r : regionCount.keySet()) {
				int count = regionCount.get(r);
				if (region == null) {
					region = r;
					maxCount = count;
				} else {
					if (maxCount < count) {
						region = r;
						maxCount = count;
					}
				}
			}
			int c1 = maxCount;
			int c2 = -1;
			regionCount.remove(region);
			for (Sight r : regionCount.keySet()) {
				int count = regionCount.get(r);
				if (optionRegion == null) {
					optionRegion = r;
					maxCount = count;
					c2 = maxCount;
				} else {
					if (maxCount < count) {
						optionRegion = r;
						maxCount = count;
						c2 = maxCount;
					}
				}
			}
			context.getRegion().add(region);
			if (optionRegion != null) {
				context.getOptionRegion().add(optionRegion);
				if (c1 == c2) {
					Map<Sight, Integer> newRegionCount = new HashMap<Sight, Integer>();
					newRegionCount.put(region, -1);
					newRegionCount.put(optionRegion, -1);
					for (DayTrip dayTrip : dayTrips) {
						List<OptionSight> ops = dayTrip.getOptionSights();
						if (ops == null) continue;
						for (OptionSight os : ops) {
							List<Sight> sts = os.getOptionSights();
							if (sts == null) continue;
							for (Sight st : sts) {
								Sight r = st.getRegion();
								if (r == null) continue;
								if (newRegionCount.containsKey(r)) {
									int t = newRegionCount.get(r);
									t++;
									newRegionCount.put(r, t);
								}
							}
						}
					}
					c1 = newRegionCount.get(region);
					c2 = newRegionCount.get(optionRegion);
					if (c1 != c2) {
						context.getRegion().clear();
						context.getOptionRegion().clear();
						if (c1 > c2) {
							context.getRegion().add(region);
							context.getOptionRegion().add(optionRegion);
						} else {
							context.getRegion().add(optionRegion);
							context.getOptionRegion().add(region);
						}
					}
				}
			}

		} else {
			context.setDomestic(false);
			Map<Sight, Integer> countryCount = new HashMap<Sight, Integer>();
			for (DayTrip dayTrip : dayTrips) {
				for (Sight sight : dayTrip.getToRegions()) {
					Sight country = sight.getContinent();
					if (country == null) continue;
					//if ("中国".equals(country.getName())) continue;
					Integer count = 0;
					if (countryCount.containsKey(country)) {
						count = countryCount.get(country) + 1;
					}
					countryCount.put(country, count);
				}
				for (OptionSight oSight : dayTrip.getCitiesByTitle().values()) {
					Map<Sight, Integer> tCount = new HashMap<Sight, Integer>();
					for (Sight sight : oSight.getOptionSights()) {
						Sight country = sight.getContinent();
						if (country == null) continue;
						//if ("中国".equals(country.getName())) continue;
						Integer count = 0;
						if (tCount.containsKey(country)) {
							count = tCount.get(country) + 1;
						}
						tCount.put(country, count);
					}
					for (Sight region : tCount.keySet()) {
						Integer count = 0;
						if (countryCount.containsKey(region)) {
							count = countryCount.get(region) + 1;
						}
						countryCount.put(region, count);
					}
				}
				for (Object o : dayTrip.getCitiesByDesc()) {
					if (o instanceof Sight) {
						Sight sight = (Sight) o;
						Sight country = sight.getContinent();
						if (country == null) continue;
						//if ("中国".equals(country.getName())) continue;
						Integer count = 0;
						if (countryCount.containsKey(country)) {
							count = countryCount.get(country) + 1;
						}
						countryCount.put(country, count);

					} else if (o instanceof OptionSight) {
						OptionSight oSight = (OptionSight) o;
						Map<Sight, Integer> tCount = new HashMap<Sight, Integer>();
						for (Sight sight : oSight.getOptionSights()) {
							Sight country = sight.getContinent();
							if (country == null) continue;
							//if ("中国".equals(country.getName())) continue;
							Integer count = 0;
							if (tCount.containsKey(country)) {
								count = tCount.get(country) + 1;
							}
							tCount.put(country, count);
						}
						for (Sight region : tCount.keySet()) {
							Integer count = 0;
							if (countryCount.containsKey(region)) {
								count = countryCount.get(region) + 1;
							}
							countryCount.put(region, count);
						}

					}
				}
			}
			Sight region = null;
			Sight optionRegion = null;
			int maxCount = -1;
			for (Sight r : countryCount.keySet()) {
				int count = countryCount.get(r);
				if (region == null) {
					region = r;
					maxCount = count;
				} else {
					if (maxCount < count) {
						region = r;
						maxCount = count;
					}
				}
			}
			int c1 = maxCount;
			int c2 = -1;
			countryCount.remove(region);
			for (Sight r : countryCount.keySet()) {
				int count = countryCount.get(r);
				if (optionRegion == null) {
					optionRegion = r;
					maxCount = count;
					c2 = maxCount;
				} else {
					if (maxCount < count) {
						optionRegion = r;
						maxCount = count;
						c2 = maxCount;
					}
				}
			}
			context.getRegion().add(region);
			if (optionRegion != null) {
				context.getOptionRegion().add(optionRegion);
				if (c1 == c2) {
					Map<Sight, Integer> newRegionCount = new HashMap<Sight, Integer>();
					newRegionCount.put(region, -1);
					newRegionCount.put(optionRegion, -1);
					for (DayTrip dayTrip : dayTrips) {
						List<OptionSight> ops = dayTrip.getOptionSights();
						if (ops == null) continue;
						for (OptionSight os : ops) {
							List<Sight> sts = os.getOptionSights();
							if (sts == null) continue;
							for (Sight st : sts) {
								Sight r = st.getContinent();
								if (r == null) continue;
								if (newRegionCount.containsKey(r)) {
									int t = newRegionCount.get(r);
									t++;
									newRegionCount.put(r, t);
								}
							}
						}
					}
					c1 = newRegionCount.get(region);
					c2 = newRegionCount.get(optionRegion);
					if (c1 != c2) {
						context.getRegion().clear();
						context.getOptionRegion().clear();
						if (c1 > c2) {
							context.getRegion().add(region);
							context.getOptionRegion().add(optionRegion);
						} else {
							context.getRegion().add(optionRegion);
							context.getOptionRegion().add(region);
						}
					}
				}
			}
		}
		return context;
	}

	public static boolean isInRegion(Sight sight, RouteContext context) {
		if (sight == null) return false;
		if (context.isDomestic()) {
			Sight region = sight.getRegion();
			if (region == null) return false;
			return context.getRegion().contains(region);
		} else {
			Sight country = sight.getContinent();
			if (country == null) return false;
			return context.getRegion().contains(country);
		}
	}

	/** extract day trip's other info by day trip's title and description */
	public void extractDayTrip(DayTrip dayTrip, RouteContext context) {
		// extract to regions
		dayTrip.getToRegions().clear();
		dayTrip.getSights().clear();
		dayTrip.getCitiesByDesc().clear();
		dayTrip.setFromCity(null);
		dayTrip.getToSights().clear();
		dayTrip.getToAuditSights().clear();
		boolean fromFlag = true;//有from
		List<Sight> toRegionList = new ArrayList<Sight>();
		List<OptionSight> optionToRegions = dict.extractOptionSights(dayTrip.getTitleInfo());
		int i = 0;
		for (OptionSight option : optionToRegions) {
			i++;
			if (option.getOptionSights().size() == 1) {
				Sight s = option.getOptionSights().get(0);
				if (isInRegion(s, context)) {
					toRegionList.add(option.getOptionSights().get(0));
				}

			} else {
				int count = 0;
				Sight validSight = null;
				for (Sight sight : option.getOptionSights()) {
					if (isInRegion(sight, context)) {
						validSight = sight;
						count++;
					}
				}
				if (count == 1) {
					toRegionList.add(validSight);
				} else {
					if (option.getOptionSights().size() == 2) {
						Sight s1 = option.getOptionSights().get(0);
						Sight s2 = option.getOptionSights().get(1);
						validSight = null;
						boolean b = false;
						Sight p = null;
						Sight p1 = s2;
						while ((p = p1.getParentSight()) != null) {
							if (p == s1) {
								validSight = p;
								b = true;
								break;
							} else {
								p1 = p;
							}
						}
						if (!b) {
							p = null;
							p1 = s1;
							while ((p = p1.getParentSight()) != null) {
								if (p == s2) {
									validSight = p;
									b = true;
									break;
								} else {
									p1 = p;
								}
							}
						}
						if (b) {
							if (isInRegion(validSight, context)) {
								toRegionList.add(validSight);
							}
						}

					}
				}

			}
			if (i == 1) {
				if (toRegionList.size() == 0) fromFlag = false;
			}
		}
		dayTrip.getToRegions().addAll(toRegionList);
		List<Sight> toRegionCityList = new ArrayList<Sight>();
		for (Sight sight : toRegionList) {
			Sight country = sight.getCountry();
			if (country == null) {
				toRegionCityList.add(sight);
			} else if (!"中国".equals(country.getName())) {
				toRegionCityList.add(sight);
			} else {
				if ("景区".equals(sight.getType()) || "城市".equals(sight.getType())) {
					toRegionCityList.add(sight);
				}
			}
		}

		dayTrip.getToRegions().clear();
		dayTrip.getToRegions().addAll(toRegionCityList);
		List<OptionSight> optionSights = dict.extractOptionSights(dayTrip.getDescription(), context);
		boolean titleNoRegion = dayTrip.getToRegions().size() == 0;
		if (toRegionCityList.size() == 0) {
			dayTrip.getCitiesByDesc().addAll(this.getOptionSightCity(optionSights, 2));
			fromFlag = false;
			for (Object o : dayTrip.getCitiesByDesc()) {
				if (o instanceof Sight) {
					Sight sight = (Sight) o;
					Sight country = sight.getCountry();
					if (country == null) continue;
					if (isInRegion(sight, context)) {
						if (titleNoRegion) {
							dayTrip.getToRegions().add(sight);
						}
						toRegionCityList.add(sight);
					}

				} else if (o instanceof OptionSight) {
					OptionSight oSight = (OptionSight) o;
					int count = 0;
					Sight validSight = null;
					for (Sight sight : oSight.getOptionSights()) {
						if (isInRegion(sight, context)) {
							validSight = sight;
							count++;
						}
					}
					if (count == 1) {
						if (titleNoRegion) {
							dayTrip.getToRegions().add(validSight);
						}
						toRegionCityList.add(validSight);
					}
				}
			}

		}
		// look up all to regions prefix such as 前往 住宿....
		ArrayList<Sight> toRegions = new ArrayList<Sight>();
		for (OptionSight optionSight : optionSights) {
			if (!optionSight.isToRegion()) {
				continue;
			}
			//TODO
			Sight rightSight = getRightSight(optionSight, toRegionCityList);
			if (!isInRegion(rightSight, context)) continue;
			if (rightSight != null) {
				dayTrip.getSights().add(rightSight);
			}

			if (rightSight != null && optionSight.isToRegion()) {
				toRegions.add(rightSight);
			}
		}

		// look up other
		for (OptionSight optionSight : optionSights) {
			if (optionSight.isToRegion()) {
				continue;
			}

			Sight rightSight = getRightSight(optionSight, toRegionCityList);
			if (rightSight != null) {
				if (!isInRegion(rightSight, context)) continue;
				dayTrip.getSights().add(rightSight);
			}
		}

		if (toRegionCityList == null || toRegionCityList.size() == 0) {
			// to regions extract
			ArrayList<Sight> scenics = new ArrayList<Sight>();
			ArrayList<Sight> cities = new ArrayList<Sight>();
			ArrayList<Sight> countries = new ArrayList<Sight>();

			for (Sight sight : toRegions) {
				String type = sight.getType();
				if ("景区".equals(type)) {
					scenics.add(sight);
				} else if ("城市".equals(type)) {
					cities.add(sight);
				} else if ("国家".equals(type)) {
					if (!"中国".equals(sight.getName())) {
						countries.add(sight);
					}
				}
			}

			for (Sight sight : scenics) {
				if (!isInRegion(sight, context)) continue;
				dayTrip.addToRegions(sight);
			}
			for (Sight sight : cities) {
				if (!isInRegion(sight, context)) continue;
				dayTrip.addToRegions(sight);
			}
			for (Sight sight : countries) {
				if (!isInRegion(sight, context)) continue;
				dayTrip.addToRegions(sight);
			}
		} else {
			int startIdx = 0;
			if (toRegionCityList.size() > 1) {
				startIdx = 1;
				if (fromFlag) dayTrip.setFromCity(toRegionCityList.get(0));
			}
			for (int idx = startIdx; idx < toRegionCityList.size(); idx++) {
				Sight sight = toRegionCityList.get(idx);
				if (isInRegion(sight, context)) {
					dayTrip.addToRegions(sight);
				}
				Sight toCountry = sight.getCountry();
				if (toCountry != null && !"中国".equals(toCountry.getName())) {
					dayTrip.addToRegions(toCountry);
				}

			}
		}
		for (Sight sight : dayTrip.getSights()) {
			boolean isToSight = filterToSightByToRegion(sight, toRegionCityList, null, dayTrip.getDayNum());

			// added contain noise check 
			boolean containNoise = false;
			String desc = dayTrip.getDescription();
			int end = sight.getEndPos();
			//TODO 景点后4字符，多么人为呀
			if (end + 4 > desc.length()) {
				end = desc.length();
			} else {
				end = end + 4;
			}

			String sightAround = desc.substring(sight.getStartPos(), end);
			if (sightAround.contains("酒店") || sightAround.contains("机场")) {
				containNoise = true;
			}

			if (isToSight && !containNoise) {
				Sight country = sight.getCountry();
				if (country == null) { //never true
					if ("南极洲".equals(sight.getName()) || "北极洲".equals(sight.getName())) {
						if (isInRegion(sight, context)) dayTrip.getToSights().add(sight);
					}
				} else if ("中国".equals(country.getName())) {
					String type = sight.getType();
					if ("景区".equals(type)) {
						if (isInRegion(sight, context)) dayTrip.getToSights().add(sight);
					} else {
						if ("景点".equals(type)) {
							Sight p = sight.getParentSight();
							if (p != null) {
								if ("景区".equals(p.getType())) {
									if (isInRegion(p, context)) {
										dayTrip.getToSights().add(p);
									}
								}
							}
						}
					}
				} else {
					if (isInRegion(sight, context)) dayTrip.getToSights().add(sight);
				}
			} else {
				String type = sight.getType();
				if ("景点".equals(type) || "景区".equals(type)) {
					dayTrip.getToAuditSights().add(sight);
				}
			}
		}

		if (fromFlag) {
			if (dayTrip.getFromCity() != null) {
				dayTrip.getToRegions().remove(dayTrip.getFromCity());
			}
		}
	}

	public void extractDayTrip2(DayTrip dayTrip) {
		// extract to regions
		List<Sight> toRegionList = new ArrayList<Sight>();
		//TODO
		List<OptionSight> optionToRegions = dict.extractOptionSights(dayTrip.getTitleInfo());
		int i = 0;
		for (OptionSight option : optionToRegions) {
			i++;
			if (option.getOptionSights().size() == 1) {
				toRegionList.add(option.getOptionSights().get(0));
			} else {
				dayTrip.getCitiesByTitle().put(i, option);
				//				toRegionList.clear();
				//				logger.info("abort a data!");
				//				break;
			}
		}
		dayTrip.getToRegions().addAll(toRegionList);
		List<Sight> toRegionCityList = new ArrayList<Sight>();
		for (Sight sight : toRegionList) {
			Sight country = sight.getCountry();
			if (country == null) {
				toRegionCityList.add(sight);
			} else if (!"中国".equals(country.getName())) {
				toRegionCityList.add(sight);
			} else {
				if ("景区".equals(sight.getType()) || "城市".equals(sight.getType())) {
					toRegionCityList.add(sight);
				}
			}
		}

		dayTrip.getToRegions().clear();
		dayTrip.getToRegions().addAll(toRegionCityList);

		List<OptionSight> optionSights = dict.extractOptionSights(dayTrip.getDescription());

		if (optionSights != null) {
			dayTrip.getOptionSights().addAll(optionSights);
		}
		if (toRegionCityList.size() == 0) {
			dayTrip.getCitiesByDesc().addAll(this.getOptionSightCity(optionSights, 2));
		}

	}

	public boolean filterToSightByToRegion(Sight sight, List<Sight> toRegionCityList, Sight maxCountCity, int dayNum) {
		if (!sight.isSight() && !sight.isSpot()) { //不是景点和景区的都过滤掉
			return false;
		}
		boolean hasParent = false;

		for (int i = 0; i < toRegionCityList.size(); i++) {
			if (dayNum == 1 && toRegionCityList.size() > 1 && i == 0) {
				continue;
			}
			Sight toSight = toRegionCityList.get(i);
			Sight country = toSight.getCountry();
			if (country == null) {
				if (sight.isAncestor(toSight)) {
					hasParent = true;
					break;
				}
			} else if (!"中国".equals(country.getName())) {
				if (sight.isAncestor(country)) {
					hasParent = true;
					break;
				}
			} else {
				Sight city = toSight.getCity();
				if (city != null && sight.isAncestor(city)) {
					hasParent = true;
					break;
				}
				if (maxCountCity != null && sight.isAncestor(maxCountCity)) {
					hasParent = true;
					break;
				}
			}
		}

		if (!hasParent) {
			return false;
		}

		return true;
	}

	/** filter to sight */
	public boolean filterToSightByToRegion(Sight sight, List<Sight> toRegionCityList, Sight maxCountCity) {
		if (!sight.isSight() && !sight.isSpot()) { //不是景点和景区的都过滤掉
			return false;
		}
		boolean hasParent = false;

		for (int i = 0; i < toRegionCityList.size(); i++) {
			if (toRegionCityList.size() > 1 && i == 0) {
				continue;
			}
			Sight toSight = toRegionCityList.get(i);
			Sight country = toSight.getCountry();
			if (country == null) {
				if (sight.isAncestor(toSight)) {
					hasParent = true;
					break;
				}
			} else if (!"中国".equals(country.getName())) {
				if (sight.isAncestor(country)) {
					hasParent = true;
					break;
				}
			} else {
				Sight city = toSight.getCity();
				if (city != null && sight.isAncestor(city)) {
					hasParent = true;
					break;
				}
				if (maxCountCity != null && sight.isAncestor(maxCountCity)) {
					hasParent = true;
					break;
				}
			}
		}

		if (!hasParent) {
			return false;
		}

		return true;
	}

	//TODO
	/** get right sight from option sights */
	public Sight getRightSight(OptionSight optionSight, List<Sight> toRegionCityList) {
		List<Sight> sightList = optionSight.getOptionSights();

		logger.debug("option sights size: " + sightList.size());
		Sight rightSight = null;
		int size = sightList.size();
		if (size == 1) { // only a option sight
			rightSight = sightList.get(0);
		}

		if ((toRegionCityList == null || toRegionCityList.size() == 0) && size > 0) { // return first sight
			rightSight = sightList.get(0);
		}

		for (Sight sight : sightList) {
			boolean hasSameParent = false;
			for (Sight toSight : toRegionCityList) {
				Sight country = toSight.getCountry();
				if (country == null) {
					if (sight.isAncestor(toSight)) {
						hasSameParent = true;
						break;
					}
				} else if (!"中国".equals(country.getName())) {
					if (sight.isAncestor(country)) {
						hasSameParent = true;
						break;
					}
				} else {
					Sight city = toSight.getCity();
					if (city != null && sight.isAncestor(city)) {
						hasSameParent = true;
						break;
					}
				}
			}

			if (hasSameParent) {
				rightSight = sight;
			}
		}

		// return a new sight with optionsight's position
		if (rightSight != null) {
			Sight newSight = rightSight.clone();
			newSight.setStartPos(optionSight.getStartPos());
			newSight.setEndPos(optionSight.getEndPos());
			return newSight;
		}

		return null;
	}

	public List<Object> getOptionSightCity(List<OptionSight> optionSights, int minLimit) {
		HashMap<Object, Integer> cMap = new HashMap<Object, Integer>();

		List<Sight> toBeChecks = new ArrayList<Sight>();
		// get cities of sights
		for (OptionSight optionSight : optionSights) {
			int optionSize = optionSight.getOptionSights().size();
			if (optionSize == 1) { // no consider multiple alias names
				Sight sight = optionSight.getOptionSights().get(0);
				Sight city = sight.getCity();
				if (sight.isCity()) {
					Integer count = 1;
					if (cMap.containsKey(city)) {
						count = cMap.get(city) + 1;
					}
					cMap.put(city, count);
				} else {
					if (city != null) {
						if (sight.getName().length() > 2) {
							Integer count = 1;
							if (cMap.containsKey(city)) {
								count = cMap.get(city) + 1;
							}
							cMap.put(city, count);
						} else {
							toBeChecks.add(city);
						}
					}
				}

			} else {
				if (optionSize == 2) {
					Sight s1 = optionSight.getOptionSights().get(0);
					Sight s2 = optionSight.getOptionSights().get(1);
					Sight validSight = null;
					boolean b = false;
					Sight p = null;
					Sight p1 = s2;
					while ((p = p1.getParentSight()) != null) {
						if (p == s1) {
							validSight = s2;
							b = true;
							break;
						} else {
							p1 = p;
						}
					}
					if (!b) {
						p = null;
						p1 = s1;
						while ((p = p1.getParentSight()) != null) {
							if (p == s2) {
								validSight = s1;
								b = true;
								break;
							} else {
								p1 = p;
							}
						}
					}
					if (b) {
						Sight city = validSight.getCity();
						if (validSight.isCity()) {
							Integer count = 1;
							if (cMap.containsKey(city)) {
								count = cMap.get(city) + 1;
							}
							cMap.put(city, count);
						} else {
							if (city != null) {
								if (validSight.getName().length() > 2) {
									Integer count = 1;
									if (cMap.containsKey(city)) {
										count = cMap.get(city) + 1;
									}
									cMap.put(city, count);
								} else {
									toBeChecks.add(city);
								}
							}
						}
					}

				} else {
					Integer count = 1;
					if (cMap.containsKey(optionSight)) {
						count = cMap.get(optionSight) + 1;
					}
					cMap.put(optionSight, count);
				}
			}
		}

		for (Sight city : toBeChecks) {
			if (cMap.containsKey(city)) {
				cMap.put(city, cMap.get(city) + 1);
			}
		}
		List<Object> result = new ArrayList<Object>();
		for (Object city : cMap.keySet()) {
			if (cMap.get(city) >= minLimit) {
				result.add(city);
			}
		}
		return result;
	}

	/** get max count city */
	public Sight getMaxCountCityName(List<OptionSight> optionSights, int minLimit) {
		HashMap<String, Integer> cityMap = new HashMap<String, Integer>();
		// get cities of sights
		for (OptionSight optionSight : optionSights) {
			int optionSize = optionSight.getOptionSights().size();
			if (optionSize == 1) { // no consider multiple alias names
				Sight sight = optionSight.getOptionSights().get(0);
				Sight city = sight.getCity();
				if (city != null && sight.getName().length() > 2) {
					Integer count = 1;
					if (cityMap.containsKey(city.getName())) {
						count = cityMap.get(city.getName()) + 1;
					}
					cityMap.put(city.getName(), count);
				}
			}
		}

		int maxCount = -1;
		String maxCountCityName = "";
		Iterator<String> it = cityMap.keySet().iterator();
		while (it.hasNext()) {
			String cityName = (String) it.next();
			int count = cityMap.get(cityName);
			if (count > maxCount) {
				maxCount = count;
				maxCountCityName = cityName;
			}
		}

		if (maxCount >= minLimit) { // the limit
			return dict.getFirstSightByName(maxCountCityName);
		}

		return null;
	}

	//	private HashMap<String, Integer> sortHashMap(HashMap<String, Integer> input) {
	//		Map<String, Integer> tempMap = new HashMap<String, Integer>();
	//		for (String wsState : input.keySet()) {
	//			tempMap.put(wsState, input.get(wsState));
	//		}
	//
	//		List<String> mapKeys = new ArrayList<String>(tempMap.keySet());
	//		List<Integer> mapValues = new ArrayList<Integer>(tempMap.values());
	//		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	//		TreeSet<Integer> sortedSet = new TreeSet<Integer>(mapValues);
	//		Object[] sortedArray = sortedSet.toArray();
	//		int size = sortedArray.length;
	//		for (int i = 0; i < size; i++) {
	//			sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), (Integer) sortedArray[i]);
	//		}
	//		return sortedMap;
	//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/data/haiwai.txt"), "utf8"));
			StringBuilder input = new StringBuilder();
			String url = "";
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				if (line.startsWith("http:")) {
					if (input.length() > 0) {
						System.out.println("=====================" + url);
						System.out.println(input.toString());
						Itineray itineray = itinerayExtractor.extract(input.toString());
						System.out.println(itineray.toString());
					}
					input.delete(0, input.length());
					url = line;
				} else {
					input.append(line + "\n");
				}
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
