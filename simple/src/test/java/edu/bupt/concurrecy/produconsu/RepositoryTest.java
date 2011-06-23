/**
 * RepositoryTest.java
 */
package edu.bupt.concurrecy.produconsu;

import org.junit.Test;

import edu.bupt.concurrency.produconsu.Consumer;
import edu.bupt.concurrency.produconsu.Producer;
import edu.bupt.concurrency.produconsu.Repository;



/**
 * @author long.ou 2011-6-14 ÏÂÎç03:26:19
 *
 */
public class RepositoryTest {
	@Test
	public void testProduConsu(){
		Repository repository = new Repository(30); 
         Consumer c1 = new Consumer(50, repository); 
         Consumer c2 = new Consumer(20, repository); 
         Consumer c3 = new Consumer(30, repository); 
         Producer p1 = new Producer(10, repository); 
         Producer p2 = new Producer(10, repository); 
         Producer p3 = new Producer(10, repository); 
         Producer p4 = new Producer(10, repository); 
         Producer p5 = new Producer(10, repository); 
         Producer p6 = new Producer(10, repository); 
         Producer p7 = new Producer(80, repository); 

         c1.start(); 
         c2.start(); 
         c3.start(); 
         p1.start(); 
         p2.start(); 
         p3.start(); 
         p4.start(); 
         p5.start(); 
         p6.start(); 
         p7.start(); 
	}
}
