/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.qunar.qunarspider.utils;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author oulong
 */
public class WrapperTest {

    public WrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    @Test
    public void testHeadAndTailOfTravel() {
        String[] headAndTail = new String[2];
        String[] date = new Wrapper().headAndTailOfTravel(headAndTail);
        System.out.println(Arrays.toString(date));
    }

    @Test
    public void testGetDateUnits(){
        List<String> units= new Wrapper().getDateUnits("11/21   12/7、29");
        System.out.println(units);
    }
}
