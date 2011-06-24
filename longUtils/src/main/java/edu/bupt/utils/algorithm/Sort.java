/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils.algorithm;

/**
 *
 * @author oulong
 */
public class Sort {

    /**
     * 插入法排序(同前面的比)
     */
    public static void InsertSort(int[] args) {
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < i; j++) {
                if (args[j] > args[i]) {
                    int key = args[i];
                    args[i] = args[j];
                    args[j] = key;
                }
            }
        }
    }

    /**
     * 冒泡法排序（同后面的比,与插入实质上一样的原理）
     */
    public static void BubbleSort(int[] args) {
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[j] < args[i]) {
                    int key = args[i];
                    args[i] = args[j];
                    args[j] = key;
                }
            }
        }
    }

    public static void main(String... args){
    }
}