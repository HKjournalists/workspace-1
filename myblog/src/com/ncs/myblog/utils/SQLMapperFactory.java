/**
 * 
 */
package com.ncs.myblog.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * @author zhangyong µ¥ÀýÄ£Ê½
 */
public class SQLMapperFactory {

	private static final SQLMapperFactory instance = new SQLMapperFactory();

	private final SqlSessionFactory sqlMapper;

	private SQLMapperFactory() {
		try {
			Properties properties = new Properties();
			properties.setProperty("url", "jdbc:mysql://localhost:3306/myblog?useUnicode=true&characterEncoding=utf8");
			Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
			sqlMapper = new SqlSessionFactoryBuilder().build(reader, properties);
		} catch (IOException e) {
			throw new RuntimeException("Initialize sqlmapper failed", e);
		}
	}

	public static final SQLMapperFactory getInstance() {
		return instance;
	}

	public final SqlSessionFactory getSqlMapper() {
		return sqlMapper;
	}

}
