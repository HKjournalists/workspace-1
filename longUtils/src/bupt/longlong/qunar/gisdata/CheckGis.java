/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bupt.longlong.qunar.gisdata;

import bupt.longlong.utils.Input;
import bupt.longlong.utils.Output;
import bupt.longlong.utils.thread.FunctionThreadPool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oulong
 */
public class CheckGis {

    private static boolean isInRegion(Point LeftUp, Point RightDown, Point needToCheck) {
        Double latitude = Double.parseDouble(needToCheck.getLatitude());
        Double longitude = Double.parseDouble(needToCheck.getLongitude());
        Double latitudeLeft = Double.parseDouble(LeftUp.getLatitude());
        Double longitudeLeft = Double.parseDouble(LeftUp.getLongitude());
        Double latitudeRight = Double.parseDouble(RightDown.getLatitude());
        Double longitudeRight = Double.parseDouble(RightDown.getLongitude());
        if (latitude <= latitudeLeft && latitude >= latitudeRight && longitude <= longitudeRight && longitude >= longitudeLeft) {
            return true;
        } else {
            return false;
        }
    }

    //不靠谱
    public static boolean isInYangPu(Point needToCheck) {
        Point LeftUp = new Point("31.2929091004544", "121.51358190984173");
        Point RightDown = new Point("31.285115945657083", "121.5244502205069");

        return isInRegion(LeftUp, RightDown, needToCheck);
    }

    public static boolean isLeft(String addr, String left) {
        if (addr.contains(left)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRight(String addr, String right) {
        if (addr.contains(right)) {
            return true;
        } else {
            return false;
        }
    }

    public static void Check() throws IOException {
        List<String> list = new ArrayList<String>();
        Input.readTxt(filepath, "utf-8", list);

        List<String> nameList = new ArrayList<String>();
        List<String> latlngList = new ArrayList<String>();
        List<String> leftList = new ArrayList<String>();
        List<String> rightList = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            String[] temp = list.get(i).split("\\|");
            try {
                nameList.add(temp[0]);
                latlngList.add(temp[3]);
                leftList.add(temp[2]);
                rightList.add(temp[5]);
            } catch (Exception e) {
                System.out.println(list.get(i));
            }
        }

        FunctionThreadPool pool = new FunctionThreadPool(nameList);
        pool.execute();
    }

    public static void Lost() throws IOException {
        List<String> list = new ArrayList<String>();
        Input.readTxt(filepath, "utf-8", list);

        List<String> latlngList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String[] temp = list.get(i).split("\\|");
            try {
                latlngList.add(temp[3]);
            } catch (Exception e) {
                System.out.println(list.get(i));
            }
        }
        System.out.println(latlngList.size());

        List<String> exsitlist = new ArrayList<String>();
        Input.readTxt(distfilepath, "utf-8", exsitlist);
        System.out.println(exsitlist.size());
        Input.readTxt(short_namefilepath, "utf-8", exsitlist);
        System.out.println(exsitlist.size());
        Input.readTxt(otherfilepath, "utf-8", exsitlist);
        System.out.println(exsitlist.size());

        for (String s : exsitlist) {
            String[] temp = s.split("\t");
            if (!latlngList.contains(s) && temp.length == 1) {
                Output.appendString(lostfilepath, s);
            }
        }
    }

    public static void main(String[] args) throws IOException {

        CheckGis.Lost();
    }
    public static final String filepath = "H:/Java work/massdata.txt";
    public static final String distfilepath = "H:/Java work/distright.txt";
    public static final String short_namefilepath = "H:/Java work/short_nameright.txt";
    public static final String otherfilepath = "H:/Java work/other.txt";
    public static final String lostfilepath = "H:/Java work/lost.txt";
}
