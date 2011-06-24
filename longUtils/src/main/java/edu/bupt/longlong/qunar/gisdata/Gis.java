/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.qunar.gisdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.longlong.qunar.gis.PNode;
import edu.bupt.longlong.qunar.gis.POIService;
import edu.bupt.longlong.utils.IoTool;
import edu.bupt.longlong.utils.Output;
import edu.bupt.longlong.utils.StringUtil;
import edu.bupt.longlong.utils.thread.FunctionThreadPool;

/**
 *
 * @author oulong
 */
public class Gis {

    public static void getGis() {
        List<String> soure = IoTool.readMoreRows(sourefilepath, "UTF-8");

        //信息内容分开存储
        List<String> first = new ArrayList<String>();
        List<String> second = new ArrayList<String>();
        for (String s : soure) {
            String[] temp = s.split(",");
            if (temp.length > 2) {
                first.add(temp[2]);
                second.add(temp[0]);
            } else {
                System.out.println(s);
            }
        }

        //导入线程池
        FunctionThreadPool pool = new FunctionThreadPool(soure);
        pool.execute();
    }

    public static void dealpNode(String name, String addr) {
        PNode pNode = POIService.doSearch("ditu.google.cn", name, addr);
        if (pNode != null) {
            Output.appendString(resultfilepath, StringUtil.divideBySignal(new String[]{addr, name, Double.toString(pNode.lat), Double.toString(pNode.lng)}));
        } else {
            Output.appendString(resultfilepath, StringUtil.divideBySignal(new String[]{addr, name, "NOT get lat and lng"}));
        }
    }
    public static final String sourefilepath = "H:/Java work/canguan.txt";
    public static final String resultfilepath = "H:/Java work/result.txt";

    public static void main(String[] args) {
        Gis.getGis();
//        dealpNode("卢湾区 世博园D片区日本产业馆", "上海");
//        shit();
    }

    public static void shit() {
        List<String> list = IoTool.readMoreRows("H:/Java work/shit.TXT", "UTF-8");
        Map<Integer, String> hh = new HashMap<Integer, String>();
        for (int i = 0; i < list.size(); i++) {
            if (i != 3) {
                continue;
            }
            Matcher matcher = Pattern.compile("\\[\".\",\"(.*?)\",\"(\\d+)\",\"(.*?)\"\\]").matcher(list.get(i));
            while (matcher.find()) {
                hh.put(Integer.parseInt(matcher.group(2)), matcher.group(1));
            }
            for (int key : hh.keySet()) {

                if (toDomesticMap.containsKey(key)) {
                    hh.put(key, "shit");
                }
                if (toOutBoundMap.containsKey(key)) {
                    hh.put(key, "shit");
                }
            }
            for (int key : hh.keySet()) {
                if(!hh.get(key).equals("shit"))
                System.out.println("toDomesticMap.put("+key + ",\""+ hh.get(key)+"\");");
            }
        }
    }
    final static Map<Integer, String> toDomesticMap = new HashMap<Integer, String>();

    static {
        toDomesticMap.put(309, "安吉");
        toDomesticMap.put(3481, "北京");
        toDomesticMap.put(521, "北京郊区");
        toDomesticMap.put(681, "北海");
        toDomesticMap.put(722, "从化");
        toDomesticMap.put(364, "常州");
        toDomesticMap.put(1721, "成都");
        toDomesticMap.put(50, "东莞");
        toDomesticMap.put(523, "德庆");
        toDomesticMap.put(3281, "都江堰");
        toDomesticMap.put(440, "峨眉山");
        toDomesticMap.put(526, "恩平");
        toDomesticMap.put(3461, "广州");
        toDomesticMap.put(41, "桂林");
        toDomesticMap.put(662, "贵阳");
        toDomesticMap.put(62, "惠东");
        toDomesticMap.put(529, "惠州");
        toDomesticMap.put(3462, "杭州");
        toDomesticMap.put(330, "横店");
        toDomesticMap.put(530, "河源");
        toDomesticMap.put(2662, "海螺沟");
        toDomesticMap.put(44, "黄山");
        toDomesticMap.put(305, "建德");
        toDomesticMap.put(2221, "景德镇");
        toDomesticMap.put(741, "江门");
        toDomesticMap.put(40, "昆明");
        toDomesticMap.put(301, "临安");
        toDomesticMap.put(39, "丽江");
        toDomesticMap.put(328, "溧阳天目湖");
        toDomesticMap.put(1461, "连云港");
        toDomesticMap.put(33, "南京");
        toDomesticMap.put(2161, "南沙");
        toDomesticMap.put(303, "宁海");
        toDomesticMap.put(1142, "平遥");
        toDomesticMap.put(308, "普陀山");
        toDomesticMap.put(64, "番禺");
        toDomesticMap.put(304, "千岛湖");
        toDomesticMap.put(81, "清远");
        toDomesticMap.put(1221, "衢州");
        toDomesticMap.put(8, "三亚");
        toDomesticMap.put(3501, "上海");
        toDomesticMap.put(329, "上海郊区");
        toDomesticMap.put(3441, "深圳");
        toDomesticMap.put(324, "苏州");
        toDomesticMap.put(57, "顺德");
        toDomesticMap.put(362, "桐庐");
        toDomesticMap.put(326, "汤山");
        toDomesticMap.put(313, "乌镇");
        toDomesticMap.put(2401, "婺源");
        toDomesticMap.put(325, "无锡");
        toDomesticMap.put(43, "武夷山");
        toDomesticMap.put(45, "武汉");
        toDomesticMap.put(302, "仙居");
        toDomesticMap.put(36, "厦门");
        toDomesticMap.put(742, "新会");
        toDomesticMap.put(317, "新昌");
        toDomesticMap.put(921, "亚布力");
        toDomesticMap.put(42, "阳朔");
        toDomesticMap.put(306, "雁荡山");
        toDomesticMap.put(321, "周庄");
        toDomesticMap.put(21, "增城");
        toDomesticMap.put(9, "珠海");
        toDomesticMap.put(84, "肇庆");
    }
    final static Map<Integer, String> toOutBoundMap = new HashMap<Integer, String>();

    static {
        toOutBoundMap.put(2081, "奥克兰");
        toOutBoundMap.put(2061, "安卡拉");
        toOutBoundMap.put(571, "澳大利亚");
        toOutBoundMap.put(63, "澳门");
        toOutBoundMap.put(579, "阿联酋");
        toOutBoundMap.put(13, "巴厘岛");
        toOutBoundMap.put(2641, "巴黎");
        toOutBoundMap.put(533, "柏林");
        toOutBoundMap.put(661, "东京");
        toOutBoundMap.put(28, "金边");
        toOutBoundMap.put(3382, "马尔代夫");
        toOutBoundMap.put(26, "苏梅岛");
        toOutBoundMap.put(3381, "泰国");
        toOutBoundMap.put(583, "智利");
        toOutBoundMap.put(4041, "大溪地");
        toOutBoundMap.put(261, "迪拜");
        toOutBoundMap.put(830, "福冈");
        toOutBoundMap.put(589, "菲律宾");
        toOutBoundMap.put(669, "釜山");
        toOutBoundMap.put(2801, "华沙");
        toOutBoundMap.put(23, "胡志明");
        toOutBoundMap.put(575, "韩国");
        toOutBoundMap.put(421, "加德满都");
        toOutBoundMap.put(221, "吉隆坡");
        toOutBoundMap.put(587, "柬埔寨");
        toOutBoundMap.put(603, "济州岛");
        toOutBoundMap.put(1483, "开普敦");
        toOutBoundMap.put(181, "科伦坡");
        toOutBoundMap.put(1521, "伦敦");
        toOutBoundMap.put(663, "兰卡威");
        toOutBoundMap.put(1801, "罗马");
        toOutBoundMap.put(582, "老挝");
        toOutBoundMap.put(7, "曼谷");
        toOutBoundMap.put(585, "毛里求斯");
        toOutBoundMap.put(2621, "米兰");
        toOutBoundMap.put(580, "缅甸");
        toOutBoundMap.put(1942, "莫斯科");
        toOutBoundMap.put(58, "马尼拉");
        toOutBoundMap.put(2841, "马德里");
        toOutBoundMap.put(106, "马来西亚");
        toOutBoundMap.put(161, "马累");
        toOutBoundMap.put(2481, "南迪");
        toOutBoundMap.put(588, "尼泊尔");
        toOutBoundMap.put(34, "普吉岛");
        toOutBoundMap.put(576, "日本");
        toOutBoundMap.put(541, "塞班岛");
        toOutBoundMap.put(105, "沙巴");
        toOutBoundMap.put(601, "首尔");
        toOutBoundMap.put(3262, "台北");
        toOutBoundMap.put(29, "吴哥");
        toOutBoundMap.put(12, "汶莱");
        toOutBoundMap.put(424, "悉尼");
        toOutBoundMap.put(17, "新加坡");
        toOutBoundMap.put(435, "新德里");
        toOutBoundMap.put(2881, "新西兰");
        toOutBoundMap.put(10, "香港");
        toOutBoundMap.put(577, "印度");
        toOutBoundMap.put(578, "印度尼西亚");
        toOutBoundMap.put(581, "越南");

        toDomesticMap.put(544,"德国");
		toDomesticMap.put(564,"意大利");
		toDomesticMap.put(574,"加拿大");
		toDomesticMap.put(570,"英国");
		toDomesticMap.put(569,"瑞典");
		toDomesticMap.put(1881,"凯恩斯");
		toDomesticMap.put(586,"瑞士");
		toDomesticMap.put(2281,"旧金山");
		toDomesticMap.put(3702,"阿德莱德");
		toDomesticMap.put(1484,"内罗毕");
		toDomesticMap.put(1481,"约翰内斯堡");
		toDomesticMap.put(665,"槟城");
		toDomesticMap.put(428,"墨尔本");
		toDomesticMap.put(4242,"平壤");
		toDomesticMap.put(1941,"布鲁塞尔");
		toDomesticMap.put(1921,"开罗");
		toDomesticMap.put(2905,"基督城");
		toDomesticMap.put(2904,"皇后镇");
    }
}
