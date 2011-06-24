/**
 * 
 */
package com.ncs.myblog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.ncs.myblog.domain.User;

/**
 * @author zhangyong
 *
 */
public class UserMapperImpl implements UserMapper {
	
	final String NAMESPACE = "com.ncs.myblog.dao.UserMapper";

	private SqlSession session;
	
	public UserMapperImpl(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public void save(User user) {
		session.insert(NAMESPACE + ".insert", user);
	}

	@Override
	public User getById(Long id) {
		return (User) session.selectOne(NAMESPACE + ".getById", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAll() {
		return session.selectList(NAMESPACE +  ".selectAll");
	}

	@Override
	public User getByName(String name) {
		return (User) session.selectOne(NAMESPACE + ".getByName", name);
	}
}
