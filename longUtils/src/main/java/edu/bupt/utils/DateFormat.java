package edu.bupt.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理不同形式的日期（年月日）字符串，以获得标准日期输出（如2010-08-08）
 * @author houming.wang & oulong
 */
public class DateFormat {

    private static Date getFirstDate(String dateString, String[] yyyyMMddRegexes, String[] MMddRegexes, String[] ddRegexes) {
        Calendar today = Calendar.getInstance();
        String[] matchedRegexes = null;
        String reg = null;
        for (String regex : yyyyMMddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = yyyyMMddRegexes;
                reg = regex;
                break;
            }
        }
        for (String regex : MMddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = MMddRegexes;
                reg = regex;
                break;
            }
        }
        for (String regex : ddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = ddRegexes;
                reg = regex;
                break;
            }
        }
        if (matchedRegexes == null) {
            return null;
        }
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DATE);
        if (matchedRegexes == yyyyMMddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            year = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2)) - 1;
            day = Integer.parseInt(m.group(3));
        } else if (matchedRegexes == MMddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            month = Integer.parseInt(m.group(1)) - 1;
            day = Integer.parseInt(m.group(2));
        } else if (matchedRegexes == ddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            day = Integer.parseInt(m.group(1));
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        return c.getTime();
    }

    private static Date getDate(String dateString, String[] yyyyMMddRegexes, String[] MMddRegexes, String[] ddRegexes, Calendar last) {
        String[] matchedRegexes = null;
        String reg = null;
        for (String regex : yyyyMMddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = yyyyMMddRegexes;
                reg = regex;
                break;
            }
        }
        for (String regex : MMddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = MMddRegexes;
                reg = regex;
                break;
            }
        }
        for (String regex : ddRegexes) {
            if (dateString.matches(regex)) {
                matchedRegexes = ddRegexes;
                reg = regex;
                break;
            }
        }
        if (matchedRegexes == null) {
            return null;
        }
        int year = last.get(Calendar.YEAR);
        int month = last.get(Calendar.MONTH);
        int day = last.get(Calendar.DATE);
        int newyear = -1, newmonth = -1, newday = -1;
        if (matchedRegexes == yyyyMMddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            newyear = Integer.parseInt(m.group(1));
            newmonth = Integer.parseInt(m.group(2)) - 1;
            newday = Integer.parseInt(m.group(3));
        } else if (matchedRegexes == MMddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            newmonth = Integer.parseInt(m.group(1)) - 1;
            newday = Integer.parseInt(m.group(2));
        } else if (matchedRegexes == ddRegexes) {
            Matcher m = Pattern.compile(reg).matcher(dateString);
            m.find();
            newday = Integer.parseInt(m.group(1));
        }
        Calendar c = Calendar.getInstance();

        if (newyear == -1 && newmonth != -1) {
            // if (newmonth < month) {
            // newyear = year + 1;
            // } else {
            newyear = year;
            // }
        }
        if (newyear == -1 && newmonth == -1 && newday != -1) {
            if (newday < day) {
                newmonth = month + 1;
                newyear = year;
                if (newmonth == 12) {
                    newmonth = 0;
                    newyear = year + 1;
                }
            } else {
                newmonth = month;
                newyear = year;
            }
        }
        c.set(Calendar.YEAR, newyear);
        c.set(Calendar.MONTH, newmonth);
        c.set(Calendar.DATE, newday);
        return c.getTime();
    }

    public static SortedSet<String> SortedDateSet(List<String> dates) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] yyyyMMddRegexes = new String[]{"(\\d{1,4})\\-(\\d{1,2})\\-(\\d{1,2})",
            "(\\d{1,4})年(\\d{1,2})月(\\d{1,2})日", "(\\d{1,4})年(\\d{1,2})月(\\d{1,2})",
            "(\\d{1,4})\\.(\\d{1,2})月(\\d{1,2})日", "(\\d{1,4})\\.(\\d{1,2})\\.(\\d{1,2})",
            "(\\d{1,4})/(\\d{1,2})/(\\d{1,2})"};

        String[] MMddRegexes = new String[]{"(\\d{1,2})\\-(\\d{1,2})",
            "(\\d{1,2})月(\\d{1,2})日", "(\\d{1,2})月(\\d{1,2})",
            "(\\d{1,2}).(\\d{1,2})", "(\\d{1,2})月(\\d{1,2})号",
            "(\\d{1,2})/(\\d{1,2})"};

        String[] ddRegexes = new String[]{"(\\d{1,2})日", "(\\d{1,2})", "(\\d{1,2})号"};

        Calendar calendar = Calendar.getInstance();
        Date firstDate = null, lastDate = null;
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).trim().isEmpty()) {
                continue;
            }
            if (i == 0) {
                firstDate = getFirstDate(dates.get(i), yyyyMMddRegexes, MMddRegexes, ddRegexes);
                lastDate = firstDate;
                result.add(sdf.format(firstDate));
            } else {
                calendar.setTime(lastDate);
                Date tDate = getDate(dates.get(i), yyyyMMddRegexes, MMddRegexes, ddRegexes, calendar);
                lastDate = tDate;
                result.add(sdf.format(tDate));
            }
        }

        SortedSet<String> dateSorter = new TreeSet<String>();
        for (String s : result) {
            dateSorter.add(s);
        }

        return dateSorter;
    }
}
