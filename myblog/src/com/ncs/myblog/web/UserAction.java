/**
 * 
 */
package com.ncs.myblog.web;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.ncs.myblog.domain.User;
import com.ncs.myblog.service.UserService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author zhangyong
 * 
 */
public class UserAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -6940104507311135293L;

	private UserService userService = new UserService();

	private List<User> userList;

	private User user;

	private String message;

	private Map<String, Object> session;

	//	public String execute() {
	//		return SUCCESS;
	//	}

	public String admin() {
		String name = (String) session.get("username");
		user = userService.getUserByName(name);
		if (user != null && user.getIsAdmin() > 0)
			return SUCCESS;
		else {
			return ERROR;
		}
	}

	public String renderToCreateUser() {
		return SUCCESS;
	}

	public String createUser() {
		boolean create = true;
		if (getUser().getPassword().length() == 0) {
			addFieldError("user.password", getText("password.required"));
			create = false;
		}
		if (getUser().getUsername().length() == 0) {
			addFieldError("user.username", getText("username.required"));
			create = false;
		}
		/* Make sure user doesn't already have an account */
		if (userService.userExists(getUser().getUsername())) {
			addFieldError("user.username", getText("user.exists"));
			create = false;
		}
		if (create) {
			userService.createUser(user);
			return SUCCESS;
		} else {
			return INPUT;
		}
	}

	public String userList() {
		userList = userService.getAllUsers();
		return SUCCESS;
	}

	public String logOnUser() {
		return SUCCESS;
	}

	public String logResult() {
		boolean isRegister = false;
		//用户身份验证
		String passwd = user.getPassword();
		user = userService.getUserByName(user.getUsername());
		if (user != null && user.getPassword().equals(passwd)) isRegister = true;
		//session中注入用户名
		if (isRegister) {
			session.put("username", user.getUsername());
			return SUCCESS;
		} else {
			addFieldError("user.username", getText("logerror"));
			return INPUT;
		}
	}

	public void validate() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
