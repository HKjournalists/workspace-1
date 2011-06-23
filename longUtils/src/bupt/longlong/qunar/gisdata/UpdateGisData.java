/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bupt.longlong.qunar.gisdata;

import bupt.longlong.qunar.database.DataSet;
import bupt.longlong.qunar.database.DbOperator;
import bupt.longlong.utils.Input;
import bupt.longlong.utils.IoTool;
import bupt.longlong.utils.MyClient;
import bupt.longlong.utils.NekoHtmlParser;
import bupt.longlong.utils.Output;
import bupt.longlong.utils.StringUtil;
import bupt.longlong.utils.TraditionalandSimple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author oulong
 */
public class UpdateGisData {

    public static void updateTimezone() throws IOException {
        List<String> list = new ArrayList<String>();
        Input.readTxt(countrypath, "gbk", list);

        List<String> list2 = new ArrayList<String>();
        Input.readTxt(countrypath, "utf-8", list2);

        for (int i = 0; i < list.size(); i++) {
            String temp = list.get(i);
            System.out.println(temp);
            for (int j = 0; j < list2.size(); j++) {
                String[] tmp = list2.get(j).split("\\|");
                System.out.println(tmp[0]);
                if (tmp[0].equals(temp)) {
                    temp = StringUtil.divideBySignal(new String[]{temp, list2.get(j)});
                } else if (tmp[0].contains(temp)) {
                    temp = StringUtil.divideBySignal(new String[]{temp, list2.get(j)});
                }
            }
            Output.appendString(powerpath, temp);
        }
    }

    public static void updateData() {
//        DbOperator.init("db.xml");
        List<String> data = IoTool.readMoreRows(csvpath, "utf-8");
        List<String> result = new ArrayList<String>();
        for (String s : data) {
            String[] temp = s.split("\\|");
            StringBuilder builder = new StringBuilder();
            if (temp.length >= 3) {
                String name = temp[0];

                for (int j = 0; j < temp.length; j++) {
                    if (j < 3) {
                    } else {
                        builder.append(name).append("|").append(temp[3]);
//                        if (j == 2 && !temp[j].equals("Empty")) {
//                            builder.append("网址：").append(temp[j]).append("\r\n");
//                        }
//                        if (j == 3 && !temp[j].equals("Empty")) {
//                            builder.append("地址：").append(temp[j]).append("\r\n");
//                        }
//                        if (j == 4 && !temp[j].equals("Empty")) {
//                            builder.append("电话：").append(temp[j]).append("\r\n");
//                        }
//                        if (j == 5 && !temp[j].equals("Empty")) {
//                            builder.append("办公时间：").append(temp[j]).append("\r\n");
//                        }
                    }
                }


//                String sql = "update Sight set visa = ? where name = ? and type = '国家'";
//                if (!message.isEmpty() && !message.equals("")) {
//                    boolean b = DataSet.update("sight213backend", sql, new String[]{message, name});
//                    if (!b) {
//                        System.out.println(name);
//                        System.out.println(message);
//                    }
//                }
            } else {
                builder.append(s);
            }
            String message = builder.toString();
            result.add(message);
        }
        IoTool.writeMultiRows(shape, result, true);
    }

    public static void updateEmbassy() throws IOException {
        List<String> list = new ArrayList<String>();
        Input.readTxt(csvpath, "utf-8", list);
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            Matcher matcher = Pattern.compile("\"(.*?)\"").matcher(s);
            while (matcher.find()) {
                String tmp = matcher.group(1);
                tmp = tmp.replaceAll(",", "，");
                s = s.replace(matcher.group(1), tmp);
            }
            s = s.replaceAll(",,", ", ,").replaceAll(",,", ", ,");
            if (s.endsWith(",")) {
                s = s + " ";
            }
            s = s.replaceAll("\"", "");
            String[] temp = s.split(",");
            System.out.println(temp.length);
            if (temp[8].startsWith("办公时间")) {
                temp[8] = temp[8].replaceFirst("办公时间", "");
            }
            Output.appendString(embassypath, StringUtil.divideBySignal(new String[]{temp[3], temp[5], temp[6], temp[7], temp[8]}));
        }
    }

    public static void processOpenTime() throws IOException {
        List<String> list = new ArrayList<String>();
        Input.readTxt(frompath, "utf-8", list);

        List<String> alist = new ArrayList<String>();
        Set<String> b = new HashSet<String>();

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (!s.isEmpty()) {
                String[] temp = s.split("\\|");
//               b.add(temp[2]+"|"+temp[3]);

                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < temp.length; j++) {
                    if (j < 3) {
                        builder.append(temp[j]).append("|");
                    } else {
                        builder.append(temp[j]).append("\t");
                    }
                }
                alist.add(builder.toString());
            }
        }

        //sohu elong ctrip
//        for(String s : b){
//            String[] temp = new String[3];
//            for(int i = 0;i<list.size();i++){
//                String more = list.get(i);
//                if(more.contains(s)){
//                    if(more.contains("sohu"))temp[0] = more;
//                    else if(more.contains("elong"))temp[1] = more;
//                    else if(more.contains("ctrip"))temp[2] = more;
//                }
//            }
//            if(temp[0]!=null)alist.add(temp[0]);
//            else if(temp[1]!=null)alist.add(temp[1]);
//            else if(temp[2]!=null)alist.add(temp[2]);
//        }

        IoTool.writeMultiRows(filepath, alist, true);
    }

    public static void main(String[] args) throws IOException {
//        UpdateGisData.updateTimezone();
        UpdateGisData.updateData();
//        UpdateGisData.updateEmbassy();
//        sb();
//        processOpenTime();
    }
    public static final String filepath = "H:/Java work/fare.txt";
    public static final String frompath = "H:/Java work/embassy(审核).txt";
    public static final String shape = "H:/Java work/shape.txt";
    public static final String powerpath = "H:/Java work/tt.txt";
    public static final String csvpath = "H:/Java work/power.txt";
    public static final String embassypath = "H:/Java work/embassy_new.txt";
    public static final String countrypath = "H:/Java work/countryname.txt";

    public static void parseReferenceHtml(String url, String charset, String xpath) {
        MyClient client = new MyClient();
        String html = client.postMethodHtml(url, charset).getHtml();

        NekoHtmlParser parser = new NekoHtmlParser();
        parser.load(html, charset);

        NodeList nodes = parser.selectNodes(xpath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String s = node.getTextContent();
            s = StringUtil.trim(s);
            s = TraditionalandSimple.complTosimple(s);
            if (!s.isEmpty() && !s.equals("地区") && !s.equals("电气参数")) {
                if (i % 2 != 0) {
                    s += "\n";
                } else {
                    s += "|";
                }
                System.out.println(s);
                IoTool.write(filepath, s, true);
            }
        }
    }
    private static final String baseurl = "http://www.chazuo.com/Plugs/%s-%d.html";
    private static String[] continent = new String[]{"Asia", "Europe", "NorthAmerica", "SouthAmerica", "Africa", "Oceania"};

    public static int acquirePageNum(String url, String charset, String xpath) {
        int result = 1;

        MyClient client = new MyClient();
        String html = client.postMethodHtml(url, charset).getHtml();

        NekoHtmlParser parser = new NekoHtmlParser();
        parser.load(html, charset);

        Node node = parser.selectSingleNode(xpath);
        if (node != null) {
            String s = node.getTextContent();
            result = Integer.parseInt(s);
        }
        return result;
    }

    public static void sb() {
        for (int i = 0; i < continent.length; i++) {
            String url = String.format(baseurl, continent[i], 1);
            int num = acquirePageNum(url, "gb2312", "//DIV[@id='pagenav']/A[last()]/preceding-sibling::A[1]");
            for (int j = 0; j < num; j++) {
                url = String.format(baseurl, continent[i], j + 1);
                parseReferenceHtml(url, "gb2312", "//DIV[@id='html_list']//LI[@class='w2']|//DIV[@id='html_list']//LI[@class='w4']");
            }
        }
    }
}
