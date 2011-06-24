/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.bupt.longlong.qunar.database;

import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 *
 * @author oulong
 */
public class DbOperator {

    private static Logger logger = Logger.getLogger(DbOperator.class);

    public static void init(String dbConfName) {
        Reader reader = new InputStreamReader(DbOperator.class.getResourceAsStream("/" + dbConfName));
        try {
            JAXPConfigurator.configure(reader, false);
        } catch (ProxoolException e) {
            logger.warn("数据库初始化出错。");
            System.out.println(e.getCause());
            logger.warn(e.getCause());
        }
    }
}
