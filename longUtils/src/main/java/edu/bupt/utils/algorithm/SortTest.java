/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils.algorithm;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author oulong
 */
public class SortTest {

    public SortTest() {
    }

    @Before
    public void setUp() {
         nums = new int[]{2, 4, 3, 1, 5};
    }

    @After
    public void tearDown() {
    }

    // add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testInsertSort() {
        Sort.InsertSort(nums);
        System.out.println(Arrays.toString(nums));
    }

    @Test
    public void testBubbleSort() {
        Sort.BubbleSort(nums);
        System.out.println(Arrays.toString(nums));
    }

    private int[] nums;
}
