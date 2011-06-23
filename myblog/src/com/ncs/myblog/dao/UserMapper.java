/**
 * 
 */
package com.ncs.myblog.dao;

import java.util.List;

import com.ncs.myblog.domain.User;

/**
 * @author zhangyong
 * 
 */
public interface UserMapper {

	void save(User user);

	User getById(Long id);

	User getByName(String name);

	List<User> getAll();
}
