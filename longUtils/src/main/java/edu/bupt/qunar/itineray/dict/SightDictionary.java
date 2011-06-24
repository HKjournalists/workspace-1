/*
 * @(#)SightDictionary.java 0.01 2010/3/18
 * 
 * Copyright (c) 2010-2012 Qunar, Inc. All rights reserved.
 */
package edu.bupt.qunar.itineray.dict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.tokenizer.CharacterTokenizerFactory;

import edu.bupt.qunar.itineray.ie.ItinerayExtractor;
import edu.bupt.qunar.itineray.ie.OptionSight;
import edu.bupt.qunar.itineray.ie.RouteContext;
import edu.bupt.qunar.itineray.ie.Sight;

public class SightDictionary {

	private static Logger logger = Logger.getLogger(SightDictionary.class);

	/** alias => alias dictionary */
	MapDictionary<String> dictionary = new MapDictionary<String>();

	/*
	 * alias => Sight list dictionary id => Sight list dictionary, the size of
	 * list is 1
	 */
	TrieDictionary<Sight> sightDict = new TrieDictionary<Sight>();

	Map<String, Sight> sights = new HashMap<String, Sight>();

	ExactDictionaryChunker dictChunker = null;

	public Sight getSightById(String id) {
		return sights.get(id);
	}

	String getBlackList() {
		InputStream is = null;
		try {
			is = getClass().getClassLoader().getResourceAsStream("blacklist.txt");
			byte[] bs = new byte[is.available()];
			is.read(bs);
			String result = new String(bs, "utf8");
			return result;
		} catch (Exception e) {
			return "";
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	Reader getDataSource() {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		try {
			String username = ResourceBundle.getBundle("placedb").getString("username");
			String password = ResourceBundle.getBundle("placedb").getString("password");
			String url = ResourceBundle.getBundle("placedb").getString("url");
			String blacklist = getBlackList();
			Set<String> blacks = new HashSet();
			if (blacklist != null) {
				String[] bls = blacklist.split(",");
				for (String bl : bls) {
					bl = bl.replaceAll("^\uFEFF", "");
					blacks.add(bl);
				}
			}
			url = StringEscapeUtils.unescapeHtml(url);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
			stat = conn.createStatement();
			String sql = "select id, name, type, parentSight_id, aliases from Sight";
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String type = rs.getString(3);
				int pId = rs.getInt(4);
				String aliases = rs.getString(5);
				if (!blacks.contains(name)) {
					sb.append(name).append(",").append(id).append(",").append(name).append(",").append(type).append(",,,").append(pId).append("\r\n");
				}
				if (aliases != null) {
					aliases = aliases.replaceAll("\\(.*\\)", "").replaceAll("（.*）", "").trim();
					String[] alias = aliases.split(",|\\s|，|;");
					for (String a : alias) {
						if (blacks.contains(a)) continue;
						if (a == null || a.isEmpty()) continue;
						sb.append(a).append(",").append(id).append(",").append(name).append(",").append(type).append(",,,").append(pId).append("\r\n");
					}
				}
			}
		} catch (Exception e) {
			return null;
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
		StringReader sr = new StringReader(sb.toString());
		return sr;
	}

	public SightDictionary() throws IOException {
		Reader r = getDataSource();
		if (r == null) {
			r = new FileReader("D:/qunarspider/referenced/sight.list");
			logger.info("改用字典文件解析!");
		}
		BufferedReader br = new BufferedReader(r);
		HashMap<String, List<Sight>> aliasHash = new HashMap<String, List<Sight>>();
		HashMap<String, Sight> idHash = new HashMap<String, Sight>();

		int lineNum = 0;
		String line;
		while ((line = br.readLine()) != null) {
			lineNum++;
			if (line.length() == 0) {
				continue;
			}
			String[] fields = line.split("\\s*,\\s*", -1);
			// alias,id,name,type,latitude,longitude,parentSightId

			String alias = fields[0];
			if (fields.length < 7 || alias.length() < 1) {
				logger.debug("wrong format found in sight dict data: " + line);
				continue;
			}

			String id = fields[1];
			String name = fields[2];
			String type = fields[3];
			if (fields[4].length() < 1) {
				fields[4] = "0";
			}
			double latitude = Double.parseDouble(fields[4]);
			if (fields[5].length() < 1) {
				fields[5] = "0";
			}
			double longitude = Double.parseDouble(fields[5]);

			String parentSightId = fields[6];

			Sight sight;
			if (idHash.containsKey(id)) {
				sight = idHash.get(id);
			} else {
				sight = new Sight(id, name, type, latitude, longitude, parentSightId);
				idHash.put(id, sight);
			}

			if (aliasHash.containsKey(alias)) {
				aliasHash.get(alias).add(sight);
			} else {
				List<Sight> sights = new ArrayList<Sight>();
				sights.add(sight);
				aliasHash.put(alias, sights);
			}
		}
		br.close();

		// build up sight tree
		Sight.buildUpSightTree(idHash);

		// build up sight dictionary
		Iterator it = aliasHash.keySet().iterator();
		while (it.hasNext()) {
			String alias = (String) it.next();

			for (int i = 0; i < aliasHash.get(alias).size(); i++) {
				sightDict.addEntry(new DictionaryEntry(alias, aliasHash.get(alias).get(i)));
			}

			dictionary.addEntry(new DictionaryEntry<String>(alias, alias, 1.0));
		}

		dictChunker = new ExactDictionaryChunker(dictionary, CharacterTokenizerFactory.INSTANCE, false, true);

		sights = idHash;
	}

	public SightDictionary(String sightDictFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(SightDictionary.class.getClassLoader().getResourceAsStream(sightDictFile), "utf8"));
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\e35ws\\qunarspider\\deals\\WEB-INF\\classes\\data\\sight.list"), "utf8"));
		HashMap<String, List<Sight>> aliasHash = new HashMap<String, List<Sight>>();
		HashMap<String, Sight> idHash = new HashMap<String, Sight>();

		int lineNum = 0;
		String line;
		while ((line = br.readLine()) != null) {
			lineNum++;
			if (line.length() == 0) {
				continue;
			}
			String[] fields = line.split("\\s*,\\s*", -1);
			// alias,id,name,type,latitude,longitude,parentSightId

			String alias = fields[0];
			if (fields.length < 7 || alias.length() < 1) {
				logger.debug("wrong format found in sight dict file: " + sightDictFile + " line number: " + lineNum);
				continue;
			}

			String id = fields[1];
			String name = fields[2];
			String type = fields[3];
			if (fields[4].length() < 1) {
				fields[4] = "0";
			}
			double latitude = Double.parseDouble(fields[4]);
			if (fields[5].length() < 1) {
				fields[5] = "0";
			}
			double longitude = Double.parseDouble(fields[5]);

			String parentSightId = fields[6];

			Sight sight;
			if (idHash.containsKey(id)) {
				sight = idHash.get(id);
			} else {
				sight = new Sight(id, name, type, latitude, longitude, parentSightId);
				idHash.put(id, sight);
			}

			if (aliasHash.containsKey(alias)) {
				aliasHash.get(alias).add(sight);
			} else {
				List<Sight> sights = new ArrayList<Sight>();
				sights.add(sight);
				aliasHash.put(alias, sights);
			}
		}
		br.close();

		// build up sight tree
		Sight.buildUpSightTree(idHash);

		// build up sight dictionary
		Iterator it = aliasHash.keySet().iterator();
		while (it.hasNext()) {
			String alias = (String) it.next();

			for (int i = 0; i < aliasHash.get(alias).size(); i++) {
				sightDict.addEntry(new DictionaryEntry(alias, aliasHash.get(alias).get(i)));
			}

			dictionary.addEntry(new DictionaryEntry<String>(alias, alias, 1.0));
		}

		dictChunker = new ExactDictionaryChunker(dictionary, CharacterTokenizerFactory.INSTANCE, false, true);

	}

	/**
	 * Extracts option sights from day trip's description by dict
	 * 
	 * @param content
	 * @return
	 */
	public List<OptionSight> extractOptionSights(String content) {
		List<OptionSight> optionSights = new ArrayList<OptionSight>();

		//TODO
		Chunking chunking = dictChunker.chunk(content);
		for (Chunk chunk : chunking.chunkSet()) {
			int start = chunk.start();
			int end = chunk.end();
			String type = chunk.type();
			double score = chunk.score();

			String aliasName = content.substring(chunk.start(), chunk.end());

			boolean isToRegion = false;
			if (ItinerayExtractor.toCityPattern.matcher(content.substring(0, chunk.start())).find()) {
				isToRegion = true;
			}
			aliasName = aliasName.replaceAll("\\s+", "");
			//			TODO
			List<DictionaryEntry<Sight>> entryList = sightDict.phraseEntryList(aliasName);

			List<Sight> sights = new ArrayList<Sight>();
			for (int i = 0; i < entryList.size(); i++) {
				sights.add(entryList.get(i).category());
			}

			OptionSight optionSight = new OptionSight(start, end, aliasName, sights, isToRegion);
			optionSights.add(optionSight);

			logger.debug("aliasName=|" + aliasName + "|" + " start=" + start + " end=" + end + " type=" + type + "score=" + score);
		}

		return optionSights;
	}

	public List<OptionSight> extractOptionSights(String content, RouteContext context) {
		List<OptionSight> optionSights = new ArrayList<OptionSight>();

		Chunking chunking = dictChunker.chunk(content);
		for (Chunk chunk : chunking.chunkSet()) {
			int start = chunk.start();
			int end = chunk.end();
			String type = chunk.type();
			double score = chunk.score();

			String aliasName = content.substring(chunk.start(), chunk.end());

			boolean isToRegion = false;
			if (ItinerayExtractor.toCityPattern.matcher(content.substring(0, chunk.start())).find()) {
				isToRegion = true;
			}
			aliasName = aliasName.replaceAll("\\s+", "");
			List<DictionaryEntry<Sight>> entryList = sightDict.phraseEntryList(aliasName);

			List<Sight> sights = new ArrayList<Sight>();
			for (int i = 0; i < entryList.size(); i++) {
				Sight s = entryList.get(i).category();
				//知道景点区域后，增加的条件
				if (!ItinerayExtractor.isInRegion(s, context)) {
					continue;
				}
				sights.add(s);

			}

			OptionSight optionSight = new OptionSight(start, end, aliasName, sights, isToRegion);
			optionSights.add(optionSight);

			logger.debug("aliasName=|" + aliasName + "|" + " start=" + start + " end=" + end + " type=" + type + "score=" + score);
		}

		return optionSights;
	}

	public Sight getFirstSightByName(String name) {
		if (name == null) {
			return null;
		}
		List<DictionaryEntry<Sight>> entryList = sightDict.phraseEntryList(name);
		if (entryList != null && entryList.size() > 0) {
			return entryList.get(0).category();
		} else {
			return null;
		}

	}

	public int getSightCountByName(String name) {
		if (name == null) {
			return 0;
		}
		List<DictionaryEntry<Sight>> entryList = sightDict.phraseEntryList(name);
		if (entryList != null) {
			return entryList.size();
		} else {
			return 0;
		}

	}

	public static void main(String[] args) {
		String s = "中国(天津) 韩国(济州) 日本(福冈) ";
		StringBuilder sb = new StringBuilder(s);
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		List<OptionSight> sights = itinerayExtractor.dict.extractOptionSights(s);
		for (int i = sights.size() - 1; i >= 0; i--) {
			OptionSight os = sights.get(i);
			if (os.getOptionSights().size() == 1) {
				Sight ss = os.getOptionSights().get(0);
				String t = "";
				if ("景区".equals(ss.getType()) || "景点".equals(ss.getType())) {
					t = "2";
				}
				if ("国家".equals(ss.getType()) || "省份".equals(ss.getType())) {
					t = "0";
				}
				if ("城市".equals(ss.getType())) {
					t = "1";
				}
				if (t.isEmpty()) continue;
				String p = t + "_" + ss.getId() + "_";
				sb.insert(os.getStartPos(), p);
			}
		}
		int i = 0;
		//		itinerayExtractor.e
	}
}
