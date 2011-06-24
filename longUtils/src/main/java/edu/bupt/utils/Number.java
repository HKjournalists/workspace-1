/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils;

import java.math.BigDecimal;

/**
 *
 * @author oulong
 */
public class Number {

    /**
     * roundingMode BigDecimal.ROUND_HALF_UP 四舍五入
     *              ROUND_CEILING 天花板
     *              ROUND_FLOOR 地板
     *              ROUND_DOWN 直接截取
     */
    public static double decimal(double count, int number, int roundingMode) {
        double d = 0.0;
        BigDecimal bd = new BigDecimal(count);
        bd = bd.setScale(number, roundingMode);
        d = Double.parseDouble(bd.toString());
        return d;
    }

    public static void main(String[] args){
        /*confused*/
        System.out.println(decimal(33.555, 2, BigDecimal.ROUND_HALF_UP));
        System.out.println(decimal(33.555, 2, BigDecimal.ROUND_HALF_DOWN));
    }
}
