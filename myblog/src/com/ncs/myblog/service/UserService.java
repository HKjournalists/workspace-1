package com.ncs.myblog.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.ncs.myblog.dao.UserMapper;
import com.ncs.myblog.dao.UserMapperImpl;
import com.ncs.myblog.domain.User;
import com.ncs.myblog.utils.SQLMapperFactory;

public class UserService {

	private static Logger LOGGER = Logger.getLogger(UserService.class);

	/**
	 * 创建用户
	 * 
	 * @param user
	 * @return
	 */
	public User createUser(User user) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		UserMapper userMapper = new UserMapperImpl(session);
		try {
			userMapper.save(user);
			session.commit();
		} catch (Exception e) {
			LOGGER.error("用户创建失败，事务回滚。");
			session.rollback();
		}
		return user;
	}

	/**
	 * 根据用户ID获得用户对象
	 * 
	 * @param id
	 * @return
	 */
	public User getUserById(final Long id) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		UserMapper userMapper = new UserMapperImpl(session);
		return userMapper.getById(id);
	}

	/**
	 * 根据用户username获得用户对象
	 * 
	 * @param name
	 * @return
	 */
	public User getUserByName(final String name) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		UserMapper userMapper = new UserMapperImpl(session);
		return userMapper.getByName(name);
	}

	/**
	 * 获取全部用户
	 * 
	 * @return
	 */
	public List<User> getAllUsers() {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		UserMapper userMapper = new UserMapperImpl(session);
		return userMapper.getAll();
	}

	/**
	 * 判断使用name的用户是否已注册
	 * 
	 * @return boolean
	 */
	public boolean userExists(String name) {
		User user = getUserByName(name);
		if (user == null)
			return false;
		else {
			return true;
		}
	}

	public static void main(String[] args) {
		UserService service = new UserService();
		User user = service.getUserByName("admin");
		System.out.println(user.getPassword());
	}
}
