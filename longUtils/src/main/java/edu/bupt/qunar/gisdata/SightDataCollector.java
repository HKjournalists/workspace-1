/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.qunar.gisdata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.qunar.database.DataSet;
import edu.bupt.qunar.database.DbOperator;

/**
 *
 * @author oulong
 */
public class SightDataCollector {

    public static final String DomesticCitys = "type = '城市' and country = '中国'";
    public static final String DomesticProvinces = "type = '省份' and country = '中国'";
    public static final String SightArea = "type = '景区'";
    public static final String ChildSight = "type = '景点'";
    public static final String OutboundArea = "type = '景区' and country !='中国'";
    public static final String ForeignCitys = "type = '城市' and country !='中国'";
    public static final String OtherCountrys = "type = '国家' and name != '中国'";

    public static void rateInDatabase(String option) {
        DbOperator.init("db.xml");
//        List<String> result = new ArrayList<String>();

        String sql = "select count(*) from Sight where " + option;
        String[][] row1 = DataSet.query("sight213backend", sql);
        int sum = Integer.parseInt(row1[0][0]);

        for (String name : sugumentName) {
            sql = "select count(*) from Sight where " + name + " is not null and char_length(" + name + ") > 0 and " + option;
            String[][] row2 = DataSet.query("sight213backend", sql);
            int num = Integer.parseInt(row2[0][0]);
            double d = num;

            //各表基数不同
            //国内城市 372
            BigDecimal bd = new BigDecimal(d / sum * 100);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.println(name + "\t" + num + "(" + bd.toString() + "%)");
//            result.add(StringUtil.divideBySignal(new String[]{name, row[0][0], "25523", bd.toString() + "%"}));
        }

//        IoTool.writeMultiRows(filepath, result, true);
    }

    public static void main(String[] args) {
        rateInDatabase(OtherCountrys);
    }
    public static final String filepath = "H:/Java work/rate.txt";
    public static final List<String> sugumentName = new ArrayList<String>();

    static {
        sugumentName.add("name");
        sugumentName.add("pinyinName");
        sugumentName.add("englishName");
//        sugumentName.add("star");
        sugumentName.add("aliases");
//        sugumentName.add("address");
        sugumentName.add("introduction");
//        sugumentName.add("fare");
//        sugumentName.add("openTime");
        sugumentName.add("latitude");
//        sugumentName.add("phoneNumber");
        sugumentName.add("bestTime");
        sugumentName.add("aroundSights");
//        sugumentName.add("aroundCitys");
//        sugumentName.add("city");
//        sugumentName.add("comment");
//        sugumentName.add("company");
//        sugumentName.add("country");
        sugumentName.add("history");
        sugumentName.add("culture");
        sugumentName.add("traffic");
        sugumentName.add("trafficBus");
        sugumentName.add("trafficPlane");
        sugumentName.add("trafficTrain");
        sugumentName.add("trafficCar");
        sugumentName.add("trafficTaxi");
        sugumentName.add("trafficOther");
        sugumentName.add("food");
        sugumentName.add("folkCultureFestivals");
        sugumentName.add("resideDesc");
        sugumentName.add("shopping");
        sugumentName.add("speciality");
        sugumentName.add("noNoCulture");
        sugumentName.add("attention");
        sugumentName.add("specialTravelAdvice");
        sugumentName.add("interestingPoint");
        sugumentName.add("travelAgencies");

        sugumentName.add("localEmbassy");
        sugumentName.add("population");
        sugumentName.add("language");
        sugumentName.add("currency");
        sugumentName.add("currencyExchangeRate");
        sugumentName.add("power");
        sugumentName.add("visa");
//        sugumentName.add("timezone");
//        sugumentName.add("logoImageUrl");
//        sugumentName.add("longitude");
//        sugumentName.add("province");

//        sugumentName.add("type");
//        sugumentName.add("website");
//        sugumentName.add("parentSight_id");






    }
}
