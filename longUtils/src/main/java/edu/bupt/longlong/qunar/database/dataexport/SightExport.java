package edu.bupt.longlong.qunar.database.dataexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.longlong.qunar.database.DataSet;
import edu.bupt.longlong.qunar.database.DbOperator;
import edu.bupt.longlong.utils.IoTool;
import edu.bupt.longlong.utils.StringUtil;
import edu.bupt.longlong.utils.thread.FunctionThreadPool;


public class SightExport {

	//一般都是在大量输入数据的情况下，导出每条数据集合中满足要求的特定数据集
	//产品条件+景点库匹配--->不存在的留下
	//标点开始，（...）结束，括号中有数字:各种括号【】〖〗

	public static List<String> sqls = new ArrayList<String>();
	public static String sql_for_dataexport = "D:/qunarspider/potato/sql for data exporting.txt";

	final static String sightsql = "select name from Sight group by name";
	static String[][] sightrow = null;
	static {
		DbOperator.init("db.xml");
		sqls = IoTool.readMoreRows(sql_for_dataexport, "gbk");
		System.out.println("Initial done.");
	}

	private static final String selectedPartPatternStr = "[\\.,，。？\\?]([\u4e00-\u9fa5【〖】〗]+?)[(（].*?\\d+.*?[）)]";
	private static final Pattern selectedPartPattern = Pattern.compile(selectedPartPatternStr);

	/**
	 * @处理框架 sql写到txt中顺序排列，注释掉行前用#，这样的一个处理框架
	 */
	public static void baseStructure() {
		List<String> resultList = new ArrayList<String>();
		String[][] row = null;
		String line = null;
		for (String sql : sqls) {
			if (sql.contains("#") || sql.isEmpty()) continue;
			row = DataSet.query("bg2_vacation", sql);
			if (row != null && row.length > 0) {
				for (int i = 0; i < row.length; i++) {
					/**
					 * 处理逻辑部分
					 */
					line = selectedWords(row[i][1]);
					if (line != null) {
						String[][] rownew = DataSet.query("bg2_vacation", "select title from route where id = " + row[i][0]);
						resultList.add(StringUtil.divideBySignal(new String[] { row[i][0], rownew[0][0], line }));
					}
				}
			}
		}

		//导入线程池
		FunctionThreadPool pool = new FunctionThreadPool(resultList);
		pool.execute();
	}

	private static String selectedWords(String content) {
		Matcher matcher = selectedPartPattern.matcher(content);
		while (matcher.find()) {
			if (!matcher.group(1).contains("【") && !matcher.group(1).contains("〖")) continue;
			System.out.println(matcher.group());
			System.out.println(matcher.group(1));
			System.out.println("========");
			IoTool.write("D:/qunarspider/result/selectMiddle.txt", matcher.group() + "\n" + matcher.group(1) + "\n" + "========" + "\r\n", true);
			return matcher.group(1);
		}
		return null;
	}

	public static void selectFromSight(String unit) {
		String[] temp = unit.split("\\|");
		if (temp.length < 3) return;
		sightrow = DataSet.query("bg2_sight", sightsql);
		for (int i = 0; i < sightrow.length; i++) {
			if (temp[2].contains(sightrow[i][0]))
				break;
			else {
				if (i == sightrow.length - 1) {
					System.out.println(unit);
					IoTool.write("D:/qunarspider/result/selectEnd.txt", unit + "\n", true);
				}
			}
		}
	}

	public static void compareWithWhole(Map<String, Integer> map) {
		String[][] row = null;
		String sql = "select id , title from route where departure like '%北京%' and arrive like '%三亚%'";
		row = DataSet.query("bg2_vacation", sql);
		for (int i = 0; i < row.length; i++) {
			if (map.containsKey(row[i][0]))
				continue;
			else {
				System.out.println(row[i][0] + "\t" + row[i][1]);
			}
		}
	}

	public static Map<String, Integer> routeInSet() {
		String[][] row = null;
		Map<String, Integer> idMap = new HashMap<String, Integer>();
		for (String sql : sqls) {
			if (sql.contains("#") || sql.isEmpty()) continue;
			row = DataSet.query("bg2_vacation", sql);
			if (row != null && row.length > 0) {
				for (int i = 0; i < row.length; i++) {
					if (idMap.containsKey(row[i][0]))
						idMap.put(row[i][0], 2);
					else {
						idMap.put(row[i][0], 1);
					}
				}
			}
		}
		System.out.println(idMap.size());
		int num = 0;
		for (String key : idMap.keySet()) {
			if (idMap.get(key) == 2) num++;
		}
		System.out.println(num);
		return idMap;
	}

	public static void main(String[] args) {
		//		routeInSet();
		//		compareWithWhole(routeInSet());
		baseStructure();
		//		selectFromSight("328385|西安+临潼+黄帝陵+延安+壶口瀑布三星双飞5日游团队游（包团行程）|【杨家岭】");
	}
}
