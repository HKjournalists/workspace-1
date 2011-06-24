package edu.bupt.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StringUtil {

    /**
     * 转换HTML中的 '&lt;' '&gt;' '"' '&amp;'
     */
    public static String convertHTML(String input) {

        StringBuilder filtered = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    filtered.append("&lt;");
                    break;
                case '>':
                    filtered.append("&gt;");
                    break;
                case '"':
                    filtered.append("&quot;");
                    break;
                case '&':
                    filtered.append("&amp;");
                    break;
                default:
                    filtered.append(c);
            }
        }
        return (filtered.toString());
    }

    /**
     * 转换HTML中的空格，Tab，换行
     */
    public static String convertHTMLContent(String input) {

        input = input.replaceAll("  ", " &nbsp;");
        input = input.replaceAll("\t", " &nbsp; &nbsp;");

        StringBuilder filtered = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\r':
                    break;
                case '\n':
                    filtered.append("<br />");
                    break;
                default:
                    filtered.append(c);
            }
        }
        return (filtered.toString());
    }

    public static String replaceSpecialChar(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("＆", "&");
    }

    /**
     * 将文本转换成相应的HTML
     */
    public static String convertTextToHTML(String input) {

        StringBuilder filtered = new StringBuilder(input.length());

        for (int i = 0; i < input.length() - 6; i++) {
            String c = input.substring(i, i + 6);
            if (c.equals("<br />")) {
                filtered.append("\n");
            } else if (c.equals("&nbsp;")) {
                filtered.append(" ");
            } else {
                filtered.append(c);
            }

        }
        return (filtered.toString());
    }

    public static String convertString(String input) {

        StringBuilder filtered = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\'':
                    filtered.append("\\'");
                    break;
                case '\n':
                    filtered.append("\\n");
                    break;
                case '\r':
                    filtered.append("\\r");
                    break;
                default:
                    filtered.append(c);
            }
        }
        return (filtered.toString());
    }

    public static String fillString(String str, ArrayList<String> args) {
        if (args == null || args.isEmpty()) {
            return str;
        }

        if (str == null || str.length() == 0) {
            return str;
        }

        int startPos = 0, endPos = 0;
        String res = "";
        int i = 0;
        while ((endPos = str.indexOf("$$", startPos)) != -1) {
            res += str.substring(startPos, endPos) + args.get(i);
            startPos = endPos + 2;
            i++;
        }
        res += str.substring(startPos);

        return res;
    }

    public static String normalizeTitle(String title) {
        title = title.replaceAll("<|《|>|》|●", " ");
        title = title.replace("(", "（");
        title = title.replace(")", "）");
        title = title.replace("*", "");
        title = title.replace("１", "1").replace("０", "0").replace("２", "2").replace("３", "3").replace("４", "4").replace("５", "5").replace("６", "6").replace("７", "7").replace("８", "8").replace("９", "9");
        title = title.replaceAll("\\s|\u00A0|\u3000|&nbsp;", "");

        return title;
    }

    public static String trim(String raw) {
        raw = raw.replaceAll("^[\\s\u00A0\u3000]*|[\\s\u00A0\u3000]*$", "");
        return raw;
    }

    public static String normalizeFeature(String feature) {
        feature = feature.replaceAll("\\s|\u00A0|\u3000|&nbsp;", "");
        return feature;
    }

    public static String normalizePrice(String price) {
        String regex = "([\\d\\.]+)";
        Matcher matcher = Pattern.compile(regex).matcher(price);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return price;
        }
    }

    public static String normalizeCJKSpace(String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("\u00A0|\u3000|&?nbsp;?", " ");
    }
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin) {
        String resultString = null;

        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
        }
        return resultString;
    }

    public static String normalizeDayTitle(String dayTitle) {
        dayTitle = StringUtil.trim(dayTitle.replaceAll("[\\s\u00A0\u3000]+|(&nbsp;)+", " "));
        while (dayTitle.matches("^第.{1,3}天.*") || dayTitle.matches("^[Dd]\\d+.*") || dayTitle.matches("^[:：\\.。!！?？、]+.*")) {
            dayTitle = StringUtil.trim(dayTitle.replaceAll("^第.{1,3}天|^[Dd]\\d+|^[:：\\.。!！?？、]*", ""));
        }
        return dayTitle;
    }

    public static String divideBySignal(String source){
        source = trim(source);
        source = normalizeCJKSpace(source);
        source = source.replaceAll("[\t\\s]+", "|");
        return source;
    }

    public static void main(String[] args) {
        String s = "	274291	elong	京都	清水寺	6：00-18：00(夏季延时到18：30)";
        System.out.println("***" + divideBySignal(s) + "***");
    }

    public static void getInnerDescs(Node node, StringBuilder sb) {
        if ("STYLE".equals(node.getNodeName())) {
            return;
        }
        NodeList children = node.getChildNodes();
        if (HtmlUtil.isNewLineTag(node.getNodeName().toUpperCase())) {
            sb.append("\r\n");
        }
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("#text".equals(child.getNodeName())) {
                String content = child.getTextContent();
                if (content.replaceAll("\\s|\u00A0|\u3000|&nbsp;?", "").isEmpty()) {
                    continue;
                }
                sb.append(StringUtil.trim(content)).append(" ");
            } else {
                getInnerDescs(child, sb);
            }
        }
        if (HtmlUtil.isNewLineTag(node.getNodeName().toUpperCase())) {
            sb.append("\r\n");
        }

    }

    public static List<String> getDescs(Node node) {
        if (node == null) {
            return new ArrayList();
        }
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<String>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("#text".equals(child.getNodeName())) {
                String content = child.getTextContent();
                if (content.replaceAll("\\s|\u00A0|\u3000|&nbsp;?", "").isEmpty()) {
                    continue;
                }
                sb.append(StringUtil.trim(content)).append(" ");
            } else {
                getInnerDescs(child, sb);
            }
        }
        String html = sb.toString();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new StringReader(html));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                result.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    public static String divideBySignal(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String temp = args[i];
            temp = trim(temp);
            if (temp.isEmpty()) {
                temp = "Empty";
            }
            if (i == args.length - 1) {
                builder.append(temp);
            } else {
                builder.append(temp).append("|");
            }
        }
        return builder.toString();
    }

    public static String divideBySignal(List<String> list) {
        String[] temp = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = list.get(i);
        }
        return divideBySignal(temp);
    }
}
