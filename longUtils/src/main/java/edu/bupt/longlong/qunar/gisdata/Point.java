/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.qunar.gisdata;

/**
 *
 * @author oulong
 */
public class Point {
    //维度、经度

    private String latitude;
    private String longitude;

    public Point(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
