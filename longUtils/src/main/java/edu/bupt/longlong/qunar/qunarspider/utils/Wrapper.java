package edu.bupt.longlong.qunar.qunarspider.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.bupt.longlong.utils.StringUtil;

/**
 * @description 常用的对wrapper处理的工具方法
 * @author oulong
 */
public class Wrapper {

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * qunar三个日期的处理规则 检查：必须抓取到，若抓取到是否正确（规则如下） "
	 * 1、如果只有1个出发时间而没有结束时间，则出发、结束时间都是该时间；
	 * 2、否则，如果有多于1个的出发时间，则出发时间是日期中最小的，结束时间是日期中最大的；
	 * 3、否则，如果没有出发和结束时间，则出发时间取Wrapper运行当天时间，结束时间为出发时间往后推三个月；
	 * 4、否则，如果只有1个结束时间而没有出发时间，则出发时间取Wrapper运行当天的时间，结束时间为抓取到的时间。
	 * 
	 * @decription 获得出发与结束日期
	 */
	String[] headAndTailOfTravel(String[] headAndTail) {
		Date dateOfDeparture = null, dateOfExpire = null;
		try {
			Date tDate = format.parse(headAndTail[0]);
			dateOfDeparture = tDate;
			tDate = format.parse(headAndTail[1]);
			dateOfExpire = tDate;
		} catch (Exception e) {
			System.out.println("Date parse error.");
		}

		Date today = new Date();
		if (dateOfDeparture != null) {
			headAndTail[0] = format.format(dateOfDeparture);
		} else {
			headAndTail[0] = format.format(today);
		}
		if (dateOfExpire != null) {
			headAndTail[1] = format.format(dateOfExpire);
		} else {
			if (dateOfDeparture == null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				cal.add(Calendar.MONTH, 3);
				dateOfExpire = cal.getTime();
				headAndTail[1] = format.format(cal.getTime());
			} else {
				dateOfExpire = dateOfDeparture;
				headAndTail[1] = format.format(dateOfDeparture);
			}
		}

		return headAndTail;
	}

	/**
	 * @decription 把日期字符串分隔成符合DateFormat约定的日期单元
	 */
	List<String> getDateUnits(String stringOfDate) {
		List<String> units = new ArrayList<String>();
		stringOfDate = StringUtil.trim(stringOfDate);
		stringOfDate = stringOfDate.replaceAll("开班$", "");
		stringOfDate = stringOfDate.replaceAll("日", "");
		boolean mark = false;
		if (stringOfDate.contains("，") && stringOfDate.contains("、") && !mark) {
			mark = true;
			units = dateDivided(stringOfDate, "，", "、");
		}

		if (stringOfDate.contains("，") && !stringOfDate.contains(".") && !stringOfDate.contains("、") && !mark) {
			mark = true;
			units = dateDivided(stringOfDate, "，", null);
		}

		if (stringOfDate.contains("、") && !stringOfDate.contains("，") && !mark) {
			mark = true;
			units = dateDivided(stringOfDate, "、", null);
		}

		if (!mark) {
			units.add(stringOfDate);
		}
		return units;
	}

	/**
	 * @param uppersign
	 *            一级日期字符串分割词
	 * @param sign
	 *            次级分割词
	 */
	private List<String> dateDivided(String stringOfDate, String uppersign, String sign) {
		List<String> units = new ArrayList<String>();
		String[] temp = stringOfDate.split(uppersign);
		for (int i = 0; i < temp.length; i++) {
			temp[i] = StringUtil.trim(temp[i]);
			if (sign != null) {
				String[] tempLeap = temp[i].split(sign);
				units.addAll(Arrays.asList(tempLeap));
			} else if (!temp[i].isEmpty()) {
				units.add(temp[i]);
			}
		}
		return units;
	}

	/**
	 * @description 获取给定月的头尾日期串(取的是当时的年份)
	 */
	void monthRange(List<String> units, int month) {
		Calendar cal = Calendar.getInstance();
		try {
			Date date = format.parse(cal.get(Calendar.YEAR) + "-" + month + "-" + "01");
			cal.setTime(date);
		} catch (ParseException e) {
			System.out.println("Date parse error.");
		}
		units.add(cal.get(Calendar.YEAR) + "-" + month + "-" + "01");
		units.add(cal.get(Calendar.YEAR) + "-" + month + "-" + cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	}

	/**
	 *@description target：辨别是否是有意义的日期字符串
	 */
//	boolean is
}
