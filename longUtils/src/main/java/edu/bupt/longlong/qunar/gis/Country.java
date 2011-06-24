/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.qunar.gis;

/**
 *
 * @author oulong
 */
public class Country {

    //中文国名，英文国名，英文简写，与中国时差，电压，插座形状，中文繁体国名 大使馆
    //chineseName,englishName,alias,timezone,power,socketShape,twName,embassy
    private String chineseName;
    private String timezone;
    private String power;
    private String socketShape;

    public Country() {
    }

    public Country(String chineseName, String timezone, String power, String socketShape) {
        this.chineseName = chineseName;
        this.timezone = timezone;
        this.power = power;
        this.socketShape = socketShape;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getPower() {
        return power;
    }

    public String getSocketShape() {
        return socketShape;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public void setSocketShape(String socketShape) {
        this.socketShape = socketShape;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Country other = (Country) obj;
        if ((this.chineseName == null) ? (other.chineseName != null) : !this.chineseName.equals(other.chineseName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.chineseName != null ? this.chineseName.hashCode() : 0);
        return hash;
    }
}
