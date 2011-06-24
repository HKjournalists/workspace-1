package edu.bupt.qunar.gis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.bupt.qunar.database.DataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class POIService {

    private static Logger log = Logger.getLogger(POIService.class.getName());
    private static final String ipPath = "H:/Java work/here!!!useful/ipList.txt";
    private static final String gisPath = "H:/Java work/testEncoding.txt";
    private static final String resultPath = "H:/Java work/result.txt";

    public static String getResponse(String url) {
        String resp = "";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);

        List<String> hostAndPort = readUrlsTxt(ipPath);
        int index = (int) (System.currentTimeMillis() % hostAndPort.size());

        String host = "";
        String port = "";
        String[] field = hostAndPort.get(index).split(":");
        if (field.length == 2) {
            host = field[0];
            port = field[1];
        } else {
            throw new RuntimeException("No porxy!");
        }
        client.getHostConfiguration().setProxy(host, Integer.parseInt(port));

        GetMethod get = null;
        try {
            get = new GetMethod(url);
            int status = client.executeMethod(get);
            if (status == 200) {
                resp = get.getResponseBodyAsString();
            } else {
//                log.warn("http status wrong:" + status);
            }
        } catch (Exception e) {
//            log.warn("access error for url:" + url, e);
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }

        return resp;
    }

    private synchronized static List<String> readUrlsTxt(String path) {
        BufferedReader bufReader = null;
        List<String> urlList = new ArrayList<String>();
        try {
            bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "gbk"));
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                if (line == null || line.isEmpty()) {
                    continue;
                }
                urlList.add(line);
            }

            return urlList;
        } catch (IOException e) {
            return urlList;
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param host
     *            maps.google.com or ditu.google.cn
     * @param query
     * @param city
     * @return PNode
     */
    public static PNode doSearch(String host, String query, String city) {
        try {
            int i = 0;
            for (String url : buildURL(query, city)) {
                i++;

                ArrayList<MarkerItem> list = new ArrayList<MarkerItem>();
                String content = getResponse("http://" + host + url);
                parsePOI(content, list);

                Sortter<MarkerItem> sor = new Sortter<MarkerItem>();
                for (MarkerItem mi : list) {
                    if (!(i == 1 && (mi.name.indexOf(city) == -1 && mi.addr.indexOf(city) == -1))) {
                        sor.add(distance(query, mi.name), mi);
                    }
                }
                sor.sort();
                MarkerItem rp = sor.getFirst();
                if (rp != null) {
                    return new PNode(rp.name, rp.addr, rp.lat, rp.lng);
                }
            }

        } catch (Exception e) {
            log.warn("查询地标出错:" + query + ";" + city, e);
        }
        return null;
    }

    private static int minimum(int a, int b, int c) {
        int x = (a <= b) ? a : b;
        return ((x <= c) ? x : c);
    }

    private static int distance(CharSequence str1, CharSequence str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] distance = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; ++i) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= len2; ++j) {
            distance[0][j] = j;
        }
        for (int i = 1; i <= len1; ++i) {
            int v = len1 + 1 - i;
            for (int j = 1; j <= len2; ++j) {
                distance[i][j] = minimum(
                        distance[(i - 1)][j] + 1,
                        distance[i][(j - 1)] + 1,
                        distance[(i - 1)][(j - 1)]
                        + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? -v
                        : v));
            }
        }

        return distance[len1][len2];
    }

    private static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String[] buildURL(String q, String city) {
        if (city != null && !city.trim().isEmpty()) {
            return new String[]{
                        "/maps?f=q&source=s_q&output=js&hl=zh-CN&ie=UTF8&oe=UTF8&q="
                        + encodeURL(q) + "&g=" + encodeURL(city)
//                                ,
//                        "/maps?f=q&source=s_q&output=js&hl=zh-CN&ie=UTF8&oe=UTF8&q="
//                        + encodeURL(new StringBuilder().append(q).append(
//                        ", ").append(city).toString())
                    };
        }
        return new String[]{"/maps?f=q&source=s_q&output=js&hl=zh-CN&ie=UTF8&oe=UTF8&q="
                    + encodeURL(q)};
    }

    private static JsonElement getNode(JsonObject obj, String path) {
        JsonElement node = obj;
        for (String p : path.split("/")) {
            node = node.getAsJsonObject().get(p);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    public static String getString(JsonElement[] args) {
        for (int i = 0; i < args.length - 1; ++i) {
            if ((args[i] != null) && (!(args[i].getAsString().equals("")))) {
                return args[i].getAsString();
            }
        }
        return ((args[(args.length - 1)] == null) ? null
                : args[(args.length - 1)].getAsString());
    }

    public static void parsePOI(String content, Collection<MarkerItem> list) {
        try {
            content = content.substring(content.indexOf("w.loadVPage(")
                    + "w.loadVPage(".length(), content.lastIndexOf(",\"state\")"));
        } catch (Exception e) {
//            log.warn("parse poi fail for content:" + content, e);
            return;
        }
        content = content.replaceAll("\\\\x", "\\\\u00");

        JsonObject root = (JsonObject) new JsonParser().parse(content);
        JsonArray ja = (JsonArray) getNode(root, "overlays/markers");

        if (ja == null) {
            return;
        }
        for (JsonElement je : ja) {
            JsonObject marker = je.getAsJsonObject();
            String id = marker.get("id").getAsString();

            if ("ABCDEFGHIJKLMNOPQRST".indexOf(id) == -1) {
//                log.info("忽略广告:" + id);
            }

            String title = getString(new JsonElement[]{marker.get("name"),
                        marker.get("sxti"), null});
            if (title == null) {
                title = getString(new JsonElement[]{marker.get("title"), null});
                if (title != null) {
                    title = title.replaceAll("<[a-zA-z/][^>]*>", "").trim();
                }
            }

            String laddr = getString(new JsonElement[]{marker.get("laddr"),
                        null});
            String addr = null;
            if (laddr != null) {
                if ((title != null) && (laddr.endsWith(title + ")"))) {
                    addr = laddr.substring(0, laddr.length() - title.length()
                            - 3);
                } else {
                    addr = laddr;
                }
            }
            if ((title == null) || (title.length() == 0)) {
                title = addr;
            }

            double lat = 0.0D;
            double lng = 0.0D;
            try {
                String lt = getString(new JsonElement[]{
                            getNode(marker, "latlng/lat"), null});
                String lg = getString(new JsonElement[]{
                            getNode(marker, "latlng/lng"), null});
                lat = Double.parseDouble(lt);
                lng = Double.parseDouble(lg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((lat == 0.0D) || (lng == 0.0D)) {
                String line = getString(new JsonElement[]{getNode(marker,
                            "latlng/alt/ll")});

                LatLng ll = GMercatorProjection.fromLine(line);
                lat = ll.lat;
                lng = ll.lng;
            }

            list.add(new MarkerItem(title, addr, lat, lng));
        }
    }

    //gis获取要求数据
    public static void main(String[] args) throws Exception {

        PNode pNode = doSearch("maps.google.com", "上海远程老年大学", "上海");
        System.out.println(pNode.lat);
        System.out.println(pNode.lng);
        System.out.println(pNode.name);
        System.out.println(pNode.addr);

//        doSearch("ditu.google.cn", "西湖", "西湖区");
//        System.out.println(pNode.lat);
//        System.out.println(pNode.lng);
//        System.out.println(pNode.name);
//        System.out.println(pNode.addr);
    }

    public synchronized static void appendString(String path, String content) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            raf.seek(raf.length());
            raf.write((content + "\r\n").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void importSightPoi() {
        int batchNum = 50;
        int startIndex = 0;
        while (true) {
            String id = "-1";

            try {
                String tmpSql = "select id, name,city,province from Sight where city is not null limit "
                        + startIndex + "," + batchNum;
                String[][] result = DataSet.query("sightBackend", tmpSql);
                if (result.length == 0) {
                    break;
                }

                for (String[] item : result) {
                    id = item[0];
                    String name = item[1];
                    String cityName = item[2];
                    String province = item[3];

                    result = DataSet.query(
                            "sightPoi",
                            "select name from poi_item where (name = ? and (addr is null  or addr = ?))",
                            new String[]{name, province + "," + id});
                    if (result.length == 1) {
                        System.out.println("has exists:" + id + "," + name);
                        continue;
                    }

                    PNode pNode = doSearch("maps.google.com", name, cityName);
                    if (pNode == null) {
                        System.out.println("can't get poi:" + id + "," + name);
                        continue;
                    }

                    String lng = Double.toString(pNode.lng);
                    String lat = Double.toString(pNode.lat);

                    StringBuilder sql = new StringBuilder();
                    sql.append(
                            "INSERT INTO poi_item(city_name, name, addr, status, latlng) ").append(
                            "  VALUES(?, ?, ?, 0, ST_GeographyFromText(?))");

                    boolean success = DataSet.update("sightPoi", sql.toString(),
                            new String[]{
                                cityName,
                                name,
                                province + "," + id,
                                "SRID=4326;POINT(" + lng + " "
                                + lat + ")"});

                    if (success) {
                        int newGid = 0;
                        String[][] row = DataSet.query(
                                "sightPoi",
                                "SELECT gid FROM poi_item WHERE city_name=? AND name=? AND addr=? AND type=1 LIMIT 1",
                                new String[]{cityName, name,
                                    province + "," + id});
                        if (row != null && row.length == 1) {
                            newGid = Integer.valueOf(row[0][0]);
                        }
                        if (newGid <= 0) {
                            throw new Exception(
                                    "Insert item error, can not get gid.");
                        }

                        StringBuilder sqlManager = new StringBuilder();
                        sqlManager.append(
                                "INSERT INTO poi_item_manager(gid, createdby, lastmodifyby, approvedby) ").append(" VALUES(?, ?, ?, ?)");

                        int status = 1;
                        String userName = "zyqunar";
                        String approvedby = (status == 1) ? userName : null;

                        Connection connection = DriverManager.getConnection("proxool.sightPoi");
                        PreparedStatement statement = null;
                        try {
                            statement = connection.prepareStatement(sqlManager.toString());

                            statement.setInt(1, newGid);
                            statement.setString(2, userName);
                            statement.setString(3, userName);
                            statement.setString(4, approvedby);

                            statement.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (statement != null) {
                                    statement.close();
                                }
                                if (connection != null) {
                                    connection.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                startIndex = startIndex + batchNum;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error poit:" + id);
                break;
            }
        }
    }

    public static void importCityPoi() throws Exception {
        String[][] result = DataSet.query("cityPoi",
                "select city_name, lat, lng from city_info");
        for (String[] item : result) {
            String cityName = item[0];
            String lat = item[1];
            String lng = item[2];

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO poi_item(city_name, name, latlng) ").append("  VALUES(?, ?, ST_GeographyFromText(?))");

            DataSet.update("sightPoi", sql.toString(), new String[]{cityName,
                        cityName, "SRID=4326;POINT(" + lng + " " + lat + ")"});

            int newGid = 0;
            String[][] row = DataSet.query(
                    "sightPoi",
                    "SELECT gid FROM poi_item WHERE city_name=? AND name=? AND type=1 LIMIT 1",
                    new String[]{cityName, cityName});
            if (row != null && row.length == 1) {
                newGid = Integer.valueOf(row[0][0]);
            }
            if (newGid <= 0) {
                throw new Exception("Insert item error, can not get gid.");
            }

            StringBuilder sqlManager = new StringBuilder();
            sqlManager.append("INSERT INTO poi_item_manager(gid, createdby, lastmodifyby, approvedby) ").append(" VALUES(?, ?, ?, ?)");

            int status = 1;
            String userName = "zyqunar";
            String approvedby = (status == 1) ? userName : null;

            Connection connection = DriverManager.getConnection("proxool.sightPoi");
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sqlManager.toString());

                statement.setInt(1, newGid);
                statement.setString(2, userName);
                statement.setString(3, userName);
                statement.setString(4, approvedby);

                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
