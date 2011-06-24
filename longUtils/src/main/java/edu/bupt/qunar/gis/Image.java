/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.qunar.gis;

/**
 *@param uri    对应图片存储的位置(网络url或者本地文件全路径) 它标志图片的唯一性
 *
 * @author oulong
 */
public class Image {

    private String name;
    private String uri;
    private int width;
    private int height;

    public Image() {
    }

    public Image(String name, String uri, int width, int height) {
        this.name = name;
        this.uri = uri;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "Image[" + "name=" + name + " uri=" + uri + " width=" + width + " height=" + height + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Image other = (Image) obj;
        if ((this.uri == null) ? (other.uri != null) : !this.uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        return hash;
    }
}
