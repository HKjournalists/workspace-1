/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author oulong
 */
public class SimilarityChineseTest {

    public SimilarityChineseTest() {
    }

    @Before
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void ComparabilityTest() {
        double d = SimilarityChinese.Comparability("玉带河", "相片玉带河");
        System.out.println(d);
    }
}
