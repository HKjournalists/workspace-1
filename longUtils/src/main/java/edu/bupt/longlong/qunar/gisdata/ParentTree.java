package edu.bupt.longlong.qunar.gisdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.bupt.longlong.qunar.database.DataSet;
import edu.bupt.longlong.qunar.database.DbOperator;
import edu.bupt.longlong.utils.StringUtil;
import edu.bupt.longlong.utils.thread.FunctionThreadPool;


/**
 * 
 * @author oulong
 *
 */
public class ParentTree {

    private static Logger logger = Logger.getLogger(ParentTree.class);
    private final static Map<String, String> argumentsMap = new HashMap<String, String>();
    private final static String databaseName = "sight213backend";

    static {
        argumentsMap.put("城市", " city = ?,");
        argumentsMap.put("省份", " province = ?,");
        argumentsMap.put("国家", " country = ?,");
        argumentsMap.put("大洲", " continent = ?,");
    }

    // 更新city、province、country、continent
    public static void updateArguments() {
        DbOperator.init("db.xml");

        String[][] rows = null;
        String sql = "select id, parentSight_id from Sight where parentSight_id is not null";
//                String sql = "select id, parentSight_id from Sight where name = '绿渊潭'";
        rows = DataSet.query(databaseName, sql, 0, 0);

        List<String> idList = new ArrayList<String>();
        List<String> parentIdList = new ArrayList<String>();

        for (int index = 0; index < rows.length; index++) {
            idList.add(rows[index][0]);
            parentIdList.add(rows[index][1]);
        }

        FunctionThreadPool pool = new FunctionThreadPool(idList);
        pool.execute();
    }

    public static void updateElement(String id, String parentSight_id) {
        String sql;
        String[][] fatherRows = null;
        String middle = null;
        StringBuilder builder = new StringBuilder();
        List<String> list = new ArrayList<String>();
        String bubu = parentSight_id;
        while (bubu != null) {
            sql = "select name, type, parentSight_id from Sight where id = "
                    + bubu;
            fatherRows = DataSet.query(databaseName, sql, 0, 0);
            middle = midMix(fatherRows[0][0], fatherRows[0][1], list);
            builder.append(middle);
            bubu = fatherRows[0][2];
        }
        if (builder.length() < 1) {
            return;
        }

        //city,province,country,continent
        String tmp = builder.toString();
        if(!tmp.contains("city"))builder.append(" city = null,");
        if(!tmp.contains("province"))builder.append(" province = null,");
        if(!tmp.contains("country"))builder.append(" country = null,");
        if(!tmp.contains("continent"))builder.append(" continent = null,");

        builder.deleteCharAt(builder.length() - 1);
        sql = "update Sight set " + builder.toString() + " where  id = "
                + id;
        String[] temp = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = list.get(i);
        }
        try {
            DataSet.update(databaseName, sql, temp);
        } catch (Exception e) {
            logger.error("更新数据库出错：出错景点id\t" + id);
        }
    }

    private static String midMix(String name, String type, List<String> list) {
        StringBuilder result = new StringBuilder();
        if (argumentsMap.keySet().contains(type)) {
            result.append(argumentsMap.get(type));
            list.add(name);
        }
        return result.toString();
    }

    //更新164
    public static void updatePg() {
        DbOperator.init("db.xml");

        String[][] rows = null;
        String sql = "select addr,gid from poi_item where status = 0 ";
//                String sql = "select id, parentSight_id from Sight where name = '绿渊潭'";
        rows = DataSet.query(pgDatabaseName, sql, 0, 0);

        List<String> idList = new ArrayList<String>();
        List<String> updateList = new ArrayList<String>();

        for (int index = 0; index < rows.length; index++) {
            if (rows[index][0].isEmpty()) {
                continue;
            }
            String[] temp = rows[index][0].split(",");
            if (temp.length == 2) {
                idList.add(StringUtil.trim(temp[1]));
                updateList.add(StringUtil.trim(rows[index][1]));
            }
        }

        FunctionThreadPool pool = new FunctionThreadPool(idList);
        pool.execute();
    }

    public static void unityPg(String id, String updateId) {
        String[][] rows = null;
        String sql = "select city from Sight where id = ?";
        rows = DataSet.query(databaseName, sql, new String[]{id});
        if (rows != null) {
            sql = "update poi_item set city_name = ? , addr = ? where gid = " + Integer.parseInt(updateId);
            try {
                DataSet.update(pgDatabaseName, sql, new String[]{rows[0][0], rows[0][0] + "," + id});
            } catch (Exception e) {
                logger.error("更新数据库出错：出错景点id\t" + id);
            }
        }
    }
    public static String pgDatabaseName = "pggis";

    public static void main(String[] args) {
        ParentTree.updateArguments();
//        ParentTree.updatePg();
    }
}
